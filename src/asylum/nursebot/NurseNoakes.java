package asylum.nursebot;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import asylum.nursebot.objects.*;
import asylum.nursebot.utils.Logger;
import org.javalite.activejdbc.InitException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.loader.ModuleLoader;
import asylum.nursebot.persistence.Connector;
import asylum.nursebot.persistence.ModelManager;
import asylum.nursebot.persistence.modules.NurseModule;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.semantics.SemanticsHandler;
import asylum.nursebot.utils.ConfigHolder;
import asylum.nursebot.utils.StringTools;

public class NurseNoakes extends TelegramLongPollingBot {

	public static final String VERSION = "3.0";
	public static final List<String> BOT_ADMIN_USERNAMES = 
			Collections.unmodifiableList(Arrays.asList("overflowerror"));
	private static final int EXIT_CODE_SHUTDOWN = 0;
	private static final int EXIT_CODE_RESTART = 1;

	private static final int EXIT_CODE_CRITICAL = 10;

	public static void main(String[] args) {
		if (Arrays.asList(args).contains("-v")) {
			System.out.println(VERSION);
			System.exit(EXIT_CODE_SHUTDOWN);
		}

		ApiContextInitializer.init();

		TelegramBotsApi botsApi = new TelegramBotsApi();

		try {
			botsApi.registerBot(new NurseNoakes());
		} catch (TelegramApiException e) {
			e.printStackTrace();
			System.exit(EXIT_CODE_CRITICAL);
		}

	}

	private CommandHandler commandHandler;
	private SemanticsHandler semanticsHandler;
	
	private Connector connector;
	
	private Set<Long> pausedChats = new HashSet<>();
	
	private Collection<Module> activeModules = new ConcurrentLinkedQueue<>();
	private Collection<Module> inactiveModules = new ConcurrentLinkedQueue<>();
	
	private long started;

	private Logger logger;
	private String loggerModule = "main";
	
	public NurseNoakes() {
		started = new Date().getTime();

		logger = Logger.getInstance();
		logger.setCrticalAction(() -> {
			System.err.println("Critical error occurred.");
			System.err.println("Shuting down.");
			System.exit(EXIT_CODE_CRITICAL);
		});

		logger.info(loggerModule, "Getting config file.");
		ConfigHolder holder = null;
		try {
			holder = ConfigHolder.getInstance();
		} catch (IOException e) {
			logger.critical(loggerModule, "Error reading config file: " + e.getMessage());
		}

		logger.debug(loggerModule, "Getting log verbosity.");
		if (holder.getLogVerbosity() != null) {
			switch (holder.getLogVerbosity().toLowerCase()) {
				case "debug":
					logger.setVerbosity(Logger.DEBUG);
					break;
				case "verbose":
					logger.setVerbosity(Logger.VERBOSE);
					break;
				case "info":
					logger.setVerbosity(Logger.INFO);
					break;
				case "warn":
					logger.setVerbosity(Logger.WARNING);
					break;
				case "error":
					logger.setVerbosity(Logger.ERROR);
					break;
				case "critical":
					logger.setVerbosity(Logger.CRITICAL);
					break;
				default:
					logger.warn(loggerModule, "Invalid log verbosity in config file.");
					break;
			}
		}

		logger.debug(loggerModule, "Getting logfile.");
		if (holder.getLogfile() != null) {
			try {
				logger.setLogfile(new File(holder.getLogfile()));
			} catch (IOException e) {
				logger.error(loggerModule, "Error while opening logfile: " + e.getMessage());
			}
		}

		try {
			logger.info(loggerModule, "Connecting to database.");
			connector = new Connector(holder.getDatabaseHost(), holder.getDatabaseSchema(), holder.getDatabaseUser(), holder.getDatabasePassword());
			logger.debug(loggerModule, "Connecting setup thread to dataase.");
			connector.connectThread(); // setup thread

			logger.verbose(loggerModule, "Building modules model.");
			ModelManager.build(NurseModule.class);
		} catch (InitException e) {
			logger.error(loggerModule, "Error while connecting to database.");
			logger.exception(e);
			logger.error(loggerModule, "Probably no instrumentation.");
			logger.critical(loggerModule, "Unable to continue.");
		}

		logger.debug(loggerModule, "Creating command handler.");
		commandHandler = new CommandHandler(this);
		logger.debug(loggerModule, "Creating semantic handler.");
		semanticsHandler = new SemanticsHandler(this);

		logger.debug(loggerModule, "Adding commands.");
		commandHandler.add(new CommandInterpreter(null)
				.setName("start")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.USER)
				.setLocality(Locality.USERS)
				.setAction(c -> {
					c.getSender().send(StringTools.makeBold("Hallo o/\nDieser Bot ist für Gruppen Chats gedacht, aber ein paar Funktionen sind auch hier nutzbar."), true);
				}));
		commandHandler.add(new CommandInterpreter(null)
				.setName("pause")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ADMIN)
				.setLocality(Locality.GROUPS)
				.setPausable(false)
				.setAction(c -> {
					pausedChats.add(c.getMessage().getChatId());
					c.getSender().send("- paused -");
				}));
		commandHandler.add(new CommandInterpreter(null)
				.setName("resume")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ADMIN)
				.setLocality(Locality.GROUPS)
				.setPausable(false)
				.setAction(c -> {
					pausedChats.remove(c.getMessage().getChatId());
					c.getSender().send("- resumed -");
				}));
		
		commandHandler.add(new CommandInterpreter(null)
				.setName("shutdown")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setPausable(false)
				.setAction(c -> {
					if (BOT_ADMIN_USERNAMES.contains(c.getMessage().getFrom().getUserName())) {
						c.getSender().send("Shutting down...");
						logger.info(loggerModule, "Got shutdown command.");
						shutdown();
					} else {
						c.getSender().reply("Du darfst das nicht.", c.getMessage());
					}
				}));
		
		commandHandler.add(new CommandInterpreter(null)
				.setName("reboot")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setPausable(false)
				.setAction(c -> {
					if (BOT_ADMIN_USERNAMES.contains(c.getMessage().getFrom().getUserName())) {
						c.getSender().send("Restarting...");
						logger.info(loggerModule, "Got restart command.");
						restart();
					} else {
						c.getSender().reply("Du darfst das nicht.", c.getMessage());
					}
				}));
		
		commandHandler.add(new CommandInterpreter(null)
				.setName("ping")
				.setInfo("ist der Bot noch aktiv")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setPausable(false)
				.setAction(c -> {
					c.getSender().reply(isChatPaused(c.getMessage().getChatId()) ? "Dieser Chat ist pausiert." : "pong", c.getMessage());
				}));
		
		commandHandler.add(new CommandInterpreter(null)
				.setName("version")
				.setInfo("zeigt die Version des Bots an")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setPausable(false)
				.setAction(c -> {
					c.getSender().reply("Version " + VERSION, c.getMessage());
				}));
		
		commandHandler.add(new CommandInterpreter(null)
				.setName("uptime")
				.setInfo("seit wann läuft der Bot")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setPausable(false)
				.setAction(c -> {
					long diff = new Date().getTime() - started;
					
					long s = diff / 1000;
					long m = s / 60;
					s %= 60;
					long h = m / 60;
					m %= 60;
					long d = h / 24;
					h %= 24;
					
					StringBuilder builder = new StringBuilder();
					builder.append("Dieser Bot läuft seit");
					boolean show = false;
					if (d > 0 || show) {
						builder.append(" ");
						show = true;
						builder.append(d).append(" ");
						builder.append("Tag");
						if (d != 1)
							builder.append("en");
					}
					if (h > 0 || show) {
						builder.append(" ");
						if (show && s == 0 && m == 0)
							builder.append("und ");
						show = true;
						builder.append(h).append(" ");
						builder.append("Stunde");
						if (h != 1)
							builder.append("n");
					}
					if (m > 0 || show) {
						builder.append(" ");
						if (show && s == 0)
							builder.append("und ");
						show = true;
						builder.append(m).append(" ");
						builder.append("Minute");
						if (m != 1)
							builder.append("n");
					}
					if (s > 0 || show) {
						builder.append(" ");
						if (show)
							builder.append("und ");
						show = true;
						builder.append(s).append(" ");
						builder.append("Sekunde");
						if (s != 1)
							builder.append("n");
					}
					builder.append(".");
					
					c.getSender().reply(builder.toString(), c.getMessage());
				}));
		
		commandHandler.add(new CommandInterpreter(null)
				.setName("info")
				.setInfo("zeigt Information zu diesem Bot an")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setPausable(false)
				.setAction(c -> {
					StringBuilder builder = new StringBuilder();
					
					builder.append(getBotUsername()).append(" ").append(VERSION).append("\n");
					
					builder.append("\n").append(StringTools.makeBold("Modules")).append("\n");
					for (Module module : activeModules) {
						builder.append("+ ").append(module.getName()).append(" (").append(module.getType().toString()).append(")\n");
					}
					for (Module module : inactiveModules) {
						builder.append("- ").append(module.getName()).append(" (").append(module.getType().toString()).append(")\n");
					}
					
					builder.append("\n").append(StringTools.makeBold("Commands")).append("\n");
					builder.append("There are currently ").append(commandHandler.getNumberOfEntities()).append(" commands installed.");
					builder.append("\n");
					
					builder.append("\n").append(StringTools.makeBold("Semantics")).append("\n");
					builder.append("There are currently ").append(semanticsHandler.getNumberOfEntities()).append(" semantic interpreters installed.");
					builder.append("\n");
					
					builder.append("\n").append(StringTools.makeBold("System")).append("\n");
					builder.append(StringTools.makeItalic("Operating System: ")).append(System.getProperty("os.name")).append("\n");
					builder.append(StringTools.makeItalic("Cores: ")).append(Runtime.getRuntime().availableProcessors()).append("\n");
					builder.append(StringTools.makeItalic("User: ")).append(System.getProperty("user.name")).append("\n");
					long maxMemory = Runtime.getRuntime().maxMemory();
					long freeMemory = Runtime.getRuntime().freeMemory();
					long usedMemory = maxMemory - freeMemory;
					builder.append(StringTools.makeItalic("Memory: ")).append(Math.round(((float) usedMemory) / 1024 / 1024 * 10)/10).append("/").append(Math.round(((float) maxMemory) / 1024 / 1024 * 10)/10).append(" MiB").append("\n");
					
					builder.append("\n");
					builder.append(StringTools.makeLink("Github Page", "https://github.com/overflowerror/NurseBot"));
					
					c.getSender().send(builder.toString(), true);
				}));
		
		commandHandler.add(new CommandInterpreter(null)
				.setName("modules")
				.setInfo("(de-)aktiviert Module")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ADMIN)
				.setLocality(Locality.GROUPS)
				.setPausable(false)
				.setAction(c -> {
					if (c.getParameter().length() < 2) {
						StringBuilder builder = new StringBuilder();
						
						builder.append("Synopsis: /modules [{+|-}MODUL]\n\n");
						
						builder.append("Aktuell sind folgende Module geladen:\n");
						
						for (Module module : activeModules) {
							builder.append("+ ").append(module.getName()).append(" (").append(module.getType().toString()).append(")\n");
						}
						for (Module module : inactiveModules) {
							builder.append("- ").append(module.getName()).append(" (").append(module.getType().toString()).append(")\n");
						}
						
						c.getSender().reply(builder.toString(), c.getMessage());
						return;
					}
					
					boolean activate = c.getParameter().substring(0, 1).equals("+");
					boolean deactivate = c.getParameter().substring(0, 1).equals("-");
					
					Module module = searchModule(c.getParameter().substring(1));
					
					if (module == null || !(activate ^ deactivate)) {
						c.getSender().reply("Das schaut nicht richtig aus. Hast du dich vertippt?", c.getMessage());
						return;
					}
					
					try {
						if (activate) {
							activateModule(module);
							c.getSender().send("Das Modul " + module.getName() + " wurde aktiviert.");
						} else {
							deactivateModule(module);
							c.getSender().send("Das Modul " + module.getName() + " wurde deaktiviert.");
						}
					} catch (Exception e) {
						logger.warn(loggerModule, "Module " + (activate ? "activiation" : "deactivation") + " failed.");
						logger.exception(e);
						c.getSender().reply("Der Vorgang ist fehlgeschlagen. Ist das Modul bereits " + (activate ? "aktiviert" : "deaktiviert") + "?", c.getMessage());
					}
					
				}));
		
		commandHandler.add(new CommandInterpreter(null)
				.setName("privacy")
				.setInfo("zeigt die Datenschutzerklärung an")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setPausable(false)
				.setAction(c -> {
					String text = "" +
						StringTools.makeItalic("Welche personenbezogenen Daten werden gespeichert?") + "\n" +
						"Benutzername und Benutzer-IDs werden zum Ablegen von Einstellungen in der Datenbank benutzt." + "\n\n" +
						StringTools.makeItalic("Wann werden personenbezogene Daten gespeichert?") + "\n" +
						"Personenbezogene Daten werden nur dann gespeichert, wenn sie benötigt werden." + "\n\n" +
						StringTools.makeItalic("Wofür werden personenbezogene Daten verwendet?") + "\n" +
						"Es werden nur Daten gespeichert, die für den Service direkt verwendet werden." + "\n\n" +
						StringTools.makeItalic("An wen werden personenbezogene Daten weitergegeben?") + "\n" +
						"Personenbezogene Daten werden nicht an Dritte weitergegeben." + "\n\n" +
						StringTools.makeItalic("Wann werden personenbezogene Daten wieder gelöscht?") + "\n" +
						"Daten werden gelöscht, sobald sie nicht mehr benötigt werden.";
					
						c.getSender().send(text, true);
				}));

		logger.debug(loggerModule, "Creating module loader.");
		ModuleLoader loader = new ModuleLoader(this, commandHandler, semanticsHandler);

		logger.info(loggerModule, "Loading dependencies.");
		loader.loadDependencies();
		logger.info(loggerModule, "Loading regular modules.");
		loader.loadModules(module -> loadModule(module));
			
			
		if (ModelManager.wasAnythingCreated()) {
			logger.warn(loggerModule,"Changes to database were made.");
			restart();
		}

		logger.info(loggerModule, "Activating modules.");
		List<NurseModule> registeredModules = NurseModule.findAll();
		for (NurseModule registeredModule : registeredModules) {
			Module module = searchModule(registeredModule.getName());
			if (module == null) {
				logger.warn(loggerModule, "Module " + registeredModule.getName() + " does not exist in database. Deleting...");
				registeredModule.delete();
				continue;
			}
			if (registeredModule.isActive())
				activateModule(module);
		}

		logger.debug(loggerModule, "End of setup. Disconnect from database.");
		connector.disconnectThread();
	}

	public void shutdown() {
		logger.info(loggerModule, "Shuting down...");
		for(Module module : activeModules) {
			logger.verbose(loggerModule, "Shutting down module " + module.getName() + "...");
			module.shutdown();
		}
		for(Module module : inactiveModules) {
			logger.verbose(loggerModule, "Shutting down module " + module.getName() + "...");
			module.shutdown();
		}

		logger.debug(loggerModule, "Closing database connection.");
		connector.close();

		logger.info(loggerModule, "Shutdown complete.");
		System.exit(EXIT_CODE_SHUTDOWN);
	}
	
	public void restart() {
		logger.info(loggerModule, "Restarting...");
		for(Module module : activeModules) {
			logger.verbose(loggerModule, "Shutting down module " + module.getName() + "...");
			module.shutdown();
		}
		for(Module module : inactiveModules) {
			logger.verbose(loggerModule, "Shutting down module " + module.getName() + "...");
			module.shutdown();
		}
		
		connector.close();
		
		System.exit(EXIT_CODE_RESTART);
	}

	private Module searchModule(String name) {
		for (Module module : activeModules) {
			if (module.getName().equals(name)) {
				return module;
			}
		}
		for (Module module : inactiveModules) {
			if (module.getName().equals(name)) {
				return module;
			}
		}
		return null;
	}
	
	private void loadModule(Module module) {
		module.init();
		
		inactiveModules.add(module);
		
		logger.verbose(loggerModule, "Module " + module.getName() + " loaded.");
	}
	
	private void activateModule(Module module) {
		if (!inactiveModules.remove(module))
			throw new IllegalArgumentException();
		
		activeModules.add(module);
		
		module.activate();
		
		NurseModule nm = NurseModule.byName(module.getName());
		if (nm == null) {
			nm = new NurseModule().setName(module.getName());
		}
		
		nm.setActive();
		
		nm.saveIt();
		
		logger.verbose(loggerModule, "Module " + module.getName() + " activated.");
	}
	
	private void deactivateModule(Module module) {
		if (!activeModules.remove(module))
			throw new IllegalArgumentException();
		
		inactiveModules.add(module);
		
		module.deactivate();
		
		NurseModule nm = NurseModule.byName(module.getName());
		if (nm == null) {
			nm = new NurseModule().setName(module.getName());
		}
		
		nm.setInactive();
		
		nm.saveIt();
		
		logger.verbose(loggerModule, "Module " + module.getName() + " deactivated.");
	}
	
	public boolean isActive(Module module) {
		return activeModules.contains(module);
	}
	
	@Override
	public String getBotUsername() {
		try {
			return ConfigHolder.getInstance().getTelegramUser();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpdateReceived(Update update) {
		try {
			connector.connectThread();
		
			if (update.hasMessage()) {
				
				if (update.getMessage().isCommand()) {
					commandHandler.parse(update.getMessage());
				} else {
					semanticsHandler.parse(update.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connector.disconnectThread();
		}
	}

	@Override
	public String getBotToken() {
		try {
			return ConfigHolder.getInstance().getTelegramToken();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isChatPaused(Long chatid) {
		return pausedChats.contains(chatid);
	}
}

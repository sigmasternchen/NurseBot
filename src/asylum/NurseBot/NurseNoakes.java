package asylum.NurseBot;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import asylum.NurseBot.commands.Command;
import asylum.NurseBot.commands.CommandHandler;
import asylum.NurseBot.commands.Locality;
import asylum.NurseBot.commands.Permission;
import asylum.NurseBot.commands.Visibility;
import asylum.NurseBot.modules.Appointments;
import asylum.NurseBot.modules.Eastereggs;
import asylum.NurseBot.modules.Module;
import asylum.NurseBot.modules.Straitjacket;

public class NurseNoakes extends TelegramLongPollingBot {

	public static final String USERNAME = "NurseNoakesBot";
	public static final String VERSION = "0.1";

	public static void main(String[] args) {
		ApiContextInitializer.init();

		TelegramBotsApi botsApi = new TelegramBotsApi();

		try {
			botsApi.registerBot(new NurseNoakes());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

	}

	private StringManager stringmanager;
	private CommandHandler commandHandler;
	
	private Set<Long> pausedChats = new HashSet<>();
	
	private List<Module> modules = new LinkedList<>();
	
	public NurseNoakes() {
		stringmanager = new StringManager();
		commandHandler = new CommandHandler(this);
	
		commandHandler.add(new Command()
				.setName("start")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.USER)
				.setLocality(Locality.USERS)
				.setAction(c -> {
					try {
						c.getSender().send(stringmanager.makeBold("Hallo o/\nDieser Bot ist eigentlich für Gruppen Chats gedacht, aber ein paar Funktionen sind auch hier nutzbar."), true);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}));
		commandHandler.add(new Command()
				.setName("pause")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ADMIN)
				.setLocality(Locality.GROUPS)
				.setPausable(false)
				.setAction(c -> {
					try {
						pausedChats.add(c.getMessage().getChatId());
						c.getSender().send("- paused -");
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}));
		commandHandler.add(new Command()
				.setName("resume")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ADMIN)
				.setLocality(Locality.GROUPS)
				.setPausable(false)
				.setAction(c -> {
					try {
						pausedChats.remove(c.getMessage().getChatId());
						c.getSender().send("- resumed -");
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}));
		
		commandHandler.add(new Command()
				.setName("info")
				.setInfo("zeigt Information zu diesem Bot an")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.USER)
				.setLocality(Locality.EVERYWHERE)
				.setPausable(false)
				.setAction(c -> {
					try {
						StringBuilder builder = new StringBuilder();
						
						builder.append(USERNAME).append(" ").append(VERSION).append("\n");
						
						builder.append("\n").append(stringmanager.makeBold("Modules")).append("\n");
						for (Module module : modules) {
							builder.append("- ").append(module.getName()).append("\n");
						}
						
						builder.append("\n").append(stringmanager.makeBold("Commands")).append("\n");
						builder.append("There are currently ").append(commandHandler.getNumberOfCommands()).append(" commands installed.");
						builder.append("\n");
						
						builder.append("\n").append(stringmanager.makeBold("System")).append("\n");
						builder.append(stringmanager.makeItalic("Operating System: ")).append(System.getProperty("os.name")).append("\n");
						builder.append(stringmanager.makeItalic("Cores: ")).append(Runtime.getRuntime().availableProcessors()).append("\n");
						builder.append(stringmanager.makeItalic("User: ")).append(System.getProperty("user.name")).append("\n");
						long maxMemory = Runtime.getRuntime().maxMemory();
						long freeMemory = Runtime.getRuntime().freeMemory();
						long usedMemory = maxMemory - freeMemory;
						builder.append(stringmanager.makeItalic("Memory: ")).append(Math.round(((float) usedMemory) / 1024 / 1024 * 10)/10).append("/").append(Math.round(((float) maxMemory) / 1024 / 1024 * 10)/10).append(" MiB").append("\n");
						
						
						c.getSender().send(builder.toString(), true);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}));
		
		modules.add(new Appointments(commandHandler));
		modules.add(new Straitjacket(this, commandHandler));
		modules.add(new Eastereggs(commandHandler));
	}

	@Override
	public String getBotUsername() {
		return USERNAME;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage()) {
			Sender sender = new Sender(update.getMessage().getChatId(), this);
			if (update.getMessage().getNewChatMembers() != null) {
				System.out.println("New Users: " + update.getMessage().getNewChatMembers());
				if (update.getMessage().getNewChatMembers().stream().anyMatch(u -> u.getUserName() != null && u.getUserName().equals(USERNAME))) {
					return;
				} else {
					try {
						sender.send(stringmanager.getNewUserString(update.getMessage().getNewChatMembers()), true);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}
			}
			if (update.getMessage().isCommand()) {
				commandHandler.parse(update.getMessage());
			}
		}
	}

	@Override
	public String getBotToken() {
		try {
			return TokenHolder.getInstance().getToken();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isChatPaused(Long chatid) {
		return pausedChats.contains(chatid);
	}
}
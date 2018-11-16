package asylum.nursebot.modules;

import asylum.nursebot.NurseNoakes;
import asylum.nursebot.Sender;
import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.loader.ModuleDependencies;
import asylum.nursebot.objects.*;
import asylum.nursebot.persistence.ModelManager;
import asylum.nursebot.persistence.modules.RandomHugsOptin;
import asylum.nursebot.utils.ThreadHelper;
import asylum.nursebot.utils.log.Logger;
import com.google.inject.Inject;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@AutoModule(load=true)
public class RandomHugs implements Module {
    @Inject
    private CommandHandler commandHandler;
    @Inject
    private NurseNoakes nurse;

	@Inject
	private ModuleDependencies moduleDependencies;


    private CommandCategory category;

    class RandomHugProperties {
    	Long chatid;
        Map<Integer, User> users = new ConcurrentHashMap<>();
        Calendar next;
        RandomHugThread thread;
    }

    class RandomHugThread extends Thread {
    	private RandomHugProperties properties;
    	private volatile boolean running = true;

    	public RandomHugThread(RandomHugProperties properties) {
    		this.properties = properties;
		}

		public void shutdown() {
			running = false;
		}

		public void run() {
    		Sender sender = new Sender(properties.chatid, nurse);

			Random random = new Random();
			while(running) {
				if (properties.next == null) {
					properties.next = Calendar.getInstance();
					properties.next.add(Calendar.DATE, 1);
					properties.next.set(Calendar.HOUR_OF_DAY, random.nextInt(24));
					properties.next.set(Calendar.MINUTE, random.nextInt(60));
					properties.next.set(Calendar.SECOND, random.nextInt(60));
				}
				ThreadHelper.ignore(InterruptedException.class, () -> Thread.sleep(30000));

				if (!running)
					break;

				if (properties.next.compareTo(Calendar.getInstance()) < 0) {
					List<User> users = new LinkedList<User>(properties.users.values());
					if (users.size() != 0) {
						User user = users.get(random.nextInt(users.size()));
						ThreadHelper.ignore(TelegramApiException.class, () -> sender.mention(user, "\\*random hug\\*"));
					}
					properties.next = null;
				}
			}
		}
	}

    private ConcurrentHashMap<Long, RandomHugProperties> randomHugChats = new ConcurrentHashMap<>();

    private Logger logger = Logger.getModuleLogger("RandomHugs");

    public RandomHugs() {
        category = new CommandCategory("Eastereggs");

		ModelManager.build(RandomHugsOptin.class);
    }

    @Override
    public String getName() {
        return "Random Hugs";
    }

    @Override
    public ModuleType getType() {
        return new ModuleType().set(ModuleType.COMMAND_MODULE);
    }

    private RandomHugProperties getProperties(Long chatid) {
		RandomHugProperties properties = randomHugChats.get(chatid);
		if (properties == null) {
			properties = new RandomHugProperties();
			randomHugChats.put(chatid, properties);
			properties.chatid = chatid;
			properties.thread = new RandomHugThread(properties);
			properties.thread.start();
		}
		return properties;
	}

    @Override
    public void init() {

		commandHandler.add(new CommandInterpreter(this)
				.setName("huginfo")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setCategory(category)
				.setAction(c -> {
					RandomHugProperties properties = getProperties(c.getMessage().getChatId());

					if ((properties == null) || (properties.users.size()) == 0) {
						c.getSender().reply("Für diesen Chat ist niemand für Random Hugs angemeldet. : (\nUm dich anzumelden benutze /hugoptin.", c.getMessage());
					} else {
						StringBuilder builder = new StringBuilder();
						builder.append("Für diesen Chat sind folgende Leute für Random Hugs angemeldet:\n");
						for (User user : properties.users.values()) {
							builder.append("- ");
							if (user.getFirstName() != null)
								builder.append(user.getFirstName()).append(" ");
							if (user.getLastName() != null)
								builder.append(user.getLastName());
							builder.append("\n");
						}
						c.getSender().reply(builder.toString(), c.getMessage());
					}
				}));

    	commandHandler.add(new CommandInterpreter(this)
			.setName("hugoptin")
				.setInfo(": )")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setCategory(category)
				.setAction(c -> {
					Long chatid = c.getMessage().getChatId();
					User user = c.getMessage().getFrom();
					RandomHugProperties properties = getProperties(chatid);

					if (properties.users.containsKey(user.getId())) {
						c.getSender().reply("Du bist bereits angemeldet.\nMit /hugoptout kannst du dich wieder abmelden.", c.getMessage());
					} else {
						properties.users.put(user.getId(), user);
						RandomHugsOptin entry = new RandomHugsOptin(chatid, user.getId());
						entry.saveIt();
						c.getSender().reply("Yay, ich freue mich schon. : )", c.getMessage());
					}
				}));

        commandHandler.add(new CommandInterpreter(this)
                .setName("hugoptout")
                .setInfo(": (")
                .setVisibility(Visibility.PUBLIC)
                .setPermission(Permission.ANY)
                .setLocality(Locality.EVERYWHERE)
                .setCategory(category)
                .setAction(c -> {
					Long chatid = c.getMessage().getChatId();
					User user = c.getMessage().getFrom();

					RandomHugProperties properties = getProperties(chatid);

					if (properties.users.containsKey(user.getId())) {
						properties.users.remove(user.getId());
						RandomHugsOptin entry = RandomHugsOptin.find(chatid, user.getId());
						if (entry != null)
							entry.delete();
						c.getSender().reply("Schade. : (", c.getMessage());
					} else {
						c.getSender().reply("Du bist nicht angemeldet.\nMit /hugoptin kannst du dich wieder anmelden.", c.getMessage());
					}
                }));

    }

    @Override
    public void activate() {
    	if (randomHugChats.size() == 0) {
    		UserLookup userLookup = moduleDependencies.get(UserLookup.class);
    		if (userLookup == null) {
    			logger.error("Loading hug users from database failed: UserLookup module not active.");
			} else {

				List<RandomHugsOptin> entries = RandomHugsOptin.findAll();

				for (RandomHugsOptin entry : entries) {
					RandomHugProperties properties = getProperties(entry.getChatId());

					if (!properties.users.containsKey(entry.getUserId())) {
						User user = userLookup.getUser(entry.getUserId());
						if (user == null) {
							logger.error("Loading user from database failed: No User Entry from User Lookup.");
						} else {
							properties.users.put(entry.getUserId(), user);
						}
					}
				}
    		}
		}

		for(RandomHugProperties properties : randomHugChats.values()) {
			if (properties.thread == null)
				properties.thread = new RandomHugThread(properties);
			if (!properties.thread.running)
				properties.thread.start();
		}
    }

    @Override
    public void deactivate() {
		for(RandomHugProperties properties : randomHugChats.values()) {
			properties.thread.shutdown();
			properties.thread = null;
		}
    }

    @Override
    public void shutdown() {

    }
}

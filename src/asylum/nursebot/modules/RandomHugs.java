package asylum.nursebot.modules;

import asylum.nursebot.NurseNoakes;
import asylum.nursebot.Sender;
import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.objects.*;
import asylum.nursebot.utils.ThreadHelper;
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


    private CommandCategory category;

    class RandomHugProperties {
        Map<Integer, User> users = new ConcurrentHashMap<>();
        Calendar next;
        RandomHugThread thread;
    }

    class RandomHugThread extends Thread {
    	private RandomHugProperties properties;
    	private Sender sender;
    	private volatile boolean running = true;

    	public RandomHugThread(RandomHugProperties properties, Sender sender) {
    		this.properties = properties;
    		this.sender = sender;
		}

		public void shutdown() {
			running = false;
		}

		public void restart() {
			running = true;
			start();
		}

		public void run() {
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

    public RandomHugs() {
        category = new CommandCategory("Eastereggs");
    }

    @Override
    public String getName() {
        return "Random Hugs";
    }

    @Override
    public ModuleType getType() {
        return new ModuleType().set(ModuleType.COMMAND_MODULE);
    }

    private RandomHugProperties getProperties(Long chatid, Sender sender) {
		RandomHugProperties properties = randomHugChats.get(chatid);
		if (properties == null) {
			properties = new RandomHugProperties();
			randomHugChats.put(chatid, properties);
			properties.thread = new RandomHugThread(properties, sender);
			properties.thread.start();
		}
		return properties;
	}

    @Override
    public void init() {

    	commandHandler.add(new CommandInterpreter(this)
			.setName("hugoptin")
				.setInfo(": )")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setCategory(category)
				.setAction(c -> {
					RandomHugProperties properties = getProperties(c.getMessage().getChatId(), c.getSender());

					if (properties.users.containsKey(c.getMessage().getFrom().getId())) {
						c.getSender().reply("Du bist bereits angemeldet.\nMit /hugoptout kannst du dich wieder abmelden.", c.getMessage());
					} else {
						properties.users.put(c.getMessage().getFrom().getId(), c.getMessage().getFrom());
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
					RandomHugProperties properties = getProperties(c.getMessage().getChatId(), c.getSender());

					if (properties.users.containsKey(c.getMessage().getFrom().getId())) {
						properties.users.remove(c.getMessage().getFrom().getId());
						c.getSender().reply("Schade. : (", c.getMessage());
					} else {
						c.getSender().reply("Du bist nicht angemeldet.\nMit /hugoptin kannst du dich wieder anmelden.", c.getMessage());
					}
                }));

    }

    @Override
    public void activate() {
		for(RandomHugProperties properties : randomHugChats.values()) {
			properties.thread.restart();
		}
    }

    @Override
    public void deactivate() {
		for(RandomHugProperties properties : randomHugChats.values()) {
			properties.thread.shutdown();
		}
    }

    @Override
    public void shutdown() {

    }
}

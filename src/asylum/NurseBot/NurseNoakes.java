package asylum.NurseBot;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class NurseNoakes extends TelegramLongPollingBot {

	public static final String USERNAME = "NurseNoakesBot";

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
	
	Set<Long> pausedChats = new HashSet<>();
	
	public NurseNoakes() {
		stringmanager = new StringManager();
		commandHandler = new CommandHandler(this);
		
		
		commandHandler.add(new Command()
				.setName("miau")
				.setInfo("Da muss wohl eine Katze gestreichelt werden.")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.USER)
				.setLocality(Locality.EVERYWHERE)
				.setAction(c -> {
					try {
						c.getSender().reply("*streichel*", c.getMessage());
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}));
		commandHandler.add(new Command()
				.setName("mimimi")
				.setInfo("Wollen wir die Muppets sehen?")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.USER)
				.setLocality(Locality.EVERYWHERE)
				.setAction(c -> {
					try {
						c.getSender().send("https://www.youtube.com/watch?v=VnT7pT6zCcA");
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}));
		commandHandler.add(new Command()
				.setName("eyeroll")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.USER)
				.setLocality(Locality.EVERYWHERE)
				.setAction(c -> {
					try {
						c.getSender().send("🙄");
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}));
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
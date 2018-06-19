package asylum.NurseBot.modules;

import org.telegram.telegrambots.exceptions.TelegramApiException;

import asylum.NurseBot.commands.Command;
import asylum.NurseBot.commands.CommandCategory;
import asylum.NurseBot.commands.CommandHandler;
import asylum.NurseBot.commands.Locality;
import asylum.NurseBot.commands.Permission;
import asylum.NurseBot.commands.Visibility;

public class Eastereggs implements Module {

	private CommandCategory category;
	
	public Eastereggs(CommandHandler commandHandler) {
		
		category = new CommandCategory("Eastereggs");
		
		commandHandler.add(new Command()
				.setName("miau")
				.setInfo("Da muss wohl eine Katze gestreichelt werden.")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.USER)
				.setLocality(Locality.EVERYWHERE)
				.setCategory(category)
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
				.setCategory(category)
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
				.setCategory(category)
				.setAction(c -> {
					try {
						c.getSender().send("🙄");
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}));
	}

	@Override
	public String getName() {
		return "Eastereggs";
	}
	
}

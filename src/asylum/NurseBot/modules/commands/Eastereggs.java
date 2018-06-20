package asylum.NurseBot.modules.commands;

import org.telegram.telegrambots.exceptions.TelegramApiException;

import asylum.NurseBot.commands.CommandInterpreter;
import asylum.NurseBot.utils.Locality;
import asylum.NurseBot.utils.Module;
import asylum.NurseBot.utils.Permission;
import asylum.NurseBot.utils.Visibility;
import asylum.NurseBot.commands.CommandCategory;
import asylum.NurseBot.commands.CommandHandler;

public class Eastereggs implements Module {

	private CommandCategory category;
	
	public Eastereggs(CommandHandler commandHandler) {
		
		category = new CommandCategory("Eastereggs");
		
		commandHandler.add(new CommandInterpreter()
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
		commandHandler.add(new CommandInterpreter()
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
		commandHandler.add(new CommandInterpreter()
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

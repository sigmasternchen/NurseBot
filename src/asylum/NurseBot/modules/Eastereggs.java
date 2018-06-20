package asylum.NurseBot.modules;

import java.util.Random;

import org.telegram.telegrambots.exceptions.TelegramApiException;

import asylum.NurseBot.commands.CommandInterpreter;
import asylum.NurseBot.objects.Locality;
import asylum.NurseBot.objects.Module;
import asylum.NurseBot.objects.Permission;
import asylum.NurseBot.objects.Visibility;
import asylum.NurseBot.semantics.SemanticInterpreter;
import asylum.NurseBot.semantics.SemanticsHandler;
import asylum.NurseBot.semantics.WakeWord;
import asylum.NurseBot.semantics.WakeWordType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import asylum.NurseBot.NurseNoakes;
import asylum.NurseBot.commands.CommandCategory;
import asylum.NurseBot.commands.CommandHandler;

public class Eastereggs implements Module {

	private CommandCategory category;
	
	private CommandHandler commandHandler;
	private SemanticsHandler semanticsHandler;
	
	public Eastereggs() {
		category = new CommandCategory("Eastereggs");
	}
	
	@Override
	public void init() {
		commandHandler.add(new CommandInterpreter(this)
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
		commandHandler.add(new CommandInterpreter(this)
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
		commandHandler.add(new CommandInterpreter(this)
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
		
		semanticsHandler.add(new SemanticInterpreter(this)
				.addWakeWord(new WakeWord("mau", WakeWordType.STANDALONE, false))
				.setLocality(Locality.EVERYWHERE)
				.setPermission(Permission.USER)
				.setAction(c -> {
					String[] replys = new String[] {
							"*streichel*", "Mau", "*flausch*"
					};
					
					new Thread(() -> {
						Random random = new Random();
						
						try {
							Thread.sleep((random.nextInt(5) + 1) * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						try {
							c.getSender().reply(replys[random.nextInt(replys.length)], c.getMessage());
						} catch (TelegramApiException e) {
							e.printStackTrace();
						}
					}).start();
				}));
	}

	@Override
	public String getName() {
		return "Eastereggs";
	}

	@Override
	public boolean isCommandModule() {
		return true;
	}

	@Override
	public boolean isSemanticModule() {
		return true;
	}

	@Override
	public boolean needsNurse() {
		return false;
	}

	@Override
	public void setNurse(NurseNoakes nurse) {
		throw new NotImplementedException();
	}

	@Override
	public void setCommandHandler(CommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	@Override
	public void setSemanticsHandler(SemanticsHandler semanticHandler) {
		this.semanticsHandler = semanticHandler;
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}

	@Override
	public void shutdown() {
	}
	
}

package asylum.nursebot.modules;

import java.util.Random;

import org.telegram.telegrambots.exceptions.TelegramApiException;

import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.objects.AutoModule;
import asylum.nursebot.objects.Locality;
import asylum.nursebot.objects.Module;
import asylum.nursebot.objects.Permission;
import asylum.nursebot.objects.Visibility;
import asylum.nursebot.semantics.SemanticInterpreter;
import asylum.nursebot.semantics.SemanticsHandler;
import asylum.nursebot.semantics.WakeWord;
import asylum.nursebot.semantics.WakeWordType;
import asylum.nursebot.NurseNoakes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;

@AutoModule(load=true)
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
				.setPermission(Permission.ANY)
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
				.setPermission(Permission.ANY)
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
				.setPermission(Permission.ANY)
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
				.setPermission(Permission.ANY)
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

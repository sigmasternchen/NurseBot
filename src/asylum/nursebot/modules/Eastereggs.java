package asylum.nursebot.modules;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import asylum.nursebot.loader.ModuleDependencies;
import asylum.nursebot.utils.StringTools;
import asylum.nursebot.utils.ThreadHelper;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.google.inject.Inject;

import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.objects.Locality;
import asylum.nursebot.objects.Module;
import asylum.nursebot.objects.ModuleType;
import asylum.nursebot.objects.Permission;
import asylum.nursebot.objects.Visibility;
import asylum.nursebot.semantics.SemanticInterpreter;
import asylum.nursebot.semantics.SemanticsHandler;
import asylum.nursebot.semantics.WakeWord;
import asylum.nursebot.semantics.WakeWordType;
import asylum.nursebot.NurseNoakes;
import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;

@AutoModule(load=true)
public class Eastereggs implements Module {

	private CommandCategory category;
	
	@Inject
	private CommandHandler commandHandler;
	@Inject
	private SemanticsHandler semanticsHandler;
	@Inject
	private ModuleDependencies moduleDependencies;
	@Inject
	private NurseNoakes nurse;

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
					c.getSender().send("https://www.youtube.com/watch?v=VnT7pT6zCcA");
				}));
		commandHandler.add(new CommandInterpreter(this)
				.setName("eyeroll")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setCategory(category)
				.setAction(c -> {
					c.getSender().send("🙄");
				}));


		semanticsHandler.add(new SemanticInterpreter(this)
				.addWakeWord(new WakeWord("*boop*", WakeWordType.ANYWHERE, false))
				.addWakeWord(new WakeWord("*stups*", WakeWordType.ANYWHERE, false))
				.addWakeWord(new WakeWord("*anstups*", WakeWordType.ANYWHERE, false))
				.setLocality(Locality.EVERYWHERE)
				.setPermission(Permission.ANY)
				.setAction(c -> {
					if (!c.getMessage().getText().contains("@" + nurse.getBotUsername()))
						return;
					
					String[] replys = new String[] {
							"*erschreck*", "*erschreck*\n*vom Stuhl fall*", "Au! D:"
					};
					
					Random random = new Random();
					
					c.getSender().reply(replys[random.nextInt(replys.length)], c.getMessage());
				}));
		

		semanticsHandler.add(new SemanticInterpreter(this)
				.addWakeWord(new WakeWord("danke", WakeWordType.ANYWHERE, false))
				.setLocality(Locality.EVERYWHERE)
				.setPermission(Permission.ANY)
				.setAction(c -> {
					if (c.getMessage().getText().split(" ").length > 4)
						return;
					if (!c.getMessage().getText().contains(" Noakes"))
						return;
					
					String[] replys = new String[] {
							"Gern geschehen.", "Hab ich gerne gemacht."
					};
					
					Random random = new Random();	
					
					c.getSender().reply(replys[random.nextInt(replys.length)], c.getMessage());
				}));
		
		semanticsHandler.add(new SemanticInterpreter(this)
				.addWakeWord(new WakeWord("hawara", WakeWordType.ANYWHERE, false))
				.setLocality(Locality.EVERYWHERE)
				.setPermission(Permission.ANY)
				.setAction(c -> {
					if (!c.getMessage().getFrom().getUserName().equals("m4xcoat"))
						return;
					
					String[] replys = new String[] {
							"Heast!"
					};
					
					Random random = new Random();	
					
					c.getSender().reply(replys[random.nextInt(replys.length)], c.getMessage());
				}));
		semanticsHandler.add(new SemanticInterpreter(this)
				.addWakeWord(new WakeWord("Gute Nacht", WakeWordType.ANYWHERE, false))
				.addWakeWord(new WakeWord("Nachti", WakeWordType.ANYWHERE, false))
				.addWakeWord(new WakeWord("Ich geh dann mal ins Bett.", WakeWordType.ANYWHERE, false))
				.addWakeWord(new WakeWord("Ich geh dann mal schlafen.", WakeWordType.ANYWHERE, false))
				.setLocality(Locality.EVERYWHERE)
				.setPermission(Permission.ANY)
				.setAction(c -> {
					String[] replys = new String[] {
							"Ciao. o/", "Gute Nacht.", "Gute Nacht!", "Eine erholsame Nacht wünsche ich.", 
							"Schlaf gut.", "Träum was Schönes.", "Nachtilein", "*zum Bett trag*", 
							"Schlaf fein.", "Träum was Flauschiges.", "Eine gute Nacht wünsche ich.", 
							"Gute Idee, ich bin auch schon müde. *gähn*", "Bis morgen.",
							"*auf die Uhr schau*\nJetzt schon? o.ô", "Tschüss, man liest sich morgen.",
							"Aber nicht mehr lange mit dem Handy spielen, ja?"
					};
					
					Calendar calendar = Calendar.getInstance();
					int hour = calendar.get(Calendar.HOUR_OF_DAY);
					if (!(hour < 2 || hour > 21))
						return;
					
					Random random = new Random();
					c.getSender().reply("test", c.getMessage());
					c.getSender().reply(replys[random.nextInt(replys.length)], c.getMessage());
				}));

		semanticsHandler.add(new SemanticInterpreter(this)
				.addWakeWord(new WakeWord("bla", WakeWordType.ANYWHERE, false))
				.setLocality(Locality.EVERYWHERE)
				.setPermission(Permission.ANY)
				.setAction(c -> {

					c.getSender().reply(c.getMessage().getFrom().getUserName() + ": " + c.getMessage().getText(), c.getMessage());

					if (!c.getMessage().getFrom().getUserName().equals("overflowerror"))
						return;
					if (!c.getMessage().getText().contains(" geht an"))
						return;

					UserLookup lookup = moduleDependencies.get(UserLookup.class);

					List<User> users = null;

					if (lookup != null) {
						users = lookup.getMentions(c.getMessage());
					}

					if (users == null) {
						users = new LinkedList<>();
						if (c.getMessage().getReplyToMessage() != null) {
							users.add(c.getMessage().getReplyToMessage().getFrom());
						}
					}

					c.getSender().send(users.toString());

					if (users.size() != 1)
						return;

					final User user = users.get(0);

					ThreadHelper.delay(() -> {
						c.getSender().send("Gratuliere," + StringTools.makeMention(user) + "!\nDu hast es wirklich verdient.", true);
					}, 1000);

					ThreadHelper.delay(() -> {
						c.getSender().send("*überreicht die Medaille*\n\nhttps://upload.wikimedia.org/wikipedia/ka/e/ed/Nobel_Prize.png", false);
					}, 2000);

					ThreadHelper.delay(() -> {
						c.getSender().send("*applaudiert*", false);
					}, 3000);

				}));
	}

	@Override
	public String getName() {
		return "Eastereggs";
	}

	@Override
	public ModuleType getType() {
		return new ModuleType()
				.set(ModuleType.COMMAND_MODULE)
				.set(ModuleType.SEMANTIC_MODULE);
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

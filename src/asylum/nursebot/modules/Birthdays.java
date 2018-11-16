package asylum.nursebot.modules;

import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.modules.birthdays.Privacy;
import asylum.nursebot.objects.*;
import asylum.nursebot.persistence.modules.BirthdaysBirthday;
import asylum.nursebot.persistence.modules.BirthdaysGratulation;
import asylum.nursebot.utils.StringTools;
import asylum.nursebot.utils.ThreadHelper;
import com.google.inject.Inject;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@AutoModule(load=true)
public class Birthdays implements Module {
	@Inject
	private CommandHandler commandHandler;

	private CommandCategory category;

	public Birthdays() {
		category = new CommandCategory("Geburtstage");
	}

	@Override
	public String getName() {
		return "Birthdays";
	}

	@Override
	public ModuleType getType() {
		return new ModuleType().set(ModuleType.COMMAND_MODULE);
	}

	@Override
	public void init() {
		commandHandler.add(new CommandInterpreter(this)
				.setName("setbirthday")
				.setInfo("setzt den eigenen Geburtstag")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setCategory(category)
				.setAction(c -> {
					final String synopsis = "Synopsis: /setbirthday YYYY-MM-DD [PRIVACY]\n\nPRIVACY := private|public|public-age|public-date\n\nDefault-Privacy ist private.";

					List<String> arguments = StringTools.tokenize(c.getParameter());

					Privacy privacy = null;

					if (arguments.size() == 1) {
						privacy = Privacy.PRIVATE;
					} else if (arguments.size() == 2) {
						privacy = Privacy.fromString(arguments.get(1));
					}

					if (privacy == null) {
						c.getSender().reply(synopsis, c.getMessage());
						return;
					}

					LocalDate date = null;

					try {
						date = LocalDate.parse(arguments.get(1));
					} catch (DateTimeParseException e) {
						c.getSender().reply(synopsis, c.getMessage());
						return;
					}

					int userid = c.getMessage().getFrom().getId();

					BirthdaysBirthday birthday = BirthdaysBirthday.findByUserid(userid);
					if (birthday == null) {
						birthday = new BirthdaysBirthday(userid);
					}
					birthday.setBirthday(date);
					birthday.setPrivacy(privacy);
					birthday.saveIt();

					c.getSender().reply("Dein Geburtstag steht jetzt im Kalender. \\o/", c.getMessage());
				}));

		commandHandler.add(new CommandInterpreter(this)
				.setName("deletebirthday")
				.setInfo("löscht den eigenen Geburtstag")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setCategory(category)
				.setAction(c -> {
					BirthdaysBirthday birthday = BirthdaysBirthday.findByUserid(c.getMessage().getFrom().getId());
					if (birthday == null) {
						c.getSender().reply("Aber ich kenne deinen Geburtstag doch nicht mal. D:", c.getMessage());
						return;
					}

					birthday.delete();

					c.getSender().reply("*deinen Geburtstag im Kalender durchstreich*", c.getMessage());
				}));

		commandHandler.add(new CommandInterpreter(this)
				.setName("enablebirthday")
				.setInfo("aktiviert Geburtstags-Gratulationen in Gruppen")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ANY)
				.setLocality(Locality.GROUPS)
				.setCategory(category)
				.setAction(c -> {
					int userid = c.getMessage().getFrom().getId();
					long  chatid = c.getMessage().getChat().getId();

					BirthdaysBirthday birthday = BirthdaysBirthday.findByUserid(userid);
					if (birthday == null) {
						c.getSender().reply("Ich würde dir ja gerne gratulieren, aber ich weiß nicht, wann du Geburtstag hast. : (\n" +
								"Mit /setbirthday kannst du es mir verraten (übrigens auch in Private Chats mit mir, wenn du ihn geheim halten willst).", c.getMessage());
						return;
					}

					if (birthday.getPrivacy() == Privacy.PRIVATE) {
						c.getSender().reply("Du hast mir gesagt, dein Geburtstag wäre privat. Bitte ändere das, und versuch es nochmal.", c.getMessage());
						return;
					}

					BirthdaysGratulation gratulation = BirthdaysGratulation.find(userid, chatid);
					if (gratulation != null) {
						c.getSender().reply("Das mache ich doch schon...", c.getMessage());
						return;
					}
					gratulation = new BirthdaysGratulation(userid, chatid);
					gratulation.saveIt();

					c.getSender().reply("Okay, an deinem nächsten Geburtstag gratuliere ich dir. : )", c.getMessage());

					if (birthday.getBirthday().isEqual(LocalDate.now())) {
						ThreadHelper.delay(() -> {
							c.getSender().reply("Oh, by the way: Happy Birthday! ; )", c.getMessage());
						}, 2000);
					});
				}));

		commandHandler.add(new CommandInterpreter(this)
				.setName("disablebirthday")
				.setInfo("deaktiviert Geburtstags-Gratulationen")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ANY)
				.setLocality(Locality.GROUPS)
				.setCategory(category)
				.setAction(c -> {
					int userid = c.getMessage().getFrom().getId();
					long  chatid = c.getMessage().getChat().getId();

					BirthdaysGratulation gratulation = BirthdaysGratulation.find(userid, chatid);
					if (gratulation == null) {
						c.getSender().reply("Aber... äh... Gut, von mir aus: ich werde dir hier (wie bisher) nicht zum Geburtstag gratulieren.", c.getMessage());
						return;
					}
					gratulation.delete();

					c.getSender().reply("Ich werde dir hier ab jetzt nicht mehr zun Geburtstag gratulieren.", c.getMessage());
				}));

		commandHandler.add(new CommandInterpreter(this)
				.setName("showbirthday")
				.setInfo("zeigt die Geburtstags der erwähnten Personen an")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ANY)
				.setLocality(Locality.EVERYWHERE)
				.setCategory(category)
				.setAction(c -> {
					final String synopsis = "Synopsis: /showbirthday [all|{MENTION}]";
				}));
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

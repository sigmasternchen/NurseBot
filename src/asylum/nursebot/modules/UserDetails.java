package asylum.nursebot.modules;

import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.loader.ModuleDependencies;
import asylum.nursebot.objects.*;
import asylum.nursebot.persistence.modules.UserDetailsInfo;
import asylum.nursebot.utils.StringTools;
import asylum.nursebot.utils.log.Logger;
import com.google.inject.Inject;
import org.telegram.telegrambots.api.objects.User;

import java.util.List;

@AutoModule(load=true)
public class UserDetails implements Module {
	@Inject
	private CommandHandler commandHandler;

	@Inject
	private ModuleDependencies dependencies;

	private Logger logger = Logger.getModuleLogger("UserDetails");

	private CommandCategory category;

	public UserDetails() {
		category = new CommandCategory("Benutzer-Informationen");
	}

	@Override
	public String getName() {
		return "UserDetails";
	}

	@Override
	public ModuleType getType() {
		return new ModuleType().set(ModuleType.COMMAND_MODULE);
	}

	@Override
	public void init() {
		commandHandler.add(new CommandInterpreter(this)
			.setName("setuserinfo")
			.setInfo("Benutzerinfo ändern")
			.setCategory(category)
			.setLocality(Locality.EVERYWHERE)
			.setVisibility(Visibility.PUBLIC)
			.setPermission(Permission.ANY)
			.setAction(c -> {
				UserLookup lookup = dependencies.get(UserLookup.class);
				if (lookup == null) {
					logger.error("Couldn't get UserLookup instance.");
					c.getSender().reply("Ohje. Irgendwas ist schiefgelaufen. Details stehen im Log-File.", c.getMessage());
					return;
				}

				final String synopsis = "Synopsis: /setuserinfo MENTION \"INFO\"";

				List<String> parameters = StringTools.tokenize(c.getParameter());
				List<User> users = lookup.getMentions(c.getMessage());
				if (parameters.size() != 2) {
					c.getSender().reply(synopsis, c.getMessage());
					return;
				}
				if (users.size() < 1) {
					c.getSender().reply("Der User wurde nicht gefunden. Möglicherweise ist er noch nicht vom UserLookup erfasst.", c.getMessage());
					return;
				}
				if (users.size() > 1) {
					c.getSender().reply("Bitte erwähne nur den User, zu dem du Details hinzufügen willst.", c.getMessage());
					return;
				}
				User user = users.get(0);
				User author = c.getMessage().getFrom();

				UserDetailsInfo info = UserDetailsInfo.get(author.getId(), user.getId());
				if (info == null) {
					info = new UserDetailsInfo(author.getId(), user.getId());
				}
				info.set(parameters.get(1));
				info.saveIt();

				c.getSender().reply("Die Benutzer Info wurde erfolgreich gesetzt.", c.getMessage());
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

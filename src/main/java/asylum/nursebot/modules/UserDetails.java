package asylum.nursebot.modules;

import java.util.List;

import com.google.inject.Inject;

import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.loader.ModuleDependencies;
import asylum.nursebot.objects.Locality;
import asylum.nursebot.objects.Module;
import asylum.nursebot.objects.ModuleType;
import asylum.nursebot.objects.Permission;
import asylum.nursebot.objects.Visibility;
import asylum.nursebot.persistence.ModelManager;
import asylum.nursebot.persistence.modules.UserDetailsInfo;
import asylum.nursebot.utils.StringTools;
import asylum.nursebot.utils.log.Logger;
import org.telegram.telegrambots.meta.api.objects.User;

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

		ModelManager.build(UserDetailsInfo.class);
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
				if (users.isEmpty()) {
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
				info.setText(parameters.get(1));
				info.saveIt();

				c.getSender().reply("Die Benutzer Info wurde erfolgreich gesetzt.", c.getMessage());
			}));

		commandHandler.add(new CommandInterpreter(this)
				.setName("getuserinfo")
				.setInfo("Benutzerinfo anzeigen")
				.setCategory(category)
				.setLocality(Locality.EVERYWHERE) // TODO: change to USER
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ANY)
				.setAction(c -> {
					UserLookup lookup = dependencies.get(UserLookup.class);
					if (lookup == null) {
						logger.error("Couldn't get UserLookup instance.");
						c.getSender().reply("Ohje. Irgendwas ist schiefgelaufen. Details stehen im Log-File.", c.getMessage());
						return;
					}

					User author = c.getMessage().getFrom();

					List<User> users = lookup.getMentions(c.getMessage());
					if (users.size() > 3) {
						c.getSender().reply("Bitte nicht so viele auf einmal.", c.getMessage());
						return;
					}
					if (users.isEmpty()) {
						c.getSender().reply("Diese User kenne ich nicht.", c.getMessage());
						return;
					}

					StringBuilder builder = new StringBuilder();

					boolean okay = false;

					for(User user : users) {
						builder.append("Details für ").append(user.getFirstName());
						if (user.getUserName() != null) {
							builder.append(" (@").append(user.getUserName()).append(")");
						}
						builder.append(":\n\n");
						List<UserDetailsInfo> infos = UserDetailsInfo.getAll(author.getId(), user.getId());

						for (UserDetailsInfo info : infos) {
							User infoAuthor = lookup.getUser(info.getAuthorUserId());
							if (infoAuthor == null)
								continue;

							okay = true;

							builder.append("von ");
							if (infoAuthor.getId().equals(author.getId())) {
								builder.append("dir");
							} else {
								builder.append(infoAuthor.getFirstName());
								if (infoAuthor.getUserName() != null) {
									builder.append(" (@").append(infoAuthor.getUserName()).append(")");
								}
							}
							builder.append(":\n");

							builder.append(info.getText());
							builder.append("\n");
						}
					}

					if (!okay) {
						c.getSender().reply("Es wurden keine Details zu diesen Benutzern gefunden.", c.getMessage());
						return;
					}

					c.getSender().reply(builder.toString(), c.getMessage());
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

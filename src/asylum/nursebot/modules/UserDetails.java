package asylum.nursebot.modules;

import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.loader.ModuleDependencies;
import asylum.nursebot.objects.*;
import com.google.inject.Inject;

@AutoModule(load=true)
public class UserDetails implements Module {
	@Inject
	private CommandHandler commandHandler;

	@Inject
	private ModuleDependencies dependencies;

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

package asylum.nursebot.modules;

import asylum.nursebot.utils.log.Logger;
import com.google.inject.Inject;

import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.objects.Locality;
import asylum.nursebot.objects.Module;
import asylum.nursebot.objects.ModuleType;
import asylum.nursebot.objects.Permission;
import asylum.nursebot.objects.Visibility;

@AutoModule(load = true)
public class TestModule implements Module {

	@Inject
	private CommandHandler commandHandler;
	
	private CommandCategory category;

	private Logger logger = Logger.getModuleLogger("TestModule");
	
	@Override
	public String getName() {
		return "Test";
	}

	@Override
	public ModuleType getType() {
		return new ModuleType()
				.set(ModuleType.COMMAND_MODULE)
				.set(ModuleType.TEST_MODULE);
	}

	public TestModule() {
		category = new CommandCategory("Test");
	}
	

	
	@Override
	public void init() {
		commandHandler.add(new CommandInterpreter(this)
				.setName("test")
				.setInfo("test")
				.setLocality(Locality.EVERYWHERE)
				.setPermission(Permission.ANY)
				.setCategory(category)
				.setVisibility(Visibility.PRIVATE)
				.setAction(c -> {
					logger.verbose(c.getMessage().toString());
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

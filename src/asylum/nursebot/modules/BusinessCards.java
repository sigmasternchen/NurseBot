package asylum.nursebot.modules;

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
import asylum.nursebot.persistence.modules.BusinessCardsCard;
import asylum.nursebot.persistence.modules.BusinessCardsCardsFields;
import asylum.nursebot.persistence.modules.BusinessCardsField;

@AutoModule(load=true)
public class BusinessCards implements Module {

	@Inject
	private CommandHandler commandHandler;
	
	@Inject
	private ModuleDependencies moduleDependencies;

	private CommandCategory category;
	
	@Override
	public String getName() {
		return "Business Cards";
	}

	@Override
	public ModuleType getType() {
		return new ModuleType().set(ModuleType.COMMAND_MODULE);
	}

	public BusinessCards() {
		ModelManager.build(BusinessCardsCard.class);
		ModelManager.build(BusinessCardsField.class);
		ModelManager.build(BusinessCardsCardsFields.class);
		
		category = new CommandCategory("Visitenkarten");
	}
	
	@Override
	public void init() {
		commandHandler.add(new CommandInterpreter(this)
				.setName("createcard")
				.setInfo("erstellt eine neue Visitenkarte")
				.setLocality(Locality.USERS)
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.ANY)
				.setCategory(category)
				.setAction(c -> {
					// TODO
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

package asylum.nursebot.modules;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.util.Args;

import com.google.inject.Inject;

import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.exceptions.NurseException;
import asylum.nursebot.exceptions.ParsingException;
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
import asylum.nursebot.semantics.SemanticsHandler;
import asylum.nursebot.utils.StringTools;

@AutoModule(load=true)
public class BusinessCards implements Module {

	@Inject
	private CommandHandler commandHandler;
	
	@Inject
	private SemanticsHandler semanticsHandler;
	
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
	
	private List<BusinessCardsField> getDefinedFields() {
		return BusinessCardsField.findAll();
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
					String help = "Synopsis: CARDNAME [public] {FIELD=VALUE ...}";
					List<String> args = StringTools.tokenize(c.getParameter());
					if (args.size() < 2) {
						c.getSender().send(help);
						return;
					}
					String cardname = args.get(0);
					boolean isPublic = args.get(1).toLowerCase().equals("public");
					args = args.subList(1, args.size());
					
					List<BusinessCardsField> fields = new LinkedList<>();
					List<BusinessCardsField> definedFields = getDefinedFields();
					
					try {
							
						for (String arg : args) {
							int position = arg.indexOf("=");
							if (position < 0)
								throw new ParsingException("Missing =");
							String key = arg.substring(0, position);
							String value = arg.substring(position + 1);
							
							
							BusinessCardsField foundField;
							for (BusinessCardsField field : definedFields) {
								if (field.getName().equals(key)) {
									foundField = field;
									break;
								}
							}
							if (foundField == null)
								throw new ParsingException("Unknown key.");
							
							
						}
					
					} catch (NurseException e) {
						c.getSender().send(help + "\n\n" + e.getMessage());
					}
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

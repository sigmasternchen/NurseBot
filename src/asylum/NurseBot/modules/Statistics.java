package asylum.NurseBot.modules;

import asylum.NurseBot.NurseNoakes;
import asylum.NurseBot.commands.CommandHandler;
import asylum.NurseBot.objects.Locality;
import asylum.NurseBot.objects.Module;
import asylum.NurseBot.objects.Permission;
import asylum.NurseBot.persistence.ModelManager;
import asylum.NurseBot.persistence.modules.StatisticsMessage;
import asylum.NurseBot.semantics.SemanticInterpreter;
import asylum.NurseBot.semantics.SemanticsHandler;
import asylum.NurseBot.semantics.WakeWord;
import asylum.NurseBot.semantics.WakeWordType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Statistics implements Module {

	private CommandHandler commandHandler;
	private SemanticsHandler semanticsHandler;

	@Override
	public String getName() {
		return "Statistics";
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

	public Statistics() {
		ModelManager.build(StatisticsMessage.class);
	}
	
	@Override
	public void init() {
		semanticsHandler.add(new SemanticInterpreter(this)
				.addWakeWord(new WakeWord(null, WakeWordType.TEXT_MESSAGE))
				.setLocality(Locality.GROUPS)
				.setPermission(Permission.ANY)
				.setAction(c -> {
					new StatisticsMessage()
						.setChatId(c.getMessage().getChatId())
						.setLength(c.getMessage().getText().length())
						.saveIt();
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

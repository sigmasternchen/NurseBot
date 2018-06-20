package asylum.NurseBot.objects;

import asylum.NurseBot.NurseNoakes;
import asylum.NurseBot.commands.CommandHandler;
import asylum.NurseBot.semantics.SemanticsHandler;

public interface Module {
	String getName();
	boolean isCommandModule();
	boolean isSemanticModule();
	boolean needsNurse();
	void setNurse(NurseNoakes nurse);
	void setCommandHandler(CommandHandler commandHandler);
	void setSemanticsHandler(SemanticsHandler semanticHandler);
	void init();
	void activate();
	void deactivate();
	void shutdown();
}

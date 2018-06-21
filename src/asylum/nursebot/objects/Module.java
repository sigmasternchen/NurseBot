package asylum.nursebot.objects;

import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.semantics.SemanticsHandler;
import asylum.nursebot.NurseNoakes;

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

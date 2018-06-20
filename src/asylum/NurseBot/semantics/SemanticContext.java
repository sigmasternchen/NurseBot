package asylum.NurseBot.semantics;

import org.telegram.telegrambots.api.objects.Message;

import asylum.NurseBot.Sender;
import asylum.NurseBot.utils.ActionContext;

public class SemanticContext extends ActionContext {

	public SemanticContext(Message message, Sender sender) {
		super(message, sender);
	}
	
}

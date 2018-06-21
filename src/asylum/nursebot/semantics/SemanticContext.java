package asylum.nursebot.semantics;

import org.telegram.telegrambots.api.objects.Message;

import asylum.nursebot.objects.ActionContext;
import asylum.nursebot.Sender;

public class SemanticContext extends ActionContext {

	public SemanticContext(Message message, Sender sender) {
		super(message, sender);
	}
	
}

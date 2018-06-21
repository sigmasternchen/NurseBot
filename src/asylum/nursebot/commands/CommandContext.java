package asylum.nursebot.commands;

import org.telegram.telegrambots.api.objects.Message;

import asylum.nursebot.objects.ActionContext;
import asylum.nursebot.Sender;

public class CommandContext extends ActionContext {

	private String parameter;
	
	public String getParameter() {
		return parameter;
	}
	
	public CommandContext(Message message, Sender sender, String parameter) {
		super(message, sender);
		this.parameter = parameter;
	}
}

package asylum.NurseBot.commands;

import org.telegram.telegrambots.api.objects.Message;

import asylum.NurseBot.Sender;
import asylum.NurseBot.utils.ActionContext;

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

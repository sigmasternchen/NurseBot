package asylum.NurseBot;

import org.telegram.telegrambots.api.objects.Message;

public class CommandContext {
	private Message message;
	private Sender sender;
	private String parameter;
	
	public Message getMessage() {
		return message;
	}
	
	public Sender getSender() {
		return sender;
	}
	
	public String getParameter() {
		return parameter;
	}
	public CommandContext(Message message, Sender sender, String parameter) {
		super();
		this.message = message;
		this.sender = sender;
	}
}

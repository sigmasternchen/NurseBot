package asylum.NurseBot.objects;

import org.telegram.telegrambots.api.objects.Message;

import asylum.NurseBot.Sender;

public abstract class ActionContext {
	private Message message;
	private Sender sender;
	
	public Message getMessage() {
		return message;
	}
	
	public Sender getSender() {
		return sender;
	}

	public ActionContext(Message message, Sender sender) {
		super();
		this.message = message;
		this.sender = sender;
	}
	
	
}

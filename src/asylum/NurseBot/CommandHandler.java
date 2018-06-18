package asylum.NurseBot;

import java.util.LinkedList;
import java.util.List;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class CommandHandler {

	private NurseNoakes nurse;
	
	private List<Command> commands;
	
	public CommandHandler(NurseNoakes nurse) {
		commands = new LinkedList<>();
		this.nurse = nurse;
		
		commands.add(new Command()
				.setName("help")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.USER)
				.setAction(c -> {
					try {
						c.getSender().send(getHelp());
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}));
	}
	
	public String getHelp() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Hier ist eine Liste von Dingen, die ich kann:\n\n");
		
		for(Command command : commands) {
			if (command.getVisibility() == Visibility.PRIVATE)
				continue;
			
			StringBuilder spaces = new StringBuilder();
			for (int i = command.getName().length() + 1; i < 20; i++)
				spaces.append(" ");
			builder.append("/").append(command.getName()).append(spaces.toString()).append(command.getInfo()).append("\n");
		}
		
		return builder.toString();
	}
	
	public void parse(Message message) {
		if (!message.hasText()) {
			System.out.println("Command without text.");
			return;
		}
		String token = message.getText().split(" ")[0];
		token = token.substring(1);
		String parameter = "";
		if (token.length() + 1 < message.getText().length())
			parameter = message.getText().substring(token.length() + 2);
		
		Command command = null;
		for (Command c : commands) {
			if (c.getName().equals(token) || (c.getName() + "@" + NurseNoakes.USERNAME).equals(token)) {
				command = c;
				break;
			}
		}
		if (command == null) {
			System.out.println("Command not found: " + token);
			return;
		}
		
		if (!command.getLocality().check(message.getChat())) {
			System.out.println("Wrong chat type.");
			return;
		}
		
		if (nurse.isChatPaused(message.getChatId()) && command.isPausable()) {
			System.out.println("Chat is paused.");
			return;
		}
		
		// TODO Permission check
		
		CommandContext context = new CommandContext(message, new Sender(message.getChatId(), nurse), parameter);
		command.getAction().action(context);
	}
	
	public void add(Command command) {
		commands.add(command);
	}
}

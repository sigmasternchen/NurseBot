package asylum.NurseBot.commands;

import java.util.LinkedList;
import java.util.List;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import asylum.NurseBot.NurseNoakes;
import asylum.NurseBot.Sender;
import asylum.NurseBot.objects.Permission;
import asylum.NurseBot.objects.Visibility;
import asylum.NurseBot.utils.StringTools;

public class CommandHandler {

	private NurseNoakes nurse;
	
	private List<CommandInterpreter> commands;
	
	public CommandHandler(NurseNoakes nurse) {
		commands = new LinkedList<>();
		this.nurse = nurse;
		
		commands.add(new CommandInterpreter(null)
				.setName("help")
				.setInfo("zeigt diese Hilfe an")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.USER)
				.setAction(c -> {
					try {
						c.getSender().send(getHelp(), true);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}));
	}
	
	public String getHelp() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Hier ist eine Liste von Dingen, die ich kann:\n\n");
		
		CommandCategory current = null;
		
		for(CommandInterpreter command : commands) {
			if (command.getVisibility() == Visibility.PRIVATE)
				continue;
			
			if (command.getCategory() != current) {
				current = command.getCategory();
				
				if (current == null) {
					builder.append("\n");
				} else {
					builder.append("\n").append(StringTools.makeBold(current.getName())).append("\n");
				}
			}
			
			builder.append("/").append(command.getName()).append(" - ").append(command.getInfo()).append("\n");
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
		
		CommandInterpreter command = null;
		for (CommandInterpreter c : commands) {
			if (c.getName().equals(token) || (c.getName() + "@" + NurseNoakes.USERNAME).equals(token)) {
				command = c;
				break;
			}
		}
		if (command == null) {
			System.out.println("Command not found: " + token);
			return;
		}
		
		if (!nurse.isActive(command.getModule())) {
			System.out.println("Module is inactive.");
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
		try {
			command.getAction().action(context);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	
	public void add(CommandInterpreter command) {
		commands.add(command);
	}

	public int getNumberOfEntities() {
		return commands.size();
	}
}

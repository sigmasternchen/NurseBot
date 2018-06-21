package asylum.nursebot.modules;

import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import org.telegram.telegrambots.api.objects.User;

import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.objects.Locality;
import asylum.nursebot.objects.Module;
import asylum.nursebot.objects.Permission;
import asylum.nursebot.semantics.SemanticInterpreter;
import asylum.nursebot.semantics.SemanticsHandler;
import asylum.nursebot.semantics.WakeWord;
import asylum.nursebot.semantics.WakeWordType;
import asylum.nursebot.utils.StringTools;
import asylum.nursebot.NurseNoakes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Greeter implements Module {

	private static final String GREETING = 
			"Hallo %s!\n" +
			"Willkommen in der Irrenanstalt für Informatiker!\n\n" + 
			"Um 16 Uhr beginnt die Pillenausgabe mit anschließendem Codeschnipselraten.";
	
	private static final String BYE =
			"Ciao %s!";
	
	private SemanticsHandler SemanticsHandler;
	
	public Greeter() {
	}
	
	private String getNewUserString(List<User> users) {
		StringBuilder builder = new StringBuilder();
		Iterator<User> iterator = users.iterator();
		boolean first = true;
		while (iterator.hasNext()) {
			User user = iterator.next();
			if (first) {
				first = false;
			} else {
				if (iterator.hasNext()) {
					builder.append(", ");
				} else {
					builder.append(" und ");
				}
			}
			builder.append(StringTools.makeMention(user));
		}
		
		Formatter formatter = new Formatter();
		formatter.format(GREETING, builder.toString());
		
		String string = formatter.toString();
		formatter.close();
		
		return string;
	}
	
	private String getLeftUserString(User user) {
		Formatter formatter = new Formatter();
		formatter.format(BYE, user.getFirstName());
		
		String string = formatter.toString();
		formatter.close();
		
		return string;
	}
	
	@Override
	public String getName() {
		return "Greeter";
	}

	@Override
	public boolean isCommandModule() {
		return false;
	}

	@Override
	public boolean isSemanticModule() {
		return true;
	}

	@Override
	public boolean needsNurse() {
		return false;
	}

	@Override
	public void setNurse(NurseNoakes nurse) {
		throw new NotImplementedException();
	}

	@Override
	public void setCommandHandler(CommandHandler commandHandler) {
		throw new NotImplementedException();
	}

	@Override
	public void setSemanticsHandler(SemanticsHandler semanticHandler) {
		this.SemanticsHandler = semanticHandler;
	}

	@Override
	public void init() {
		SemanticsHandler.add(new SemanticInterpreter(this)
			.addWakeWord(new WakeWord(null, WakeWordType.META))
			.setLocality(Locality.GROUPS)
			.setPermission(Permission.ANY)
			.setAction(c -> {
				if (c.getMessage().getNewChatMembers() != null) {
					System.out.println("New Users: " + c.getMessage().getNewChatMembers());
					if (c.getMessage().getNewChatMembers().stream().anyMatch(u -> u.getUserName() != null && u.getUserName().equals(NurseNoakes.USERNAME))) {
						return;
					} else {
						c.getSender().send(getNewUserString(c.getMessage().getNewChatMembers()), true);
					}
				}
			}));
		
		SemanticsHandler.add(new SemanticInterpreter(this)
				.addWakeWord(new WakeWord(null, WakeWordType.META))
				.setLocality(Locality.GROUPS)
				.setPermission(Permission.ANY)
				.setAction(c -> {
					if (c.getMessage().getLeftChatMember() != null) {
						System.out.println("Left User: " + c.getMessage().getLeftChatMember());
						
						c.getSender().send(getLeftUserString(c.getMessage().getLeftChatMember()), true);
					}
				}));
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}

	@Override
	public void shutdown() {
	}
}

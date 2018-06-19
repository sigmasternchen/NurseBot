package asylum.NurseBot;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.api.objects.User;

public class StringManager {
	public String getNewUserString(List<User> users) {
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
			builder.append(makeMention(user));
		}
		
		return "Hallo " + builder.toString() + "!\nWillkommen in der Irrenanstalt für Informatiker!\n\nUm 16 Uhr beginnt die Pillenausgabe mit anschließendem Codeschnipselraten.";
	}
	
	public String makeMention(User user) {
		return user.getUserName() == null ? makeLink(user.getFirstName(), "tg://user?id=" + user.getId()) : ("@" + user.getUserName());
	}
	
	public String makeBold(String text) {
		return "*" + text + "*";
	}
	
	public String makeItalic(String text) {
		return "_" + text + "_";
	}
	
	public String makeCode(String text) {
		return "`" + text + "`";
	}
	
	public String makeCodeMultiline(String text) {
		return "```\n" + text + "\n```";
	}
	
	public String makeLink(String text, String url) {
		return "[" + text + "](" + url + ")";
	}
	
	public List<String> tokenize(String text) {
		List<String> list = new LinkedList<>();
		
		Matcher m = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(text);
		while (m.find()) {
			if (m.group(1) != null)
				list.add(m.group(1));
			else
				list.add(m.group(2));
		}
		    
		return list;
	}
}
package asylum.nursebot.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.api.objects.User;

public class StringTools {
	
	public static String makeMention(User user) {
		return user.getUserName() == null ? makeLink(user.getFirstName(), "tg://user?id=" + user.getId()) : ("@" + user.getUserName());
	}
	
	public static String makeBold(String text) {
		return "*" + text + "*";
	}
	
	public static String makeItalic(String text) {
		return "_" + text + "_";
	}
	
	public static String makeCode(String text) {
		return "`" + text + "`";
	}
	
	public static String makeCodeMultiline(String text) {
		return "```\n" + text + "\n```";
	}
	
	public static String makeLink(String text, String url) {
		return "[" + text + "](" + url + ")";
	}
	
	public static List<String> tokenize(String text) {
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

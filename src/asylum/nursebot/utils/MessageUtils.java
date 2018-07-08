package asylum.nursebot.utils;

import java.util.LinkedList;
import java.util.List;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;
import org.telegram.telegrambots.api.objects.User;

public class MessageUtils {
	public static List<User> getMentionedUsers(Message message) {
		List<User> list = new LinkedList<>();
		
		for (MessageEntity entity : message.getEntities()) {
			if (!entity.getType().equals("text_mention"))
				continue;
			list.add(entity.getUser());
		}
		
		return list;
	}
}

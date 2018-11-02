package asylum.nursebot.semantics;

import org.telegram.telegrambots.exceptions.TelegramApiException;

@FunctionalInterface
public interface SemanticAction {
	void action(SemanticContext context) throws TelegramApiException;
}

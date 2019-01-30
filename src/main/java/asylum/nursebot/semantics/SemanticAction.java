package asylum.nursebot.semantics;


import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FunctionalInterface
public interface SemanticAction {
	void action(SemanticContext context) throws TelegramApiException;
}

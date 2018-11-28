package asylum.nursebot.executor;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ErrorCallback extends BiConsumer<Exception, CallbackContext>{
}

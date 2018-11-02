package asylum.nursebot.executor;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface ExitCallback extends BiConsumer<ExitCode, CallbackContext>{

}

package asylum.nursebot.utils;

import asylum.nursebot.utils.log.Logger;

public class ThreadHelper {

	private static Logger logger = Logger.getModuleLogger("ThreadHelper");

	public static void ignore(Class<? extends Exception> clazz, Action action) {
		ignore(clazz, true, action);
	}
	
	public static void ignore(Class<? extends Exception> clazz, boolean stackTrace, Action action) {
		try {
			action.action();
		} catch(Exception e) {
			if (clazz.isInstance(e)) {
				if (stackTrace)
					logger.exception(e);
			} else {
				logger.warn("Exception in ignore-clause (stacktrace hidden).");
			}
		}
	}

	public static void delay(Action action, long delay) {
		new Thread(() -> {
			ignore(InterruptedException.class, () -> Thread.sleep(delay));
			ignore(Exception.class, action);
		}).start();
	}
}

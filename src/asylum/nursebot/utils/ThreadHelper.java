package asylum.nursebot.utils;

public class ThreadHelper {
	
	public static void ignore(Class<? extends Exception> clazz, Action action) {
		ignore(clazz, true, action);
	}
	
	public static void ignore(Class<? extends Exception> clazz, boolean stackTrace, Action action) {
		try {
			action.action();
		} catch(Exception e) {
			if (clazz.isInstance(e)) {
				if (stackTrace)
					e.printStackTrace();
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	public static void delay(Action action, long delay) {
		new Thread(() -> {
			ignore(InterruptedException.class, () -> Thread.sleep(delay));
			ignore(Exception.class, action);
		}).run();
	}
}

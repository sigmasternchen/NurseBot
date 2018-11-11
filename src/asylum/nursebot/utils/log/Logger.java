package asylum.nursebot.utils.log;

public abstract class Logger {
	public final static int DEBUG = -1;
	public final static int VERBOSE = 0;
	public final static int INFO = 1;
	public final static int WARNING = 2;
	public final static int ERROR = 3;
	public final static int EXCEPTION = 3;
	public final static int CRITICAL = 4;

	protected final static int DEFAULT_VERBOSITY = INFO;

	protected enum LogColor {
		BLACK (31), RED (31), GREEN (32), YELLOW (33), BLUE (34), MAGENTA (35), CYAN (36), WHITE (37);

		private int value;

		LogColor(int value) {
			this.value = value;
		}

		public String startANSI() {
			return "\033[" + value + "m";
		}

		public String endANSI() {
			return "\033[0m";
		}

		public String blink() {
			return "\033[1m";
		}
	}

	private static LoggerImpl instance;

	public static LoggerImpl getInstance() {
		if (instance == null)
			instance = new LoggerImpl();
		return instance;
	}

	public static Logger getWrapper(String module) {
		return new LoggerWrapper(getInstance(), module);
	}

	public abstract void log(int verbosity, String msg);
	public abstract void log(int verbosity, String module, String msg);

	public abstract void debug(String msg);
	public abstract void debug(String module, String msg);

	public abstract void verbose(String msg);
	public abstract void verbose(String module, String msg);

	public abstract void info(String msg);
	public abstract void info(String module, String msg);

	public abstract void warn(String msg);
	public abstract void warn(String module, String msg);

	public abstract void error(String msg);
	public abstract void error(String module, String msg);

	public abstract void critical(String msg);
	public abstract void critical(String module, String msg);

	public abstract void exception(Exception exception);
	public abstract void exception(String module, Exception exception);

}

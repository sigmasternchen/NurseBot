package asylum.nursebot.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.Instant;

public class Logger {
	private PrintStream logfile;
	private PrintStream stdout;
	private int verbosity;
	private Action critical;

	public final static int DEBUG = -1;
	public final static int VERBOSE = 0;
	public final static int INFO = 1;
	public final static int WARNING = 2;
	public final static int ERROR = 3;
	public final static int CRITICAL = 4;

	private final static int DEFAULT_VERBOSITY = INFO;

	private enum LogColor {
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

	private static Logger instance;

	public static Logger getInstance() {
		if (instance == null)
			instance = new Logger();
		return instance;
	}

	private Logger() {
		verbosity = DEFAULT_VERBOSITY;
		stdout = System.out;
		logfile = new PrintStream( new NullOutputStream());
	}

	public void setCrticalAction(Action critical) {
		this.critical = critical;
	}

	public void setVerbosity(int verbosity) {
		this.verbosity = verbosity;
	}

	public void setStdout(PrintStream stdout) {
		this.stdout = stdout;
	}

	public void setLogfile(File logfile) throws FileNotFoundException {
		this.logfile = new PrintStream(logfile);

		info("logger", "The logfile is: " + logfile.getAbsolutePath());
	}

	public void setLogfileStream(PrintStream logfile) {
		this.logfile = logfile;
	}

	private LogColor getColorForVerbosity(int verbosity) {
		switch (verbosity) {
			case DEBUG:
				return LogColor.CYAN;
			case VERBOSE:
				return LogColor.MAGENTA;
			case INFO:
				return LogColor.BLUE;
			case WARNING:
				return LogColor.YELLOW;
			case ERROR:
				return LogColor.RED;
			case CRITICAL:
				return LogColor.RED;
			default:
				return LogColor.WHITE;
		}
	}

	private String getStringForVerbosity(int verbosity) {
		switch (verbosity) {
			case DEBUG:
				return "debug";
			case VERBOSE:
				return "verbose";
			case INFO:
				return "info";
			case WARNING:
				return "warning";
			case ERROR:
				return "error";
			case CRITICAL:
				return "critical";
			default:
				return "?";
		}
	}

	private String formatMsg(int verbosity, boolean useColor, Instant instant, String module, String msg) {
		StringBuilder builder = new StringBuilder();

		builder.append(instant).append(" ");

		builder.append("[");
		if (useColor) {
			LogColor color = getColorForVerbosity(verbosity);
			builder.append(color.startANSI());
			if (verbosity == Logger.CRITICAL)
				builder.append(color.blink());
			builder.append(getStringForVerbosity(verbosity));
			builder.append(color.endANSI());
		} else {
			builder.append(getStringForVerbosity(verbosity));
		}
		builder.append("] ");

		builder.append("[").append(module).append("] ");
		builder.append(msg);

		return builder.toString();
	}

	private void toLogfile(int verbosity, Instant instant, String module, String msg) {
		logfile.println(formatMsg(verbosity, false, instant, module, msg));
	}

	private void toStdout(int verbosity, Instant instant, String module, String msg) {
		stdout.println(formatMsg(verbosity, true, instant, module, msg));
	}

	public synchronized void log(int verbosity, String module, String msg) {
		if (verbosity < this.verbosity)
			return;

		Instant instant = Instant.now();
		toLogfile(verbosity, instant, module, msg);
		toStdout(verbosity, instant, module, msg);
	}

	public void debug(String module, String msg) {
		log(DEBUG, module, msg);
	}

	public void verbose(String module, String msg) {
		log(VERBOSE, module, msg);
	}

	public void info(String module, String msg) {
		log(INFO, module, msg);
	}

	public void warn(String module, String msg) {
		log(WARNING, module, msg);
	}

	public void error(String module, String msg) {
		log(ERROR, module, msg);
	}

	public synchronized void exception(Exception exception) {
		exception.printStackTrace(logfile);
		exception.printStackTrace(stdout);
	}

	public void critical(String module, String msg) {
		log(CRITICAL, module, msg);
		if (critical != null)
			ThreadHelper.ignore(Exception.class, critical);
	}
}

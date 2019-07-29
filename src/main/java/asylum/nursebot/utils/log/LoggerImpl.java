package asylum.nursebot.utils.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.Instant;

import asylum.nursebot.utils.Action;
import asylum.nursebot.utils.NullOutputStream;
import asylum.nursebot.utils.ThreadHelper;

public class LoggerImpl extends Logger {
	private PrintStream logfile;
	private PrintStream stdout;
	private int verbosity;
	private Action critical;

	private static final String DEFAULT_MODULE = "-";

	protected LoggerImpl() {
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

		info("LoggerImpl", "The logfile is: " + logfile.getAbsolutePath());
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
			if (verbosity == LoggerImpl.CRITICAL)
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

	private synchronized void toLogfile(int verbosity, Instant instant, String module, String msg) {
		logfile.println(formatMsg(verbosity, false, instant, module, msg));
	}

	private synchronized void toStdout(int verbosity, Instant instant, String module, String msg) {
		stdout.println(formatMsg(verbosity, true, instant, module, msg));
	}

	@Override
	public void log(int verbosity, String msg) {
		log(verbosity, DEFAULT_MODULE, msg);
	}

	public void log(int verbosity, String module, String msg) {
		if (verbosity < this.verbosity)
			return;

		Instant instant = Instant.now();
		toLogfile(verbosity, instant, module, msg);
		toStdout(verbosity, instant, module, msg);
	}

	@Override
	public void debug(String msg) {
		debug(DEFAULT_MODULE, msg);
	}

	public void debug(String module, String msg) {
		log(DEBUG, module, msg);
	}

	@Override
	public void verbose(String msg) {
		verbose(DEFAULT_MODULE, msg);
	}

	public void verbose(String module, String msg) {
		log(VERBOSE, module, msg);
	}

	@Override
	public void info(String msg) {
		info(DEFAULT_MODULE, msg);
	}

	public void info(String module, String msg) {
		log(INFO, module, msg);
	}

	@Override
	public void warn(String msg) {
		warn(DEFAULT_MODULE, msg);
	}

	public void warn(String module, String msg) {
		log(WARNING, module, msg);
	}

	@Override
	public void error(String msg) {
		error(DEFAULT_MODULE, msg);
	}

	public void error(String module, String msg) {
		log(ERROR, module, msg);
	}

	@Override
	public void critical(String msg) {
		critical(DEFAULT_MODULE, msg);
	}

	public void exception(Exception exception) {
		exception(DEFAULT_MODULE, exception);
	}

	@Override
	public synchronized void exception(String module, Exception exception) {

		log(EXCEPTION, module, exception.getClass().getCanonicalName());
		exception.printStackTrace(logfile);
		exception.printStackTrace(stdout);
	}

	public void critical(String module, String msg) {
		log(CRITICAL, module, msg);
		if (critical != null)
			ThreadHelper.ignore(Exception.class, critical);
	}
}

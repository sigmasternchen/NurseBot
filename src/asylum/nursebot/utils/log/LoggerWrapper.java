package asylum.nursebot.utils.log;

public class LoggerWrapper extends Logger {
	private LoggerImpl logger;
	private String module;

	protected LoggerWrapper(LoggerImpl logger, String module) {
		this.logger = logger;
		this.module = module;
	}

	public void log(int verbosity, String msg) {
		logger.log(verbosity, module, msg);
	}

	@Override
	public void log(int verbosity, String module, String msg) {
		logger.log(verbosity, module, msg);
	}

	public void debug(String msg) {
		logger.debug(module, msg);
	}

	@Override
	public void debug(String module, String msg) {
		logger.debug(module, msg);
	}

	public void verbose(String msg) {
		logger.verbose(module, msg);
	}

	@Override
	public void verbose(String module, String msg) {
		logger.verbose(module, msg);

	}

	public void info(String msg) {
		logger.info(module, msg);
	}

	@Override
	public void info(String module, String msg) {
		logger.info(module, msg);

	}

	public void warn(String msg) {
		logger.warn(module, msg);
	}

	@Override
	public void warn(String module, String msg) {
		logger.warn(module, msg);
	}

	public void error(String msg) {
		logger.error(module, msg);
	}

	@Override
	public void error(String module, String msg) {
		logger.error(module, msg);
	}

	public void critical(String msg) {
		logger.critical(module, msg);
	}

	@Override
	public void critical(String module, String msg) {
		logger.critical(module, msg);
	}

	@Override
	public void exception(Exception exception) {
		logger.exception(module, exception);
	}

	@Override
	public void exception(String module, Exception exception) {
		logger.exception(module, exception);
	}
}
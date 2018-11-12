package asylum.nursebot.persistence;

import asylum.nursebot.persistence.selfbuilding.Selfbuilding;
import asylum.nursebot.utils.log.Logger;

public class ModelManager {
	private static boolean wasAnythingCreated = false;

	private static Logger logger = Logger.getModuleLogger("ModelManager");
	
	static public <T extends Selfbuilding> void build(Class<T> c) {
		logger.info("Building model class " + c.getCanonicalName() + "...");
		try {
			if (c.newInstance().selfbuild())
				wasAnythingCreated = true;
		} catch (IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static boolean wasAnythingCreated() {
		return wasAnythingCreated;
	}
}

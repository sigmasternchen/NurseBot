package asylum.nursebot.persistence;

import java.lang.reflect.InvocationTargetException;

import asylum.nursebot.persistence.selfbuilding.Selfbuilding;
import asylum.nursebot.utils.log.Logger;

public class ModelManager {
	private static boolean wasAnythingCreated = false;

	private static Logger logger = Logger.getModuleLogger("ModelManager");

	public static <T extends Selfbuilding> void build(Class<T> c) {
		logger.info("Building model class " + c.getCanonicalName() + "...");
		try {
			if (c.getConstructor().newInstance().selfbuild())
				wasAnythingCreated = true;
		} catch (IllegalAccessException | InstantiationException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean wasAnythingCreated() {
		return wasAnythingCreated;
	}
}

package asylum.NurseBot.persistence;

import asylum.NurseBot.persistence.selfbuilding.Selfbuilding;

public class ModelManager {
	private static boolean wasAnythingCreated = false;
	
	static public <T extends Selfbuilding> void build(Class<T> c) {
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

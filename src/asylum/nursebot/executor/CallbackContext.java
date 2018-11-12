package asylum.nursebot.executor;

import asylum.nursebot.exceptions.WhatTheFuckException;

import java.util.HashMap;

public class CallbackContext extends HashMap<Class<?>, Object>{
	private static final long serialVersionUID = 1988125468800948893L;

	public void put(Object object) {
		put(object.getClass(), object);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz) {
		Object obj = ((HashMap<Class<?>, Object>) this).get(clazz);
		if (!(clazz.isInstance(obj)))
			throw new WhatTheFuckException("WAT?");
		return (T) obj;
	}
}

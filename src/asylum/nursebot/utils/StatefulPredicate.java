package asylum.nursebot.utils;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class StatefulPredicate<T, S> implements Predicate<S> {
	private T state;
	private BiPredicate<T, S> predicate;

	public StatefulPredicate(T initState, BiPredicate<T, S> predicate) {
		state = initState;
		this.predicate = predicate;
	}

	@Override
	public boolean test(S s) {
		return predicate.test(state, s);
	}
}

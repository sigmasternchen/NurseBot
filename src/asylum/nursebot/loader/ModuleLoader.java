package asylum.nursebot.loader;

import java.util.Set;

import org.reflections.Reflections;

import com.google.inject.Guice;
import com.google.inject.Injector;

import asylum.nursebot.NurseNoakes;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.objects.Module;
import asylum.nursebot.semantics.SemanticsHandler;

public class ModuleLoader {
	private DependencyProvider provider;
	
	public ModuleLoader(NurseNoakes nurse, CommandHandler commandHandler, SemanticsHandler semanticsHandler) {
		this.provider = new DependencyProvider(nurse, commandHandler, semanticsHandler);
	}
	
	
	public void loadAll(ModuleHandler handler) {
		Reflections reflections = new Reflections("asylum.nursebot");
		Set<Class<?>> list = reflections.getTypesAnnotatedWith(AutoModule.class);
		for (Class<?> clazz : list) {
			if (!Module.class.isAssignableFrom(clazz))
				throw new RuntimeException(clazz.getCanonicalName() + " is annotated but not a module.");
			AutoModule annotation = clazz.getAnnotation(AutoModule.class);
			if (annotation == null)
				throw new RuntimeException("HOW?! " + clazz.getCanonicalName());
			if (annotation.load()) {
				Module module = inject(clazz);
				handler.handle(module);
			}
		}
	}

	private Module inject(Class<?> clazz) {
		Injector injector = Guice.createInjector(provider);
		return (Module) injector.getInstance(clazz);
	}
}

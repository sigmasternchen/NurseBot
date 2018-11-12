package asylum.nursebot.loader;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import asylum.nursebot.utils.log.Logger;
import org.reflections.Reflections;

import com.google.inject.Guice;
import com.google.inject.Injector;

import asylum.nursebot.NurseNoakes;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.objects.Module;
import asylum.nursebot.semantics.SemanticsHandler;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

public class ModuleLoader {
	private List<Provider> providers;
	
	private List<Class<? extends Module>> dependencyClasses;
	
	private ModuleDependencies dependencies;

	private Logger logger = Logger.getModuleLogger("ModuleLoader");

	private Reflections reflections = new Reflections("asylum.nursebot");

	public ModuleLoader(NurseNoakes nurse, CommandHandler commandHandler, SemanticsHandler semanticsHandler) {
		this.providers = new LinkedList<>();
		this.dependencyClasses = new LinkedList<>();
		this.dependencies = new ModuleDependencies();
		
		this.providers.add(new BaseProvider(nurse, commandHandler, semanticsHandler, dependencies));
	}
	
	@SuppressWarnings("unchecked")
	public void loadDependencies() {
		Set<Class<?>> list = reflections.getTypesAnnotatedWith(AutoDependency.class);
		for (Class<?> clazz : list) {
			if (Module.class.isAssignableFrom(clazz)) {
				dependencyClasses.add((Class<? extends Module>) clazz);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadModules(ModuleHandler handler) {
		Injector injector = Guice.createInjector(providers);

		Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoModule.class);

		Set<Class<?>> dependenciesToLoad = new HashSet<Class<?>>();
		Set<Class<?>> regularModulesToLoad = new HashSet<Class<?>>();

		for (Class<?> clazz : annotatedClasses) {
			if (!Module.class.isAssignableFrom(clazz)) {
				logger.error(clazz.getCanonicalName() + " is annotated but not a module.");
				continue;
			}
			AutoModule annotation = clazz.getAnnotation(AutoModule.class);
			if (annotation == null) {
				logger.error("Cannot get annotation object from annotated class " + clazz.getCanonicalName() + ". This should not happen.");
				continue;
			}
			if (annotation.load()) {
				if (dependencyClasses.contains(clazz))
					dependenciesToLoad.add(clazz);
				else
					regularModulesToLoad.add(clazz);
			}
		}

		for (Class<?> clazz : dependenciesToLoad) {
			logger.verbose("Loading module class " + clazz.getCanonicalName() + ".");
			Module module = (Module) injector.getInstance(clazz);
			handler.handle(module);

			logger.verbose("Adding " + clazz.getCanonicalName() + " as dependency.");
			dependencies.put((Class<? extends Module>) clazz, module);
		}

		for (Class<?> clazz : regularModulesToLoad) {
			logger.verbose("Loading module class " + clazz.getCanonicalName() + ".");
			Module module = (Module) injector.getInstance(clazz);
			handler.handle(module);
		}
	}
}

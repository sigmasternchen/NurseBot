package asylum.nursebot.loader;

import com.google.inject.AbstractModule;

import asylum.nursebot.NurseNoakes;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.semantics.SemanticsHandler;

public class DependencyProvider extends AbstractModule {
	
	private NurseNoakes nurse;
	private CommandHandler commandHandler;
	private SemanticsHandler semanticsHandler;

	public DependencyProvider(NurseNoakes nurse, CommandHandler commandHandler, SemanticsHandler semanticsHandler) {
		this.nurse = nurse;
		this.commandHandler = commandHandler;
		this.semanticsHandler = semanticsHandler;
	}

	@Override
	protected void configure() {
		bind(NurseNoakes.class).toInstance(nurse);
		bind(CommandHandler.class).toInstance(commandHandler);
		bind(SemanticsHandler.class).toInstance(semanticsHandler);
	}

}

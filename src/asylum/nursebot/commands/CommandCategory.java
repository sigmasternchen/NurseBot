package asylum.nursebot.commands;

public class CommandCategory {
	private String name;

	public CommandCategory(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}


	public boolean equals(Object object) {
		if (!(object instanceof CommandCategory))
			return false;
		CommandCategory category = (CommandCategory) object;

		return name.equals(category.name);
	}

}

package asylum.NurseBot.commands;

import asylum.NurseBot.utils.Locality;
import asylum.NurseBot.utils.Permission;
import asylum.NurseBot.utils.Visibility;

public class CommandInterpreter {
	private String name = "notset";
	private String info = "";
	private CommandCategory category = null;
	private Visibility visibility = Visibility.PRIVATE;
	private Permission permission = Permission.OWNER;
	private Locality locality = Locality.EVERYWHERE;
	private boolean pausable = true;
	private CommandAction action = (c -> System.out.println("Action not defined."));
	
	public CommandInterpreter setName(String name) {
		this.name = name;
		return this;
	}
	public CommandInterpreter setInfo(String info) {
		this.info = info;
		return this;
	}
	public CommandInterpreter setVisibility(Visibility visibility) {
		this.visibility = visibility;
		return this;
	}
	public CommandInterpreter setPermission(Permission permission) {
		this.permission = permission;
		return this;
	}
	public CommandInterpreter setLocality(Locality locality) {
		this.locality = locality;
		return this;
	}
	public CommandInterpreter setAction(CommandAction action) {
		this.action = action;
		return this;
	}
	public CommandInterpreter setPausable(boolean pausable) {
		this.pausable = pausable;
		return this;
	}
	public CommandInterpreter setCategory(CommandCategory category) {
		this.category = category;
		return this;
	}
	
	
	public String getName() {
		return name;
	}
	public String getInfo() {
		return info;
	}
	public Visibility getVisibility() {
		return visibility;
	}
	public Permission getPermission() {
		return permission;
	}
	public Locality getLocality() {
		return locality;
	}
	public CommandAction getAction() {
		return action;
	}
	public CommandCategory getCategory() {
		return category;
	}
	
	public boolean isPausable() {
		return pausable;
	}
}

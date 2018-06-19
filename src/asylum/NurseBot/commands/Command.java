package asylum.NurseBot.commands;


public class Command {
	private String name = "notset";
	private String info = "";
	private CommandCategory category = null;
	private Visibility visibility = Visibility.PRIVATE;
	private Permission permission = Permission.OWNER;
	private Locality locality = Locality.EVERYWHERE;
	private boolean pausable = true;
	private Action action = (c -> System.out.println("Action not defined."));
	
	public Command setName(String name) {
		this.name = name;
		return this;
	}
	public Command setInfo(String info) {
		this.info = info;
		return this;
	}
	public Command setVisibility(Visibility visibility) {
		this.visibility = visibility;
		return this;
	}
	public Command setPermission(Permission permission) {
		this.permission = permission;
		return this;
	}
	public Command setLocality(Locality locality) {
		this.locality = locality;
		return this;
	}
	public Command setAction(Action action) {
		this.action = action;
		return this;
	}
	public Command setPausable(boolean pausable) {
		this.pausable = pausable;
		return this;
	}
	public Command setCategory(CommandCategory category) {
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
	public Action getAction() {
		return action;
	}
	public CommandCategory getCategory() {
		return category;
	}
	
	public boolean isPausable() {
		return pausable;
	}
}

package asylum.nursebot.persistence.selfbuilding;

public enum Key {
	NONE, KEY, UNIQUE, UNIQUE_KEY, PRIMARY_KEY;
	
	public String toString() {
		switch (this) {
		case KEY:
			return "KEY";
		case NONE:
			return "";
		case PRIMARY_KEY:
			return "PRIMARY KEY";
		case UNIQUE_KEY:
			return "UNIQUE KEY";
		case UNIQUE:
			return "UNIQUE";
		default:
			return "";
		}
	}
}

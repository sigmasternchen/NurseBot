package asylum.nursebot.modules.birthdays;

public enum Privacy {
	PRIVATE ('-'), PUBLIC ('p'), PUBLIC_AGE ('a'), PUBLIC_DATE ('d');

	private char character;

	Privacy(char c) {
		this.character = c;
	}

	public char getCharacter() {
		return character;
	}

	public static Privacy fromCharacter(char c) {
		for (Privacy privacy : Privacy.values()) {
			if (privacy.character == c)
				return privacy;
		}
		return null;
	}

	public static Privacy fromString(String name) {
		name = name.toLowerCase().replace('-', '_');
		for (Privacy privacy : Privacy.values()) {
			if (privacy.name().toLowerCase().equals(name))
				return privacy;
		}
		return null;
	}
}

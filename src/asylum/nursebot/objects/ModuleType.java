package asylum.nursebot.objects;

public class ModuleType {
	public static int COMMAND_MODULE = 1 << 0;
	public static int SEMANTIC_MODULE = 1 << 1;
	
	private int value = 0;
	
	public ModuleType set(int flag) {
		this.value |= flag;
		return this;
	}
	
	public boolean is(int flag) {
		return ((this.value & flag) != 0);
	}
	
	private char getTypeChar(int position) {
		switch (position) {
		case 0:
			return 'C';
		case 1:
			return 'S';
		default:
			return '?';
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; 1 << i <= value; i++) {
			if ((1 << i & value) != 0)
				builder.append(getTypeChar(i));
		}
		
		return builder.toString();
	}
}

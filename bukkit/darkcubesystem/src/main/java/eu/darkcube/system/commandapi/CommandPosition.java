package eu.darkcube.system.commandapi;

public class CommandPosition {
	
	private int level;
	
	protected CommandPosition(int level) {
		this.level = level;
	}
	
	protected int getPosition() {
		return level;
	}
	
	public final CommandPosition next() {
		return new CommandPosition(level + 1);
	}
	
	protected CommandPosition getNext() {
		return new CommandPosition(level + 1);
	}
}

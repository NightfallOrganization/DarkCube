package eu.darkcube.system.commandapi;

public class Argument {

	private boolean needed;
	private String name;
	private String beschreibung;
	
	public Argument(String name, String beschreibung) {
		this(name, beschreibung, true);
	}
	
	public Argument(String name, String beschreibung, boolean needed) {
		this.beschreibung = beschreibung;
		this.needed = needed;
		this.name = name;
	}
	
	public String getDescription() {
		return beschreibung;
	}
	
	public boolean isNeeded() {
		return needed;
	}
	
	public String getName() {
		return name;
	}
}

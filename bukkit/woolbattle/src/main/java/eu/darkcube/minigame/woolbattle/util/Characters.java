package eu.darkcube.minigame.woolbattle.util;

public enum Characters {

	SHIFT_SHIFT_LEFT("«"),
	SHIFT_SHIFT_RIGHT("»"),
	HEART("❤");
	
	;
	
	private String c;
	
	private Characters(String c) {
		this.c = c;
	}
	
	public String getChar() {
		return c;
	}
	@Override
	public String toString() {
		return getChar();
	}
}

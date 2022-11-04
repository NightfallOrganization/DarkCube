package de.pixel.bedwars.util;

public enum ScoreboardTeam {

	LOBBY_TIMER("LOBBY_TIMER", "§1§b"),
	LOBBY_GOLD("LOBBY_GOLD", "§2§r"),
	LOBBY_IRON("LOBBY_IRON", "§3§r"),
	LOBBY_MAPS("LOBBY_MAPS", "§4§d"),
	
	ENDGAME_TIMER("ENDGAME_TIMER", "§5§b"),

	;

	private String tag;
	private String entry;
	private String prefixMessage;

	private ScoreboardTeam(String prefixMessage, String entry) {
		tag = name();
		this.entry = entry;
		this.prefixMessage = prefixMessage;
	}

	public String getEntry() {
		return entry;
	}

	public String getPrefixMessage() {
		return prefixMessage;
	}

	public String getTag() {
		return tag;
	}
}

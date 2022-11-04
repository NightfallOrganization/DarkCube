package eu.darkcube.minigame.woolbattle.util;

public enum ScoreboardObjective {

	LOBBY("lobby"), INGAME("ingame"), ENDGAME("endgame"),

	;

	private final String key;

	private ScoreboardObjective(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getMessageKey() {
		return "SCOREBOARD_OBJECTIVE_" + name();
	}
}

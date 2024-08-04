package eu.darkcube.minigame.woolbattle.common.scoreboard;

public enum CommonScoreboardObjective {
    LOBBY,
    INGAME,
    ENDGAME;
    private final String key;

    CommonScoreboardObjective() {
        this.key = name();
    }

    public String key() {
        return key;
    }

    public String messageKey() {
        return "SCOREBOARD_OBJECTIVE_" + key;
    }
}

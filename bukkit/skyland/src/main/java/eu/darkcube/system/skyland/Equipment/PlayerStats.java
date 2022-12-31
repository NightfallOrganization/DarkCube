package eu.darkcube.system.skyland.Equipment;

public class PlayerStats {

    PlayerStatsType type;
    int menge;

    public PlayerStats(PlayerStatsType type, int menge) {
        this.type = type;
        this.menge = menge;
    }

    public PlayerStatsType getType() {
        return type;
    }

    public int getMenge() {
        return menge;
    }

    public void setMenge(int menge) {
        this.menge = menge;
    }

    public void setType(PlayerStatsType type) {
        this.type = type;
    }
}

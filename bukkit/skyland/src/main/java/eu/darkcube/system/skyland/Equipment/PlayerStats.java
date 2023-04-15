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

    @Override
    public String toString() {

        return type + "``" + menge;
    }

    public static PlayerStats parseString(String s){

        return new PlayerStats(PlayerStatsType.valueOf(s.split("``")[0]), Integer.parseInt(s.split("``")[1]));
    }
}

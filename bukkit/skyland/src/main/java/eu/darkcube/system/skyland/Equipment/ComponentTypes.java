package eu.darkcube.system.skyland.Equipment;



public enum ComponentTypes {
    AXE(new PlayerStats[]{new PlayerStats(PlayerStatsType.STRENGHT, 3)})

    ;


    private PlayerStats[] stats;


    ComponentTypes(PlayerStats[] stats){
        this.stats = stats;
    }

    public PlayerStats[] getStats() {
        return stats;
    }
}


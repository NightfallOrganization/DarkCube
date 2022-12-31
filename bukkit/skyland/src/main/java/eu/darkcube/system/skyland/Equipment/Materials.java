package eu.darkcube.system.skyland.Equipment;

public enum Materials {

    DRAGON_SCALE(new PlayerStats[] {new PlayerStats(PlayerStatsType.STRENGHT, 10)})



    ;


    private PlayerStats[] stats;
    Materials(PlayerStats[] stats){
        this.stats = stats;
    }

}

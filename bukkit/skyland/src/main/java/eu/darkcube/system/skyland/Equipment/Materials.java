package eu.darkcube.system.skyland.Equipment;

public enum Materials {

    DRAGON_SCALE(new PlayerStats[] {new PlayerStats(PlayerStatsType.STRENGHT, 10)}, Rarity.RARE)



    ;


    private PlayerStats[] stats;
    private Rarity rarity;
    Materials(PlayerStats[] stats, Rarity rarity){
        this.stats = stats;
        this.rarity = rarity;
    }

}

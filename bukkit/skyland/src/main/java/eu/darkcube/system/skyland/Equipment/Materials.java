package eu.darkcube.system.skyland.Equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public enum Materials {

    DRAGON_SCALE(new PlayerStats[] {new PlayerStats(PlayerStatsType.STRENGHT, 10)}, Rarity.RARE)//beispiel



    ;


    private PlayerStats[] stats;
    private Rarity rarity;
    private static HashMap<Rarity, ArrayList<Materials>> materials = new HashMap<>();
    Materials(PlayerStats[] stats, Rarity rarity){
        this.stats = stats;
        this.rarity = rarity;

        if(!getMaterials().containsKey(rarity)){
            getMaterials().put(rarity, new ArrayList<>());
        }

        Materials.getMaterials().get(rarity).add(this);

    }

    public static HashMap<Rarity, ArrayList<Materials>> getMaterials() {
        return materials;
    }

    public static Materials getRandomMaterial(HashMap<Rarity, ArrayList<Materials>> materials){
        Rarity r = Rarity.rollRarity(materials);
        ArrayList<Materials> materials1 = materials.get(r);
        Random random = new Random();
        int i = random.nextInt(materials1.size());
        return materials1.get(i);
    }
}

package eu.darkcube.system.skyland.Equipment;

import java.util.ArrayList;
import java.util.Random;

public enum Rarity {

    RARE("&7", 1000)
    ;

    private String prefix;
    private int weight;
    private static ArrayList<Rarity> rarities = new ArrayList<>();

    Rarity(String prefix, int weight){
        this.prefix = prefix;
        this.weight = weight;
        Rarity.getRarities().add(this);
    }

    public static ArrayList<Rarity> getRarities() {
        return rarities;
    }

    public static Rarity rollRarity(){
        int totalWeight = 0;
        for (Rarity r : rarities){
            totalWeight+= r.weight;
        }

        Random random = new Random();
        random.nextInt(totalWeight);
        for (Rarity r: rarities) {
            if (totalWeight <= r.weight){
                return r;
            }
            totalWeight = totalWeight-r.weight;
        }

        //sollte nie auftreten!!!
        return null;
    }
}

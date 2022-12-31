package eu.darkcube.system.skyland.Equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public enum Rarity {

    ORDINARY("&a", 3500),
    RARE("&9", 1500),
    EPIC("&d", 500),
    MYTHIC("&3", 250),
    LEGENDARY("&6", 100),
    DIVINE("&e", 1),
    UNIGUE("&b", 0),
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

    public static Rarity rollRarity(HashMap<Rarity, ArrayList<Materials>> materials){

        int totalWeight = 0;
        for (Rarity r : materials.keySet()){
            totalWeight+= r.weight;
        }

        Random random = new Random();
        random.nextInt(totalWeight);
        for (Rarity r: materials.keySet()) {
            if (totalWeight <= r.weight){
                return r;
            }
            totalWeight = totalWeight-r.weight;
        }

        //sollte nie auftreten!!!
        return null;
    }
}

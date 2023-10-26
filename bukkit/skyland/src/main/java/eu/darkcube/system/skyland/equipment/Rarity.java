/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.equipment;

import java.util.Random;

public enum Rarity {

	ORDINARY("§a", 3500),
	RARE("§9", 1500),
	EPIC("§d", 500),
	MYTHIC("§3", 250),
	LEGENDARY("§6", 100),
	DIVINE("§e", 1),
	UNIGUE("§b", 0),
	;

	private String prefix;
	private int weight;

	//private static ArrayList<Rarity> rarities = new ArrayList<>();

	Rarity(String prefix, int weight) {
		this.prefix = prefix;
		this.weight = weight;
		//Rarity.getRarities().add(this);
	}

/*    public static ArrayList<Rarity> getRarities() {
        System.out.println("");
        if (rarities == null){
            System.out.println("rarities is null");
            rarities = new ArrayList<>();
        }else {
            System.out.println("rarities:");
            for (Rarity r : rarities) {
                System.out.println(r);
            }
        }
        System.out.println("");

        return rarities;
    }*/


	public static Rarity rollRarity(){
		int totalWeight = 0;
		for (Rarity value : values()) {
			totalWeight += value.getWeight();
		}

		Random random = new Random();
		int roll = random.nextInt(totalWeight);
		for (Rarity value : values()) {
			if (value.getWeight() >= roll){
				return value;
			}
			roll = roll -value.getWeight();
		}

		return Rarity.ORDINARY;
	}

	public static Rarity rollRarity(Rarity[] rarities){
		int totalWeight = 0;
		for (Rarity value : rarities) {
			totalWeight += value.getWeight();
		}

		Random random = new Random();
		int roll = random.nextInt(totalWeight);

		for (Rarity value : rarities) {
			if (value.getWeight() >= roll){
				return value;
			}
			roll = roll -value.getWeight();
		}

		return Rarity.ORDINARY;
	}
	public int getWeight() {
		return weight;
	}

	public String getPrefix() {
		return prefix;
	}
}

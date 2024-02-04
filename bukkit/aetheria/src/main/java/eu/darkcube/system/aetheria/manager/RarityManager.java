/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager;

public class RarityManager {

    public enum Rarity {
        ORDINARY("§a", 3500),
        RARE("§9", 1500),
        EPIC("§d", 500),
        MYTHIC("§3", 250),
        LEGENDARY("§6", 100),
        DIVINE("§e", 1),
        UNIQUE("§b", 0);

        private final String prefix;
        private final int weight;

        Rarity(String prefix, int weight) {
            this.prefix = prefix;
            this.weight = weight;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public int getWeight() {
            return this.weight;
        }

    }

}

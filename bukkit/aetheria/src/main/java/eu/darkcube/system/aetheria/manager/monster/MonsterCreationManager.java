/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

import eu.darkcube.system.aetheria.manager.RarityManager;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class MonsterCreationManager {
    public List<Monster> monsters = new ArrayList<>();

    public void createMonsterWithRarity(RarityManager.Rarity rarity, String name, EntityTypeManager.EntityType entityType, int minLevel, int maxLevel) {
        monsters.add(new Monster(name, rarity, entityType, minLevel, maxLevel));
    }

    public void createMultipleMonsters() {
        createMonsterWithRarity(RarityManager.Rarity.ORDINARY, "Monster1", EntityTypeManager.EntityType.ZOMBIE, 1, 20);
        createMonsterWithRarity(RarityManager.Rarity.DIVINE, "Monster2", EntityTypeManager.EntityType.SKELETON, 1, 20);
        createMonsterWithRarity(RarityManager.Rarity.ORDINARY, "Monster3", EntityTypeManager.EntityType.PILLAGER, 1, 30);
    }

    public Optional<Monster> getMonsterByEntityType(EntityTypeManager.EntityType entityType) {
        return monsters.stream()
                .filter(monster -> monster.entityType == entityType)
                .findFirst();
    }

    public class Monster {
        private String name;
        private RarityManager.Rarity rarity;
        private EntityTypeManager.EntityType entityType;
        private int minLevel;
        private int maxLevel;

        public Monster(String name, RarityManager.Rarity rarity, EntityTypeManager.EntityType entityType, int minLevel, int maxLevel) {
            this.name = name;
            this.rarity = rarity;
            this.entityType = entityType;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
        }

        public String getName() {
            return name;
        }

        public RarityManager.Rarity getRarity() {
            return rarity;
        }

        public List<Monster> getMonsters() {
            return new ArrayList<>(monsters);
        }

        public int getMinLevel() {
            return minLevel;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public EntityTypeManager.EntityType getEntityType() {
            return entityType;
        }

    }

    public List<Monster> getMonsters() {
        return new ArrayList<>(monsters);
    }

}
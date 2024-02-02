/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

import eu.darkcube.system.aetheria.manager.RarityManager;
import eu.darkcube.system.aetheria.other.GsonUtil;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MonsterCreationManager {
    public List<MonsterType> monsterTypes = new ArrayList<>();

    public void createMonsterWithRarity(RarityManager.Rarity rarity, String name, EntityTypeManager.EntityType entityType, int minLevel, int maxLevel) {
        monsterTypes.add(new MonsterType(name, rarity, entityType, minLevel, maxLevel));
    }

    public void createMultipleMonsters() {
        createMonsterWithRarity(RarityManager.Rarity.ORDINARY, "Monster1", EntityTypeManager.EntityType.ZOMBIE, 1, 20);
        createMonsterWithRarity(RarityManager.Rarity.DIVINE, "Monster2", EntityTypeManager.EntityType.SKELETON, 1, 20);
        createMonsterWithRarity(RarityManager.Rarity.ORDINARY, "Monster3", EntityTypeManager.EntityType.PILLAGER, 1, 30);
    }

    public Optional<MonsterType> getMonsterTypeByEntityType(EntityTypeManager.EntityType entityType) {
        return monsterTypes.stream()
                .filter(monsterType -> monsterType.entityType == entityType)
                .findFirst();
    }

    public static class MonsterType {
        public static final PersistentDataType<String, MonsterType> MONSTER_TYPE = GsonUtil.createDataType(MonsterType.class);
        private String name;
        private RarityManager.Rarity rarity;
        private EntityTypeManager.EntityType entityType;
        private int minLevel;
        private int maxLevel;

        private MonsterType(String name, RarityManager.Rarity rarity, EntityTypeManager.EntityType entityType, int minLevel, int maxLevel) {
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

    public List<MonsterType> getMonsters() {
        return new ArrayList<>(monsterTypes);
    }

}
/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class EntityTypeManager {

    public enum EntityType {
        ZOMBIE(org.bukkit.entity.EntityType.ZOMBIE, org.bukkit.entity.Zombie.class),
        SKELETON(org.bukkit.entity.EntityType.SKELETON, org.bukkit.entity.Skeleton.class),
        CREEPER(org.bukkit.entity.EntityType.SKELETON, org.bukkit.entity.Creeper.class),
        SPIDER(org.bukkit.entity.EntityType.SKELETON, org.bukkit.entity.Spider.class),
        PILLAGER(org.bukkit.entity.EntityType.PILLAGER, org.bukkit.entity.Pillager.class);

        private final org.bukkit.entity.EntityType bukkitType;
        private final Class<? extends org.bukkit.entity.Entity> entityClass;

        EntityType(org.bukkit.entity.EntityType bukkitType, Class<? extends Entity> entityClass) {
            this.bukkitType = bukkitType;
            this.entityClass = entityClass;
            Companion.TYPE_BY_BUKKIT.put(bukkitType, this);
        }

        public org.bukkit.entity.EntityType getBukkitType() {
            return bukkitType;
        }

        public Class<? extends org.bukkit.entity.Entity> getEntityClass() {
            return entityClass;
        }

        public static EntityType valueOf(org.bukkit.entity.EntityType bukkitType) {
            EntityType type = Companion.TYPE_BY_BUKKIT.get(bukkitType);
            if (type != null) {
                return type;
            }
            throw new NoSuchElementException("No EntityType for " + bukkitType);
        }

        private static class Companion {
            private static final Map<org.bukkit.entity.EntityType, EntityType> TYPE_BY_BUKKIT = new HashMap<>();
        }

    }

}

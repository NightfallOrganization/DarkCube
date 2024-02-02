/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager.monster;

public class EntityTypeManager {

    public enum EntityType {
        ZOMBIE(org.bukkit.entity.Zombie.class),
        SKELETON(org.bukkit.entity.Skeleton.class),
        PILLAGER(org.bukkit.entity.Pillager.class);

        private final Class<? extends org.bukkit.entity.Entity> entityClass;

        EntityType(Class<? extends org.bukkit.entity.Entity> entityClass) {
            this.entityClass = entityClass;
        }

        public Class<? extends org.bukkit.entity.Entity> getEntityClass() {
            return entityClass;
        }

    }

}

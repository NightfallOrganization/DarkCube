/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.entity;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.system.event.RecursiveEvent;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class EntityDamageEvent extends EntityEvent.Cancellable implements RecursiveEvent {
    private final @NotNull DamageCause cause;

    public EntityDamageEvent(@NotNull Entity entity, @NotNull DamageCause cause) {
        super(entity);
        this.cause = cause;
    }

    public @NotNull DamageCause cause() {
        return cause;
    }

    public enum DamageCause {
        /**
         * Damage caused when an entity contacts a block such as a Cactus.
         */
        CONTACT,
        /**
         * Damage caused when an entity attacks another entity.
         */
        ENTITY_ATTACK,
        /**
         * Damage caused by being put in a block
         */
        SUFFOCATION,
        /**
         * Damage caused when an entity falls a distance greater than 3 blocks
         */
        FALL,
        /**
         * Damage caused by direct exposure to fire
         */
        FIRE,
        /**
         * Damage caused due to burns caused by fire
         */
        FIRE_TICK,
        /**
         * Damage caused due to a snowman melting
         */
        MELTING,
        /**
         * Damage caused by direct exposure to lava
         */
        LAVA,
        /**
         * Damage caused by running out of air while in water
         */
        DROWNING,
        /**
         * Damage caused by being in the area when a block explodes.
         */
        BLOCK_EXPLOSION,
        /**
         * Damage caused by being in the area when an entity, such as a
         * Creeper, explodes.
         */
        ENTITY_EXPLOSION,
        /**
         * Damage caused by falling into the void
         */
        VOID,
        /**
         * Damage caused by being struck by lightning
         */
        LIGHTNING,
        /**
         * Damage caused by starving due to having an empty hunger bar
         */
        STARVATION,
        /**
         * Damage caused due to an ongoing poison effect
         */
        POISON,
        /**
         * Damage caused by being hit by a damage potion or spell
         */
        MAGIC,
        /**
         * Damage caused by Wither potion effect
         */
        WITHER,
        /**
         * Damage caused by being hit by a falling block which deals damage
         * <p>
         * <b>Note:</b> Not every block deals damage
         * <p>
         */
        FALLING_BLOCK,
        /**
         * Damage caused in retaliation to another attack by the Thorns
         * enchantment.
         */
        THORNS,
        /**
         * Custom damage.
         */
        CUSTOM,
        UNKNOWN
    }
}

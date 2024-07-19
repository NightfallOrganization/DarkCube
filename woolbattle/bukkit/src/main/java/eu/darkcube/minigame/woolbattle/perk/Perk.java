/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk;

import java.util.ArrayList;
import java.util.Collection;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler.ConfiguredScheduler;
import org.bukkit.event.Listener;

public class Perk {

    private final Collection<Listener> listeners = new ArrayList<>();
    private final Collection<ConfiguredScheduler> schedulers = new ArrayList<>();

    private final ActivationType activationType;
    private final PerkName perkName;
    private final Cooldown defaultCooldown;
    private final int defaultCost;
    private final Item defaultItem;
    private final UserPerkCreator perkCreator;
    private final boolean autoCountdownCooldown;
    private Cooldown cooldown;
    private int cost;

    public Perk(ActivationType activationType, PerkName perkName, int cooldownSeconds, int cost, Item defaultItem, UserPerkCreator perkCreator) {
        this(activationType, perkName, new Cooldown(Unit.TICKS, cooldownSeconds * 20), cost, defaultItem, perkCreator);
    }

    public Perk(ActivationType activationType, PerkName perkName, Cooldown cooldown, int cost, Item defaultItem, UserPerkCreator perkCreator) {
        this(activationType, perkName, cooldown, true, cost, defaultItem, perkCreator);
    }

    public Perk(ActivationType activationType, PerkName perkName, Cooldown cooldown, boolean autoCountdownCooldown, int cost, Item defaultItem, UserPerkCreator perkCreator) {
        this.activationType = activationType;
        this.perkName = perkName;
        this.defaultCooldown = cooldown;
        this.defaultCost = cost;
        this.cost = cost;
        this.cooldown = cooldown;
        this.autoCountdownCooldown = autoCountdownCooldown;
        this.defaultItem = defaultItem;
        this.perkCreator = perkCreator;
    }

    /**
     * Adds listeners for this perk. All listeners will be registered and unregistered when needed
     * for the perk to function (for example during ingame phase)
     *
     * @param listeners the listeners to add to this perk
     */
    protected void addListener(Listener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    /**
     * Adds schedulers for this perk. All schedulers will be registered and unregistered when
     * needed
     * for the perk to function (for example during ingame phase)
     *
     * @param schedulers the schedulers to add to this perk
     */
    protected void addScheduler(ConfiguredScheduler... schedulers) {
        this.schedulers.addAll(Arrays.asList(schedulers));
    }

    /**
     * This starts all the perk specific logic for this perk. This includes things like listeners,
     * schedulers, etc...
     *
     * @see #stopLogic()
     */
    public void startLogic() {
        WoolBattleBukkit.registerListeners(listeners.toArray(new Listener[0]));
        schedulers.forEach(ConfiguredScheduler::start);
    }

    /**
     * This stops all the perk specific logic for this perk. This includes things like listeners,
     * schedulers, etc...
     *
     * @see #startLogic()
     */
    public void stopLogic() {
        schedulers.forEach(ConfiguredScheduler::stop);
        WoolBattleBukkit.unregisterListeners(listeners.toArray(new Listener[0]));
    }

    public UserPerkCreator perkCreator() {
        return perkCreator;
    }

    /**
     * @return if the cooldown should automatically count down every tick
     */
    public boolean autoCountdownCooldown() {
        return autoCountdownCooldown;
    }

    public ActivationType activationType() {
        return activationType;
    }

    public Item defaultItem() {
        return defaultItem;
    }

    public PerkName perkName() {
        return perkName;
    }

    public int cost() {
        return cost;
    }

    public Cooldown cooldown() {
        return cooldown;
    }

    public void cooldown(Cooldown cooldown) {
        this.cooldown = cooldown;
    }

    public void cost(int cost) {
        this.cost = cost;
    }

    public Cooldown defaultCooldown() {
        return defaultCooldown;
    }

    public int defaultCost() {
        return defaultCost;
    }

    /**
     * This is the class for all perk types. May need renaming in the future, {@code
     * ActivationType}
     * is not quite accurate.<br><br>
     * <p>
     * {@link ActivationType#PRIMARY_WEAPON PRIMARY_WEAPON},
     * {@link ActivationType#SECONDARY_WEAPON SECONDARY_WEAPON} and
     * {@link ActivationType#ARROW ARROW} are perks internally, makes it easier to track with
     * existing system and helps with code reuse. This may also help for the future to make them
     * changeable
     */
    public enum ActivationType {
        // Order is important here, as the inventory will be filled in that order for new players
        PRIMARY_WEAPON("primaryWeapon", 1, Item.DEFAULT_BOW),
        SECONDARY_WEAPON("secondaryWeapon", 1, Item.DEFAULT_SHEARS),

        ACTIVE("active", 2, Item.PERKS_ACTIVE),
        MISC("misc", 1, Item.PERKS_MISC),
        PASSIVE("passive", 1, Item.PERKS_PASSIVE),

        DOUBLE_JUMP("doubleJump", 1, null),

        ARROW("arrow", 1, Item.DEFAULT_ARROW);
        private final String type;
        private final int maxCount;
        private final Item displayItem;

        ActivationType(String type, int maxCount, Item displayItem) {
            this.type = type;
            this.maxCount = maxCount;
            this.displayItem = displayItem;
        }

        public Item displayItem() {
            return displayItem;
        }

        public String type() {
            return type;
        }

        public int maxCount() {
            return maxCount;
        }

        @Override
        public String toString() {
            return type;
        }

    }

    /**
     * Functional interface for creating new instances of {@link UserPerk}s. Every {@link Perk
     * perk}
     * must have one.
     */
    public interface UserPerkCreator {
        UserPerk create(WBUser user, Perk perk, int id, int perkSlot, WoolBattleBukkit woolbattle);
    }

    public static final class Cooldown {
        private final Unit unit;
        private final int cooldown;

        public Cooldown(Unit unit, int cooldown) {
            this.unit = unit;
            this.cooldown = cooldown;
        }

        public Unit unit() {
            return unit;
        }

        public int cooldown() {
            return cooldown;
        }

        public abstract static class Unit {
            public static final Unit TICKS = new Unit() {
                @Override
                public int itemCount(int cooldown) {
                    return cooldown / 20 + 1;
                }
            };
            public static final Unit ACTIVATIONS = new Unit() {
                @Override
                public int itemCount(int cooldown) {
                    return cooldown;
                }
            };

            public abstract int itemCount(int cooldown);
        }
    }
}

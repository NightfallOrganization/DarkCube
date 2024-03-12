/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.perk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.listener.Listener;
import eu.darkcube.minigame.woolbattle.api.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;

public class Perk {
    private static final Logger logger = Logger.getLogger("Perk");

    private final Collection<PerkHook> hooks = new ArrayList<>();

    private final ActivationType activationType;
    private final PerkName perkName;
    private final Cooldown defaultCooldown;
    private final int defaultCost;
    private final Item defaultItem;
    private final UserPerkCreator perkCreator;
    private final boolean autoCountdownCooldown;
    private Cooldown cooldown;
    private int cost;

    public Perk(ActivationType activationType, PerkName perkName, int cooldownSeconds, int cost, Item item) {
        this(activationType, perkName, cooldownSeconds, cost, item, (user, perk, id, perkSlot, game) -> new DefaultUserPerk(user, id, perkSlot, perk, game));
    }

    public Perk(ActivationType activationType, PerkName perkName, int cooldownSeconds, int cost, Item defaultItem, UserPerkCreator perkCreator) {
        this(activationType, perkName, new Cooldown(Cooldown.Unit.TICKS, cooldownSeconds * 20), cost, defaultItem, perkCreator);
    }

    public Perk(ActivationType activationType, PerkName perkName, Cooldown cooldown, int cost, Item defaultItem, UserPerkCreator perkCreator) {
        this(activationType, perkName, cooldown, true, cost, defaultItem, perkCreator);
    }

    public Perk(ActivationType activationType, PerkName perkName, Cooldown defaultCooldown, boolean autoCountdownCooldown, int defaultCost, Item defaultItem, UserPerkCreator perkCreator) {
        this.activationType = activationType;
        this.perkName = perkName;
        this.defaultCooldown = defaultCooldown;
        this.defaultCost = defaultCost;
        this.defaultItem = defaultItem;
        this.perkCreator = perkCreator;
        this.autoCountdownCooldown = autoCountdownCooldown;
    }

    protected void addHooks(PerkHook... hooks) {
        this.hooks.addAll(Arrays.asList(hooks));
    }

    protected void addListeners(Listener... listeners) {
        this.addHooks(new PerkHook.ListenersHook(listeners));
    }

    /**
     * This starts all the perk specific logic for this perk. This includes things like listeners,
     * schedulers, etc...
     *
     * @see #stopLogic()
     */
    public void startLogic() {
        hooks.forEach(PerkHook::enable);
    }

    /**
     * This stops all the perk specific logic for this perk. This includes things like listeners,
     * schedulers, etc...
     *
     * @see #startLogic()
     */
    public void stopLogic() {
        hooks.forEach(PerkHook::disable);
    }

    public UserPerkCreator perkCreator() {
        return perkCreator;
    }

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

    public void cost(int cost) {
        this.cost = cost;
    }

    public Cooldown cooldown() {
        return cooldown;
    }

    public void cooldown(Cooldown cooldown) {
        this.cooldown = cooldown;
    }

    public Cooldown defaultCooldown() {
        return defaultCooldown;
    }

    public int defaultCost() {
        return defaultCost;
    }

    public interface UserPerkCreator {
        UserPerk create(WBUser user, Perk perk, int id, int perkSlot, Game game);
    }

    public record Cooldown(Unit unit, int cooldown) {
        public interface Unit {
            Unit TICKS = cooldown -> cooldown / 20 + 1;
            Unit ACTIVATIONS = cooldown -> cooldown;

            int itemCount(int cooldown);
        }
    }
}

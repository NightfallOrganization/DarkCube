/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.perk.user;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.game.ingame.Ingame;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class DefaultUserPerk implements UserPerk {
    private final Implementation implementation = InjectionLayer.ext().instance(Implementation.class);
    private final int id;
    private final Perk perk;
    private final WBUser owner;
    private final int perkSlot;
    private final Game game;
    private int slot;
    private int cooldown;

    public DefaultUserPerk(final WBUser owner, final Perk perk, final int id, final int perkSlot, Game game) {
        this(owner, id, perkSlot, perk, game);
    }

    public DefaultUserPerk(final WBUser owner, final int id, final int perkSlot, final Perk perk, Game game) {
        this.id = id;
        this.perkSlot = perkSlot;
        this.perk = perk;
        this.game = game;
        this.slot = owner.perksStorage().perkInvSlot(perk.activationType(), this.perkSlot);
        this.cooldown = 0;
        this.owner = owner;
    }

    protected Item currentItem() {
        return perk.defaultItem();
    }

    /**
     * Returns the perk slot for the perk type.<br>
     * Example: for {@link ActivationType#ACTIVE} slot 1
     *
     * @return the perk slot for the perk type
     */
    public int perkSlot() {
        return perkSlot;
    }

    @Override
    public @NotNull Game game() {
        return game;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public @NotNull Perk perk() {
        return perk;
    }

    @Override
    public @NotNull PerkItem currentPerkItem() {
        return new PerkItem(this::currentItem, this);
    }

    @Override
    public void slot(int slot) {
        if (this.slot == slot) return;
        var oldValue = this.slot;
        slotSilent(slot);
        if (game instanceof Ingame) {
            implementation.slotChanged(this, oldValue);
            // if (oldValue == 100) {
            //     owner().getBukkitEntity().getOpenInventory().setCursor(null);
            // } else if (oldValue != -1 /* -1 for no slot set */) {
            //     owner().getBukkitEntity().getOpenInventory().setItem(oldValue, null);
            // } // TODO move this into Implementation
            currentPerkItem().setItem();
        }
    }

    @Override
    public int slot() {
        return slot;
    }

    @Override
    public int cooldown() {
        return this.cooldown;
    }

    @Override
    public void cooldown(int cooldown) {
        this.cooldown = Math.min(cooldown, perk.cooldown().cooldown());
        if (game.phase() instanceof Ingame) {
            if (owner.team().canPlay()) {
                currentPerkItem().setItem();
            }
        }
    }

    @Override
    public void slotSilent(int slot) {
        this.slot = slot;
        var pp = owner().perksStorage();
        pp.perkInvSlot(perk().activationType(), perkSlot(), slot);
        owner().perksStorage(pp);
    }

    @Override
    public @NotNull WBUser owner() {
        return owner;
    }

    public interface Implementation {
        void slotChanged(DefaultUserPerk userPerk, int oldSlot);
    }
}

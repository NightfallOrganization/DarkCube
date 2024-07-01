/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.listener.perks;

import static eu.darkcube.minigame.woolbattle.api.event.user.UserInteractEvent.Action.LEFT_CLICK_AIR;
import static eu.darkcube.minigame.woolbattle.api.event.user.UserInteractEvent.Action.LEFT_CLICK_BLOCK;
import static eu.darkcube.minigame.woolbattle.api.util.PerkUtils.*;

import java.util.concurrent.atomic.AtomicReference;

import eu.darkcube.minigame.woolbattle.api.event.user.UserInteractEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerk;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;

public abstract class BasicPerkListener extends PerkListener {
    protected final Key perkKey;

    public BasicPerkListener(Game game, Perk perk) {
        super(game, perk);
        this.perkKey = Key.key(game.woolbattle(), "perk");
        this.listeners.addListener(UserInteractEvent.class, event -> {
            if (!mayActivate()) return;
            var item = event.item();
            if (item == null) return;
            var user = event.user();

            var refUserPerk = new AtomicReference<UserPerk>();
            if (!checkUsable(user, item, perk(), userPerk -> {
                refUserPerk.set(userPerk);
                event.cancel();
            }, game)) {
                playSoundNotEnoughWool(user);
                return;
            }
            var userPerk = refUserPerk.get();

            var left = event.action() == LEFT_CLICK_AIR || event.action() == LEFT_CLICK_BLOCK;
            if (!activate(userPerk)) {
                if (!(left ? activateLeft(userPerk) : activateRight(userPerk))) {
                    return;
                }
            }
            activated(userPerk);
        });
    }

    /**
     * Called when the perk is activated
     *
     * @param perk the perk
     * @return if the perk was activated and cooldown should start
     */
    protected boolean activate(UserPerk perk) {
        return false;
    }

    /**
     * Called when the perk is activated with a right click
     *
     * @param perk the perk
     * @return if the perk was activated and cooldown should start
     */
    protected boolean activateRight(UserPerk perk) {
        return false;
    }

    /**
     * Called when the perk is activated with a left click
     *
     * @param perk the perk
     * @return if the perk was activated and cooldown should start
     */
    protected boolean activateLeft(UserPerk perk) {
        return false;
    }

    /**
     * Called when any of the activate methods return true. Default implementation is paying wool
     * and starting cooldown
     *
     * @param perk the perk
     */
    protected void activated(UserPerk perk) {
        payForPerk(perk);
        perk.cooldown(perk.perk().cooldown().cooldown());
    }

    protected boolean mayActivate() {
        return true;
    }
}

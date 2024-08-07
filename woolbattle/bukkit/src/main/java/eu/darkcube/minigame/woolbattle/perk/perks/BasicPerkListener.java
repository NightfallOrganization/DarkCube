/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks;

import java.util.concurrent.atomic.AtomicReference;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.LaunchableInteractEvent;
import eu.darkcube.minigame.woolbattle.listener.RegisterNotifyListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public abstract class BasicPerkListener extends PerkListener implements RegisterNotifyListener {

    protected final WoolBattleBukkit woolbattle;
    private final Handle handle;

    public BasicPerkListener(Perk perk, WoolBattleBukkit woolbattle) {
        super(perk);
        this.woolbattle = woolbattle;
        this.handle = new Handle(woolbattle);
    }

    @Override
    public void registered() {
        WoolBattleBukkit.registerListeners(this.handle);
    }

    @Override
    public void unregistered() {
        WoolBattleBukkit.unregisterListeners(this.handle);
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
        payForThePerk(perk);
        perk.cooldown(perk.perk().cooldown().cooldown());
    }

    protected boolean mayActivate() {
        return true;
    }

    private class Handle implements Listener {

        private final WoolBattleBukkit woolbattle;

        private Handle(WoolBattleBukkit woolbattle) {
            this.woolbattle = woolbattle;
        }

        @EventHandler
        private void handle(LaunchableInteractEvent event) {
            if (!mayActivate()) return;
            ItemStack item = event.getItem();
            if (item == null) {
                return;
            }
            WBUser user = WBUser.getUser(event.getPlayer());
            AtomicReference<UserPerk> refUserPerk = new AtomicReference<>();
            if (!checkUsable(user, item, perk(), userPerk -> {
                refUserPerk.set(userPerk);
                if (event.getEntity() != null) {
                    event.getEntity().remove();
                    userPerk.currentPerkItem().setItem();
                    new Scheduler(woolbattle, userPerk.currentPerkItem()::setItem).runTask();
                }
                event.setCancelled(true);
            }, woolbattle)) {
                return;
            }
            UserPerk userPerk = refUserPerk.get();
            boolean left = event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK;
            if (!activate(userPerk) && !(left ? activateLeft(userPerk) : activateRight(userPerk))) {
                return;
            }
            activated(userPerk);
        }
    }
}

/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.perk.passive.EventMayDoubleJump;
import eu.darkcube.minigame.woolbattle.event.user.EventUserAttackUser;
import eu.darkcube.minigame.woolbattle.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler.ConfiguredScheduler;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class SpiderPerk extends Perk {
    public static final PerkName SPIDER = new PerkName("SPIDER");

    private final Key CLIMBING;

    public SpiderPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.PASSIVE, SPIDER, new Cooldown(Unit.TICKS, 60), 2, Item.PERK_SPIDER, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_SPIDER_COOLDOWN, wb));
        CLIMBING = Key.key(woolbattle, "perk_spider_climbing");
        SpiderSchedulerListener schedulerListener = new SpiderSchedulerListener(woolbattle);
        addScheduler(schedulerListener);
        addListener(schedulerListener);
    }

    public boolean isClimbing(WBUser user) {
        return user.user().getMetaDataStorage().has(CLIMBING);
    }

    public static class SpiderStateChangeEvent extends UserEvent implements Cancellable {
        private static final HandlerList handlers = new HandlerList();
        private boolean climbing;
        private boolean cancel;

        public SpiderStateChangeEvent(WBUser user, boolean climbing) {
            super(user);
            this.climbing = climbing;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }

        @Api
        public boolean climbing() {
            return climbing;
        }

        @Api
        public void climbing(boolean climbing) {
            this.climbing = climbing;
        }

        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        @Override
        public boolean isCancelled() {
            return cancel;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancel = cancel;
        }
    }

    private class SpiderSchedulerListener extends Scheduler implements ConfiguredScheduler, Listener {

        private SpiderSchedulerListener(WoolBattleBukkit woolbattle) {
            super(woolbattle);
        }

        @Override
        public void run() {
            float woolPerTick = cost() / 20F;
            WBUser.onlineUsers().stream().filter(u -> u.getTeam().canPlay()).forEach(user -> user.perks().perks(perkName()).forEach(perk -> {
                if (user.getTicksAfterLastHit() < cooldown().cooldown()) {
                    perk.cooldown(cooldown().cooldown() - user.getTicksAfterLastHit());
                    return;
                }
                if (perk.cooldown() > 0) perk.cooldown(0);
                Location loc = user.getBukkitEntity().getLocation();
                Vector dir = loc.getDirection();
                dir.setY(0);
                float wool = user.woolCount();
                if ((dir.getX() == 0 && dir.getZ() == 0) || user.getBukkitEntity().isOnGround() || user.getBukkitEntity().isSneaking() || wool < woolPerTick) {
                    if (isClimbing(user)) {
                        interrupt(user);
                    }
                    return;
                }
                dir.normalize();
                loc.add(dir);
                if (loc.getBlock().getType().isSolid() || loc.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
                    if (!isClimbing(user)) {
                        SpiderStateChangeEvent event = new SpiderStateChangeEvent(user, true);
                        Bukkit.getPluginManager().callEvent(event);
                        boolean climbing = !event.isCancelled() && event.climbing();
                        if (climbing) {
                            user.user().getMetaDataStorage().set(CLIMBING, 1F);
                        }
                    }
                    if (isClimbing(user)) {
                        float cur = user.user().getMetaDataStorage().<Float>get(CLIMBING);
                        cur = cur + woolPerTick;
                        user.removeWool((int) cur);
                        user.user().getMetaDataStorage().set(CLIMBING, cur % 1);

                        user.getBukkitEntity().setAllowFlight(true);
                        user.getBukkitEntity().setFlying(true);
                        user.getBukkitEntity().setFlySpeed(0.05F);
                    }
                } else if (isClimbing(user)) {
                    interrupt(user);
                }
            }));
        }

        @EventHandler
        public void handle(EventMayDoubleJump event) {
            if (isClimbing(event.user())) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void handle(EventUserAttackUser event) {
            if (isClimbing(event.target())) {
                interrupt(event.target());
            }
        }

        private void interrupt(WBUser user) {
            if (isClimbing(user)) {
                SpiderStateChangeEvent event = new SpiderStateChangeEvent(user, false);
                Bukkit.getPluginManager().callEvent(event);
                boolean climbing = event.climbing() || event.isCancelled();
                if (!climbing) {
                    user.user().getMetaDataStorage().remove(CLIMBING);
                    user.getBukkitEntity().setFlying(false);
                }
            }
        }

        @Override
        public void start() {
            runTaskTimer(1);
        }

        @Override
        public void stop() {
            cancel();
        }
    }
}

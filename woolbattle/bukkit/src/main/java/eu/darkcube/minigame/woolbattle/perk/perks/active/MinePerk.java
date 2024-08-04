/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.active;

import java.util.concurrent.atomic.AtomicReference;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MinePerk extends Perk {
    public static final PerkName MINE = new PerkName("MINE");

    public MinePerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, MINE, 6, 7, Item.PERK_MINE, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_MINE_COOLDOWN, woolbattle));
        addListener(new ListenerMine(this, woolbattle));
    }

    public static class ListenerMine extends BasicPerkListener {
        private final WoolBattleBukkit woolbattle;

        public ListenerMine(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
            this.woolbattle = woolbattle;
        }

        @EventHandler
        public void handle(BlockPlaceEvent event) {
            if (event.getItemInHand() == null) return;
            var item = event.getItemInHand();
            var user = WBUser.getUser(event.getPlayer());
            var perkRef = new AtomicReference<UserPerk>(null);
            if (!checkUsable(user, item, perk(), perkRef::set, woolbattle)) {
                if (perkRef.get() != null) event.setCancelled(true);
                return;
            }
            var userPerk = perkRef.get();
            userPerk.currentPerkItem().setItem();
            new Scheduler(woolbattle, userPerk.currentPerkItem()::setItem).runTask();
            event.setCancelled(true);
            activated(userPerk);
            new Scheduler(woolbattle, () -> woolbattle.ingame().place(event.getBlock(), b -> {
                b.setType(Material.STONE_PLATE);
                woolbattle.ingame().setMetaData(b, "perk", userPerk);
            })).runTask();
        }

        @EventHandler
        public void handle(PlayerInteractEvent event) {
            var p = event.getPlayer();
            var user = WBUser.getUser(p);
            if (!user.getTeam().canPlay()) return;
            if (event.getAction() != Action.PHYSICAL) return;
            var block = event.getClickedBlock();
            if (block == null) return;
            UserPerk perk = woolbattle.ingame().getMetaData(block, "perk", null);
            if (perk == null) return;
            if (!perk.perk().perkName().equals(MinePerk.MINE)) return;

            event.setCancelled(true);
            woolbattle.ingame().destroy(block);
            block.getWorld().createExplosion(block.getLocation().add(0.5, 0.5, 0.5), 5);

            new Scheduler(woolbattle, () -> {
                var playerLoc = p.getLocation().toVector();
                var blockLoc = block.getLocation().add(0.5, 0, 0.5).toVector();

                var velocity = playerLoc.subtract(blockLoc).normalize();
                velocity.multiply(Math.pow(playerLoc.distance(blockLoc), 0.4) / 6);
                if (woolbattle.ingame().playerUtil().attack(perk.owner(), user)) {
                    p.damage(0);
                }
                velocity.setY(1.3);
                p.setVelocity(velocity);
            }).runTaskLater(2);
        }

        @EventHandler
        public void handle(BlockPhysicsEvent event) {
            var block = event.getBlock();
            UserPerk perk = woolbattle.ingame().getMetaData(block, "perk", null);
            if (perk == null) return;
            if (!perk.perk().perkName().equals(MinePerk.MINE)) return;

            event.setCancelled(true);
            woolbattle.ingame().destroy(block);
            block.getWorld().createExplosion(block.getLocation().add(0.5, 0.5, 0.5), 3);

            var blockLoc = block.getLocation().add(0.5, 0, 0.5);
            for (var p : Bukkit.getOnlinePlayers()) {
                if (p.getLocation().distanceSquared(blockLoc) > 9) {
                    continue;
                }
                var user = WBUser.getUser(p);

                var playerLoc = p.getLocation().toVector();
                var blockVec = block.getLocation().add(0.5, 0, 0.5).toVector();

                var velocity = playerLoc.subtract(blockVec).normalize();
                velocity.multiply(Math.pow(playerLoc.distance(blockVec), 0.4) / 6);
                if (woolbattle.ingame().playerUtil().attack(perk.owner(), user)) {
                    p.damage(0);
                }
                velocity.setY(1.3);
                p.setVelocity(velocity);
            }
        }

        @Override
        protected boolean mayActivate() {
            return false;
        }
    }

}

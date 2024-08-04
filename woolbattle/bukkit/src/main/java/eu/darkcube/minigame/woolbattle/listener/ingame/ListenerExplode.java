/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.WoolBombPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFallingSand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

public class ListenerExplode extends Listener<EntityExplodeEvent> {
    private final WoolBattleBukkit woolbattle;

    public ListenerExplode(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    @EventHandler
    public void handle(EntityExplodeEvent e) {
        var mid = e.getEntity().getLocation();
        var x = mid.getX();
        var y = mid.getY();
        var z = mid.getZ();
        for (var b : e.blockList()) {
            if (woolbattle.ingame().destroy(b)) {
                @SuppressWarnings("deprecation") var block = b.getWorld().spawnFallingBlock(b.getLocation().add(0.5, 0.5, 0.5), b.getType(), b.getData());
                var vx = block.getLocation().getX() - x;
                var vy = block.getLocation().getY() - y;
                var vz = block.getLocation().getZ() - z;
                block.setVelocity(new Vector(vx, vy, vz).multiply(.2));
                var craft = (CraftFallingSand) block;
                craft.getHandle().setSize(0.1F, 0.1F);
            }
        }
        e.blockList().clear();
    }

    @EventHandler
    public void handle(BlockExplodeEvent event) {
        var mid = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        var x = mid.getX();
        var y = mid.getY();
        var z = mid.getZ();
        for (var b : event.blockList()) {
            if (woolbattle.ingame().destroy(b)) {
                @SuppressWarnings("deprecation") var block = b.getWorld().spawnFallingBlock(b.getLocation().add(0.5, 0.5, 0.5), b.getType(), b.getData());
                var vx = block.getLocation().getX() - x;
                var vy = block.getLocation().getY() - y;
                var vz = block.getLocation().getZ() - z;
                block.setVelocity(new Vector(vx, vy, vz).multiply(.2));
                var craft = (CraftFallingSand) block;
                craft.getHandle().setSize(0.1F, 0.1F);
            }
        }
        event.blockList().clear();
    }

    @EventHandler
    public void handle(EntityDamageByBlockEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        if (event.getCause() != DamageCause.BLOCK_EXPLOSION) {
            return;
        }
        event.setCancelled(true);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void handle(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        var p = (Player) event.getEntity();
        var user = WBUser.getUser(p);
        if (!user.getTeam().canPlay()) {
            event.setCancelled(true);
            return;
        }
        if (event.getDamager().getType() == EntityType.PRIMED_TNT) {
            var tnt = (TNTPrimed) event.getDamager();
            if (tnt.hasMetadata("source")) {
                var source = (WBUser) tnt.getMetadata("source").getFirst().value();
                if (tnt.getLocation().distance(p.getLocation()) > tnt.getYield()) {
                    event.setCancelled(true);
                    return;
                }
                Player a = source.getBukkitEntity();
                var loc = p.getLocation().add(0, 0.5, 0);
                var attacker = WBUser.getUser(a);
                event.setCancelled(true);
                var x = loc.getX() - tnt.getLocation().getX();
                var y = loc.getY() - tnt.getLocation().getY();
                y = Math.max(y, 0.7);
                var z = loc.getZ() - tnt.getLocation().getZ();
                var direction = new Vector(x, y, z).normalize();
                double strength = 0;
                strength += tnt.getMetadata("boost").getFirst().asDouble();

                var t = (tnt.getYield() - tnt.getLocation().distance(loc)) / (tnt.getYield() * 2) + 0.5;
                strength *= t;
                strength *= 1.2;
                if (!p.isOnGround()) {
                    strength *= 1.2;
                }

                var strengthX = strength;
                var strengthY = strength;
                var strengthZ = strength;

                if (a.equals(p)) {
                    if (p.getLocation().distance(tnt.getLocation()) < 1.3) {
                        strengthX *= 0.2;
                        strengthZ *= 0.2;
                    }
                }

                var velocity = direction.clone();
                velocity.setX(velocity.getX() * strengthX);
                velocity.setY(1 + (velocity.getY() * strengthY / 5));
                velocity.setZ(velocity.getZ() * strengthZ);
                p.setVelocity(velocity);
                if (!tnt.hasMetadata("peaceful")) {
                    woolbattle.ingame().playerUtil().attack(attacker, user);
                }
            }
        } else if (event.getDamager().getType() == EntityType.SNOWBALL) {
            var bomb = (Snowball) event.getDamager();
            if (!bomb.getMetadata("perk").isEmpty() && bomb.getMetadata("perk").getFirst().asString().equals(WoolBombPerk.WOOL_BOMB.getName())) {
                event.setCancelled(true);
            }
        }
    }
}

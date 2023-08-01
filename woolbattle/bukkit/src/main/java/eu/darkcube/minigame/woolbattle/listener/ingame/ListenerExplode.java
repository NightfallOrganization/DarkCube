/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.WoolBombPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFallingSand;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

public class ListenerExplode extends Listener<EntityExplodeEvent> {

    @Override
    @EventHandler
    public void handle(EntityExplodeEvent e) {
        Location mid = e.getEntity().getLocation();
        double x = mid.getX();
        double y = mid.getY();
        double z = mid.getZ();
        for (Block b : e.blockList()) {
            if (WoolBattle.instance().ingame().destroy(b)) {
                @SuppressWarnings("deprecation")
                FallingBlock block = b.getWorld()
                        .spawnFallingBlock(b.getLocation().add(0.5, 0.5, 0.5), b.getType(),
                                b.getData());
                double vx = block.getLocation().getX() - x;
                double vy = block.getLocation().getY() - y;
                double vz = block.getLocation().getZ() - z;
                block.setVelocity(new Vector(vx, vy, vz).multiply(.2));
                CraftFallingSand craft = (CraftFallingSand) block;
                craft.getHandle().setSize(0.1F, 0.1F);
            }
        }
        e.blockList().clear();
    }

    @EventHandler
    public void handle(BlockExplodeEvent event) {
        Location mid = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        double x = mid.getX();
        double y = mid.getY();
        double z = mid.getZ();
        for (Block b : event.blockList()) {
            if (WoolBattle.instance().ingame().destroy(b)) {
                @SuppressWarnings("deprecation")
                FallingBlock block = b.getWorld()
                        .spawnFallingBlock(b.getLocation().add(0.5, 0.5, 0.5), b.getType(),
                                b.getData());
                double vx = block.getLocation().getX() - x;
                double vy = block.getLocation().getY() - y;
                double vz = block.getLocation().getZ() - z;
                block.setVelocity(new Vector(vx, vy, vz).multiply(.2));
                CraftFallingSand craft = (CraftFallingSand) block;
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
        Player p = (Player) event.getEntity();
        WBUser user = WBUser.getUser(p);
        if (!user.getTeam().canPlay()) {
            event.setCancelled(true);
            return;
        }
        if (event.getDamager().getType() == EntityType.PRIMED_TNT) {
            TNTPrimed tnt = (TNTPrimed) event.getDamager();
            if (tnt.hasMetadata("source")) {
                WBUser source = (WBUser) tnt.getMetadata("source").get(0).value();
                if (tnt.getLocation().distance(p.getLocation()) > tnt.getYield()) {
                    event.setCancelled(true);
                    return;
                }
                Player a = source.getBukkitEntity();
                Location loc = p.getLocation().add(0, 0.5, 0);
                WBUser attacker = WBUser.getUser(a);
                event.setCancelled(true);
                double x = loc.getX() - tnt.getLocation().getX();
                double y = loc.getY() - tnt.getLocation().getY();
                y = Math.max(y, 0.7);
                double z = loc.getZ() - tnt.getLocation().getZ();
                Vector direction = new Vector(x, y, z).normalize();
                double strength = 0;
                strength += tnt.getMetadata("boost").get(0).asDouble();

                double t = (tnt.getYield() - tnt.getLocation().distance(loc)) / (tnt.getYield() * 2)
                        + 0.5;
                strength *= t;
                strength *= 1.2;
                if (!p.isOnGround()) {
                    strength *= 1.2;
                }

                double strengthX = strength;
                double strengthY = strength;
                double strengthZ = strength;

                if (a.equals(p)) {
                    if (p.getLocation().distance(tnt.getLocation()) < 1.3) {
                        strengthX *= 0.2;
                        strengthZ *= 0.2;
                    }
                }

                Vector velocity = direction.clone();
                velocity.setX(velocity.getX() * strengthX);
                velocity.setY(1 + (velocity.getY() * strengthY / 5));
                velocity.setZ(velocity.getZ() * strengthZ);
                p.setVelocity(velocity);
                WoolBattle.instance().ingame().attack(attacker, user);
            }
        } else if (event.getDamager().getType() == EntityType.SNOWBALL) {
            Snowball bomb = (Snowball) event.getDamager();
            if (bomb.getMetadata("perk").size() != 0 && bomb.getMetadata("perk").get(0).asString()
                    .equals(WoolBombPerk.WOOL_BOMB.getName())) {
                event.setCancelled(true);
            }
        }
    }
}

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import java.util.Collection;
import java.util.HashSet;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect.OrdinaryColor;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler.ConfiguredScheduler;
import eu.darkcube.system.util.data.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class StomperPerk extends Perk {
    public static final PerkName STOMPER = new PerkName("STOMPER");

    private final Key wasOnGround;
    private final Key active;
    private final Key startPos;

    public StomperPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.PASSIVE, STOMPER, new Cooldown(Unit.ACTIVATIONS, 0), 10, Item.PERK_STOMPER, DefaultUserPerk::new);
        wasOnGround = new Key(woolbattle, "perk_stomper_was_on_ground");
        active = new Key(woolbattle, "perk_stomper_active");
        startPos = new Key(woolbattle, "perk_stomper_start_pos");
        addListener(new StomperListener());
        addScheduler(new StomperScheduler(woolbattle));
    }

    private static class ShockwaveScheduler extends Scheduler {
        private static final double speedBlocksPerTick = 1;
        private static final double particlesPerBlock = 2;
        private final WBUser user;
        private final double maxRadius;
        private final Location center;
        private final Collection<WBUser> hit = new HashSet<>();
        private final WoolBattleBukkit woolbattle;
        private double radius = 0;

        public ShockwaveScheduler(WBUser user, Location center, double maxRadius, WoolBattleBukkit woolbattle) {
            super(woolbattle);
            this.user = user;
            this.center = center;
            this.maxRadius = maxRadius;
            this.woolbattle = woolbattle;
        }

        @Override
        public void run() {
            radius += speedBlocksPerTick;
            if (radius > maxRadius) {
                cancel();
            }
            double circumference = Math.PI * 2 * radius;

            int particleCount = (int) Math.ceil(particlesPerBlock * circumference);

            double theta = 0;

            for (int i = 0; i < particleCount; i++, theta += (Math.PI * 2 / particleCount)) {
                double x = radius * Math.cos(theta);
                double z = radius * Math.sin(theta);
                double y = Math.sin(theta + radius) * 0.3 + 0.3;
                center.add(x, y, z);
                ParticleEffect.REDSTONE.display(new OrdinaryColor(230, 230, 230), center, 50);
                // ParticleEffect.REDSTONE.display(0, 0, 0, 1, 1, center, 50);
                center.subtract(x, y, z);
            }
            for (WBUser target : WBUser.onlineUsers()) {
                if (!target.getTeam().canPlay()) continue;
                if (hit.contains(target)) continue;
                if (!woolbattle.ingame().playerUtil().canAttack(user, target)) continue;
                if (target.getBukkitEntity().getLocation().distance(center) > radius) continue;
                if (target.getBukkitEntity().getLocation().distance(center) < radius - 2) continue;
                if (Math.abs(target.getBukkitEntity().getLocation().getY() - center.getY()) > 2.5) continue;
                if (!woolbattle.ingame().playerUtil().attack(user, target)) continue;
                target.getBukkitEntity().damage(0);
                hit.add(target);
                Vector v = target.getBukkitEntity().getLocation().toVector().subtract(center.toVector());
                if (v.length() == 0) continue;
                v.normalize();
                v.setY(v.getY() + 0.2);
                if (v.length() != 0) {
                    v.normalize();
                    v.multiply(Math.pow(maxRadius - radius + 0.1, 0.3));
                    if (target.getBukkitEntity().isOnGround()) v.multiply(1.5);
                    target.getBukkitEntity().setVelocity(v);
                }
            }
        }
    }

    public class StomperScheduler extends Scheduler implements ConfiguredScheduler {
        private final WoolBattleBukkit woolbattle;

        public StomperScheduler(WoolBattleBukkit woolbattle) {
            super(woolbattle);
            this.woolbattle = woolbattle;
        }

        @Override
        public void run() {
            for (WBUser user : WBUser.onlineUsers()) {
                if (user.perks().count(STOMPER) == 0) continue;
                if (user.getBukkitEntity().isOnGround()) user.user().metadata().set(wasOnGround, true);
                if (!user.getTeam().canPlay()) continue;
                if (!user.user().metadata().has(active)) continue;
                if (!user.getBukkitEntity().isOnGround() && user.getBukkitEntity().getLocation().subtract(0, 0.5, 0).getBlock().getType() == Material.AIR) continue;
                int removed;
                user.user().metadata().remove(active);
                double starty = user.user().metadata().remove(startPos);
                double diff = starty - user.getBukkitEntity().getLocation().getY();
                if (diff < 2) continue;
                if ((removed = user.removeWool(cost())) != cost()) {
                    user.addWool(removed);
                    continue;
                }
                double rad = Math.pow(diff, 0.6);
                new ShockwaveScheduler(user, user.getBukkitEntity().getLocation(), rad, woolbattle).runTaskTimer(1);
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

    public class StomperListener implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        public void handle(PlayerMoveEvent event) {
            WBUser user = WBUser.getUser(event.getPlayer());
            if (!user.getTeam().canPlay()) return;
            if (user.perks().count(perkName()) == 0) return;
            if (!user.user().metadata().has(active)) {
                boolean og = user.user().metadata().has(wasOnGround) && user.user().metadata().<Boolean>remove(wasOnGround);
                if (!og && event.getFrom().subtract(0, 0.95, 0).getBlock().getType() == Material.AIR) return;
            }
            if (event.getFrom().getY() - event.getTo().getY() < 0) {
                user.user().metadata().set(active, true);
                user.user().metadata().set(startPos, event.getTo().getY());
            }
        }
    }
}

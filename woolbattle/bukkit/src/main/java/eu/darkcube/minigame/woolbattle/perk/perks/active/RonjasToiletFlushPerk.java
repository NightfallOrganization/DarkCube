/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class RonjasToiletFlushPerk extends Perk {
    public static final PerkName RONJAS_TOILET_FLUSH = new PerkName("RONJAS_TOILET_FLUSH");

    public RonjasToiletFlushPerk() {
        super(ActivationType.ACTIVE, RONJAS_TOILET_FLUSH, 13, 12, Item.PERK_RONJAS_TOILET_SPLASH,
                (user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
                        Item.PERK_RONJAS_TOILET_SPLASH_COOLDOWN));
        addListener(new ListenerRonjasToiletFlush(this));
    }

    public static class ListenerRonjasToiletFlush extends BasicPerkListener {

        private static final double RANGE = 4;

        public ListenerRonjasToiletFlush(Perk perk) {
            super(perk);
        }

        @Override
        protected boolean activateRight(UserPerk perk) {
            Egg egg = perk.owner().getBukkitEntity().launchProjectile(Egg.class);
            egg.setMetadata("source", new FixedMetadataValue(WoolBattleBukkit.instance(), perk.owner()));
            egg.setMetadata("perk", new FixedMetadataValue(WoolBattleBukkit.instance(),
                    perk.perk().perkName().getName()));
            return true;
        }

        @EventHandler
        public void handle(ProjectileHitEvent e) {
            if (e.getEntityType() == EntityType.EGG) {
                Egg egg = (Egg) e.getEntity();
                if (!isEggPerk(egg)) {
                    return;
                }
                ParticleEffect.DRIP_WATER.display(.3F, 1F, .3F, 1, 250, egg.getLocation(), 50);

                if (egg.getTicksLived() <= 3) {
                    WBUser.onlineUsers().stream()
                            .filter(u -> !u.getTeam().isSpectator())
                            .map(WBUser::getBukkitEntity).filter(bukkitEntity ->
                                    bukkitEntity.getLocation().distance(egg.getLocation()) < RANGE)
                            .forEach(t -> {
                                Vector v = egg.getVelocity().multiply(1.3);
                                v.setY(egg.getVelocity().getY()).normalize().multiply(3)
                                        .setY(v.getY() + 1.2);
                                t.setVelocity(v);
                            });
                } else {
                    WBUser.onlineUsers().stream()
                            .filter(u -> !u.getTeam().isSpectator())
                            .map(WBUser::getBukkitEntity)
                            .filter(bukkitEntity -> bukkitEntity.getWorld().equals(egg.getWorld()))
                            .filter(bukkitEntity ->
                                    bukkitEntity.getLocation().distance(egg.getLocation())
                                            < RANGE + 1).forEach(t -> {
                                double x = t.getLocation().getX() - egg.getLocation().getX();
                                double y = t.getLocation().getY() - egg.getLocation().getY();
                                double z = t.getLocation().getZ() - egg.getLocation().getZ();
                                t.setVelocity(new Vector(x, Math.max(1, y), z).normalize().multiply(2));
                            });
                }
            }
        }

        @EventHandler
        public void handle(EntityDamageByEntityEvent e) {
            if (e.getEntityType() != EntityType.PLAYER
                    || e.getDamager().getType() != EntityType.EGG)
                return;
            Player t = (Player) e.getEntity();
            Egg egg = (Egg) e.getDamager();
            if (!isEggPerk(egg)) {
                return;
            }
            WBUser user = (WBUser) egg.getMetadata("source").get(0).value();
            WBUser target = WBUser.getUser(t);
            if (target.projectileImmunityTicks() > 0) {
                e.setCancelled(true);
                return;
            }
            WoolBattleBukkit.instance().ingame().attack(user, target);
        }

        private boolean isEggPerk(Egg egg) {
            if (!egg.hasMetadata("source")) {
                return false;
            }
            if (!egg.hasMetadata("perk")) {
                return false;
            }
            return egg.getMetadata("perk").get(0).asString()
                    .equals(RonjasToiletFlushPerk.RONJAS_TOILET_FLUSH.getName());
        }
    }
}

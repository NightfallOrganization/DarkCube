/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.other;

import java.util.List;
import java.util.Random;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.perk.other.BowArrowHitPlayerEvent;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ArrowPerk extends Perk {
    public static final PerkName ARROW = new PerkName("ARROW");

    private final WoolBattleBukkit woolbattle;

    public ArrowPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ARROW, ARROW, 0, 0, Item.DEFAULT_ARROW, (user, perk, id, perkSlot, wb) -> new DefaultUserPerk(user, id, perkSlot, perk, wb));
        this.woolbattle = woolbattle;
        addListener(new ArrowPerkListener());
    }

    public static void claimArrow(WoolBattleBukkit woolbattle, Arrow arrow, WBUser user, float strength, int blockDamage) {
        arrow.setMetadata("user", new FixedMetadataValue(woolbattle, user));
        arrow.setMetadata("perk", new FixedMetadataValue(woolbattle, ArrowPerk.ARROW));
        arrow.setMetadata("strength", new FixedMetadataValue(woolbattle, strength));
        arrow.setMetadata("blockDamage", new FixedMetadataValue(woolbattle, blockDamage));
    }

    private class ArrowPerkListener implements Listener {
        @EventHandler
        public void handle(ProjectileHitEvent e) {
            if (e.getEntityType() == EntityType.ARROW) {
                Arrow arrow = (Arrow) e.getEntity();
                if (!arrow.hasMetadata("perk")) return;
                if (!arrow.getMetadata("perk").getFirst().value().equals(perkName())) return;

                // Copied from EntityArrow#t_ to calculate block the arrow is stuck in
                EntityArrow earrow = ((CraftArrow) e.getEntity()).getHandle();
                Vec3D vec3d = new Vec3D(earrow.locX, earrow.locY, earrow.locZ);
                Vec3D vec3d1 = new Vec3D(earrow.locX + earrow.motX, earrow.locY + earrow.motY, earrow.locZ + earrow.motZ);
                MovingObjectPosition movingobjectposition = earrow.world.rayTrace(vec3d, vec3d1, false, true, false);
                vec3d = new Vec3D(earrow.locX, earrow.locY, earrow.locZ);
                vec3d1 = new Vec3D(earrow.locX + earrow.motX, earrow.locY + earrow.motY, earrow.locZ + earrow.motZ);
                if (movingobjectposition != null) {
                    vec3d1 = new Vec3D(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
                }

                Entity entity = null;
                List<Entity> list = earrow.world.getEntities(earrow, earrow.getBoundingBox().a(earrow.motX, earrow.motY, earrow.motZ).grow(1.0, 1.0, 1.0));
                double d0 = 0.0;

                int j;
                float f1;
                for (j = 0; j < list.size(); ++j) {
                    Entity entity1 = list.get(j);
                    if (entity1.ad() && (entity1 != earrow.shooter || earrow.ticksLived >= 5)) {
                        f1 = 0.3F;
                        AxisAlignedBB axisalignedbb1 = entity1.getBoundingBox().grow(f1, f1, f1);
                        MovingObjectPosition movingobjectposition1 = axisalignedbb1.a(vec3d, vec3d1);
                        if (movingobjectposition1 != null) {
                            double d1 = vec3d.distanceSquared(movingobjectposition1.pos);
                            if (d1 < d0 || d0 == 0.0) {
                                entity = entity1;
                                d0 = d1;
                            }
                        }
                    }
                }

                if (entity != null) {
                    movingobjectposition = new MovingObjectPosition(entity);
                }
                if (movingobjectposition != null && movingobjectposition.entity == null) {
                    int x = movingobjectposition.a().getX();
                    int y = movingobjectposition.a().getY();
                    int z = movingobjectposition.a().getZ();
                    Block block = e.getEntity().getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.WOOL) {
                        woolbattle.ingame().setBlockDamage(block, woolbattle.ingame().getBlockDamage(block) + arrow.getMetadata("blockDamage").getFirst().asInt());
                    }
                }
                earrow.die();
            }
        }

        @EventHandler
        public void handle(EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof Player userPlayer)) return;
            if (!(event.getDamager() instanceof Arrow arrow)) return;
            WBUser user = WBUser.getUser(userPlayer);
            if (!arrow.hasMetadata("perk")) return;
            if (!arrow.getMetadata("perk").getFirst().value().equals(perkName())) return;
            WBUser shooter = (WBUser) arrow.getMetadata("user").getFirst().value();
            float strength = arrow.getMetadata("strength").getFirst().asFloat();
            event.setCancelled(true);
            if (user.getTicksAfterLastHit() < 10) return;
            if (user.projectileImmunityTicks() > 0) {
                arrow.remove();
                return;
            }
            if (!woolbattle.ingame().playerUtil().canAttack(shooter, user)) {
                arrow.remove();
                return;
            }
            BowArrowHitPlayerEvent hitEvent = new BowArrowHitPlayerEvent(arrow, shooter, user);
            Bukkit.getPluginManager().callEvent(hitEvent);
            if (hitEvent.isCancelled()) {
                arrow.remove();
                return;
            }
            if (!woolbattle.ingame().playerUtil().attack(shooter, user)) {
                arrow.remove();
                logger.warning("Inconsistent behaviour for ArrowPerk. This might mess up some logic for " + "other perks");
                return;
            }
            userPlayer.damage(0);
            var shooterPlayer = shooter.getBukkitEntity();
            if (shooterPlayer != null) {
                shooterPlayer.playSound(shooter.getBukkitEntity().getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
            }
            userPlayer.getWorld().playSound(arrow.getLocation(), Sound.ARROW_HIT, 1, 1);
            userPlayer.setVelocity(arrow.getVelocity().setY(0).normalize().multiply(.47 + new Random().nextDouble() / 70 + strength / 1.42).setY(.400023));

            new Scheduler(woolbattle) {
                @Override
                public void run() {
                    Location loc = userPlayer.getLocation();
                    execute(woolbattle.ingame(), loc);
                }

                private void execute(Ingame ingame, Location loc) {
                    double width = 0.7;
                    for (double xoff = -width; xoff < width + 1; xoff++) {
                        for (double yoff = -1; yoff < 3; yoff++) {
                            for (double zoff = -width; zoff < width + 1; zoff++) {
                                Location l = loc.clone().add(xoff, yoff, zoff);
                                Block b = l.getBlock();
                                if (b.getType() == Material.WOOL) {
                                    int dmg = ingame.getBlockDamage(b);
                                    ingame.setBlockDamage(b, dmg + 1);
                                }
                            }
                        }
                    }
                }
            }.runTaskLater(3);

        }
    }
}

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
import eu.darkcube.minigame.woolbattle.perk.perks.other.ArrowPerk;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Random;

public class ArrowBombPerk extends Perk {
    public static final PerkName ARROW_BOMB = new PerkName("ARROW_BOMB");

    public ArrowBombPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, ARROW_BOMB, 9, 7, Item.PERK_ARROW_BOMB, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_ARROW_BOMB_COOLDOWN, woolbattle));
        addListener(new ArrowBombListener(this, woolbattle));
    }

    private static class ArrowBombListener extends BasicPerkListener {
        private final Random r = new Random();
        private final WoolBattleBukkit woolbattle;

        public ArrowBombListener(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
            this.woolbattle = woolbattle;
        }

        @Override protected boolean activateRight(UserPerk perk) {
            Snowball snowball = perk.owner().getBukkitEntity().launchProjectile(Snowball.class);
            snowball.setVelocity(snowball.getVelocity()); // de-sync fix
            snowball.setMetadata("perk", new FixedMetadataValue(woolbattle, perk));
            return true;
        }

        @EventHandler public void handle(ProjectileHitEvent event) {
            if (!(event.getEntity() instanceof Snowball)) return;
            Snowball snowball = (Snowball) event.getEntity();
            if (!snowball.hasMetadata("perk")) return;
            if (!(snowball.getMetadata("perk").get(0).value() instanceof UserPerk)) return;
            UserPerk perk = (UserPerk) snowball.getMetadata("perk").get(0).value();
            WBUser user = perk.owner();
            int count = 30;
            Vector dir = new Vector(1, 0.3, 0);

            Location l = new Location(null, 0, 0, 0);
            l.setDirection(dir);
            for (int i = 0; i < count; i++) {
                float pitch = r.nextFloat() * 50F + 10;
                float yaw = r.nextInt(360 * 2) / 2F;
                if (r.nextFloat() < 0.1) {
                    pitch = pitch - 10 + r.nextFloat() * 40F;
                    l.setPitch(pitch);
                } else {
                    l.setPitch(-pitch);
                }
                l.setYaw(yaw);
                dir = l.getDirection();
                Arrow arrow = user.getBukkitEntity().getWorld().spawnArrow(snowball.getLocation(), dir, .9F, 0);
                arrow.setMetadata("noParticles", new FixedMetadataValue(woolbattle, true));
                ArrowPerk.claimArrow(woolbattle, arrow, user, 3, 2);
            }
        }

        @EventHandler public void handle(EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Snowball) {
                Snowball snowball = (Snowball) event.getDamager();
                if (!snowball.hasMetadata("perk")) return;
                if (snowball.getMetadata("perk").get(0).value().equals(perk().perkName())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}

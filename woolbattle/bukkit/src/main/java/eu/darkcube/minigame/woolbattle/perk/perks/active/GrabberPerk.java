/*
 * Copyright (c) 2023-2024. [DarkCube]
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
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class GrabberPerk extends Perk {
    public static final PerkName GRABBER = new PerkName("GRABBER");

    public GrabberPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, GRABBER, 7, 10, Item.PERK_GRABBER, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_GRABBER_COOLDOWN, woolbattle));
        addListener(new ListenerGrabber(this, woolbattle));
    }

    private static class ListenerGrabber extends BasicPerkListener {

        private final WoolBattleBukkit woolbattle;
        private final Key DATA_GRABBED;
        private final Key DATA_SCHEDULER;
        private final Key DATA_PERK;

        public ListenerGrabber(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
            this.woolbattle = woolbattle;
            DATA_GRABBED = Key.key(woolbattle, "grabber_grabbed");
            DATA_SCHEDULER = Key.key(woolbattle, "grabber_scheduler");
            DATA_PERK = Key.key(woolbattle, "perk_grabber");
        }

        public boolean hasTarget(WBUser user) {
            return user.user().metadata().has(DATA_GRABBED);
        }

        public boolean teleportTarget(WBUser user) {
            if (!hasTarget(user)) {
                return false;
            }
            WBUser grabbed = user.user().metadata().remove(DATA_GRABBED);
            user.user().metadata().<Scheduler>remove(DATA_SCHEDULER).cancel();
            UserPerk perk = user.user().metadata().remove(DATA_PERK);
            perk.currentPerkItem().setItem();
            grabbed.getBukkitEntity().teleport(user.getBukkitEntity());
            perk.cooldown(perk.perk().cooldown().cooldown());
            return true;
        }

        public void setTarget(WBUser user, WBUser target) {
            user.user().metadata().set(DATA_GRABBED, target);
            user.user().metadata().<UserPerk>get(DATA_PERK).currentPerkItem().setItem();
        }

        @Override
        protected boolean activateRight(UserPerk perk) {
            WBUser user = perk.owner();
            if (teleportTarget(user)) {
                return true;
            }
            if (user.user().metadata().has(DATA_SCHEDULER)) {
                return false;
            }
            payForThePerk(perk);
            user.user().metadata().set(DATA_SCHEDULER, new Scheduler(woolbattle) {
                {
                    runTaskLater(TimeUnit.SECOND.itoTicks(5));
                }

                @Override
                public void run() {
                    perk.cooldown(perk.perk().cooldown().cooldown());
                    user.user().metadata().remove(DATA_GRABBED);
                    user.user().metadata().remove(DATA_SCHEDULER);
                    user.user().metadata().remove(DATA_PERK);
                    perk.currentPerkItem().setItem();
                }
            });
            Player p = user.getBukkitEntity();
            Egg egg = p.getWorld().spawn(p.getEyeLocation(), Egg.class);
            egg.setShooter(p);
            egg.setVelocity(p.getLocation().getDirection().multiply(1.5));
            egg.setMetadata("perk", new FixedMetadataValue(woolbattle, GrabberPerk.GRABBER));
            user.user().metadata().set(DATA_PERK, perk);
            return false;
        }

        @Override
        protected void activated(UserPerk perk) {
            perk.cooldown(perk.perk().cooldown().cooldown());
        }

        @EventHandler
        public void handle(EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Egg && e.getEntity() instanceof Player && ((Egg) e.getDamager()).getShooter() instanceof Player) {
                Egg egg = (Egg) e.getDamager();
                if (egg.hasMetadata("perk") && egg.getMetadata("perk").get(0).value().equals(GrabberPerk.GRABBER)) {
                    Player target = (Player) e.getEntity();
                    WBUser targetUser = WBUser.getUser(target);
                    if (targetUser.projectileImmunityTicks() > 0) {
                        e.setCancelled(true);
                        return;
                    }
                    Player p = (Player) egg.getShooter();
                    WBUser user = WBUser.getUser(p);
                    if (woolbattle.ingame().playerUtil().attack(user, targetUser) || (user.getTeam() == targetUser.getTeam() && user != targetUser)) {
                        if (user.getTeam() == targetUser.getTeam() && user != targetUser) {
                            e.setCancelled(true);
                        }
                        setTarget(user, targetUser);
                    } else {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

}

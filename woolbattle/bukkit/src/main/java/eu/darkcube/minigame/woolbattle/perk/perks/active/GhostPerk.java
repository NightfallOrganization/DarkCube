/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.perk.active.EventGhostStateChange;
import eu.darkcube.minigame.woolbattle.event.perk.passive.EventMayDoubleJump;
import eu.darkcube.minigame.woolbattle.event.user.EventUserKill;
import eu.darkcube.minigame.woolbattle.event.user.EventUserMayAttack;
import eu.darkcube.minigame.woolbattle.event.user.UserArmorSetEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GhostPerk extends Perk {
    public static final PerkName GHOST = new PerkName("GHOST");

    public GhostPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, GHOST, 30, 20, Item.PERK_GHOST, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_GHOST_COOLDOWN, woolbattle));
        addListener(new ListenerGhost(this, woolbattle));
    }

    private static class ListenerGhost extends BasicPerkListener {
        private final WoolBattleBukkit woolbattle;
        private final Key DATA_GHOST_PERK;
        private final Key DATA_GHOST_POS;
        private final Key DATA_GHOST_ATTACKS;
        private final Key DATA_GHOST_FORCE_ATTACKABLE;

        public ListenerGhost(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
            this.woolbattle = woolbattle;
            DATA_GHOST_PERK = Key.key(woolbattle, "ghost_perk");
            DATA_GHOST_POS = Key.key(woolbattle, "ghost_pos");
            DATA_GHOST_ATTACKS = Key.key(woolbattle, "ghost_attacks");
            DATA_GHOST_FORCE_ATTACKABLE = Key.key(woolbattle, "ghost_force_attackable");
        }

        public void reset(WBUser user) {
            reset0(user, user.user().metadata().get(DATA_GHOST_POS));
        }

        public boolean isGhost(WBUser user) {
            return user.user().metadata().has(DATA_GHOST_POS);
        }

        private void reset0(WBUser user, Location loc) {
            user.getBukkitEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
            user.getBukkitEntity().teleport(loc);
            user.user().metadata().remove(DATA_GHOST_POS);
            user.user().metadata().remove(DATA_GHOST_ATTACKS);
            UserPerk perk = user.user().metadata().remove(DATA_GHOST_PERK);
            perk.cooldown(perk.perk().cooldown().cooldown());
            user.getBukkitEntity().setHealth(user.getBukkitEntity().getMaxHealth());
            woolbattle.ingame().playerUtil().setArmor(user);
            ParticleEffect.MOB_APPEARANCE.display(0, 0, 0, 0, 1, user.getBukkitEntity().getLocation(), user.getBukkitEntity());
            Bukkit.getPluginManager().callEvent(new EventGhostStateChange(user, false));
        }

        @Override
        protected boolean activateRight(UserPerk perk) {
            WBUser user = perk.owner();
            if (isGhost(user)) {
                return false;
            }
            payForThePerk(perk);
            Player p = user.getBukkitEntity();
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 200, false, false));
            user.user().metadata().set(DATA_GHOST_POS, p.getLocation());
            user.user().metadata().set(DATA_GHOST_PERK, perk);
            woolbattle.ingame().playerUtil().setArmor(user);
            p.setMaxHealth(20);

            Bukkit.getPluginManager().callEvent(new EventGhostStateChange(user, true));

            new Scheduler(woolbattle) {

                @Override
                public void run() {
                    if (!isGhost(user)) {
                        this.cancel();
                        p.removePotionEffect(PotionEffectType.BLINDNESS);
                        p.removePotionEffect(PotionEffectType.SPEED);
                        return;
                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 0, false, false), true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15, 10, false, false), true);
                }

            }.runTaskTimer(1);
            return false;
        }

        @EventHandler
        public void handle(EventUserMayAttack event) {
            if (event.mayAttack() && isGhost(event.user())) {
                if (!event.user().user().metadata().getOr(DATA_GHOST_FORCE_ATTACKABLE, false)) event.mayAttack(false);
            }
        }

        @EventHandler
        public void handle(EventUserKill event) {
            if (isGhost(event.user())) {
                event.setCancelled(true);
                reset(event.user());
            }
        }

        @EventHandler
        public void handle(UserArmorSetEvent event) {
            if (isGhost(event.user())) event.color(Color.WHITE);
        }

        @EventHandler
        public void handle(EventMayDoubleJump event) {
            if (event.mayDoubleJump()) {
                if (isGhost(event.user())) {
                    event.mayDoubleJump(false);
                }
            }
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void handle(PlayerQuitEvent e) {
            WBUser user = WBUser.getUser(e.getPlayer());
            user.user().metadata().remove(DATA_GHOST_POS);
            user.user().metadata().remove(DATA_GHOST_ATTACKS);
            user.user().metadata().remove(DATA_GHOST_PERK);
        }

        @EventHandler
        public void handle(EntityDamageByEntityEvent e) {
            if (e.getCause() == DamageCause.FALL) {
                return;
            }
            if (e.getEntity() instanceof Player) {
                WBUser user = WBUser.getUser((Player) e.getEntity());
                if (e.getDamager() instanceof Projectile) {
                    if (!(((Projectile) e.getDamager()).getShooter() instanceof Player)) {
                        return;
                    }
                } else if (!(e.getDamager() instanceof Player)) {
                    return;
                }

                WBUser attacker = e.getDamager() instanceof Player ? WBUser.getUser((Player) e.getDamager()) : WBUser.getUser((Player) ((Projectile) e.getDamager()).getShooter());
                if (attacker.getTeam().isSpectator() && !attacker.isTrollMode()) {
                    e.setCancelled(true);
                    return;
                }

                if (isGhost(user)) {
                    Player p = user.getBukkitEntity();
                    if (p.getNoDamageTicks() == 0) {
                        user.user().metadata().set(DATA_GHOST_FORCE_ATTACKABLE, true);
                        boolean suc = woolbattle.ingame().playerUtil().attack(attacker, user);
                        user.user().metadata().remove(DATA_GHOST_FORCE_ATTACKABLE);
                        if (suc) {
                            if (!user.user().metadata().has(DATA_GHOST_ATTACKS)) {
                                user.user().metadata().set(DATA_GHOST_ATTACKS, 1);
                                p.setHealth(p.getMaxHealth() / 2);
                            } else {
                                p.setHealth(p.getMaxHealth());
                                reset(user);
                            }
                            p.damage(0);
                            p.setNoDamageTicks(10);
                            if (e.getDamager() instanceof Projectile) {
                                attacker.getBukkitEntity().playSound(attacker.getBukkitEntity().getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
                            }
                        }
                    }
                    e.setCancelled(true);
                }
            }
        }

    }

}

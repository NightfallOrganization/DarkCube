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
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.util.data.Key;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class MinigunPerk extends Perk {
    public static final PerkName MINIGUN = new PerkName("MINIGUN");

    public MinigunPerk(WoolBattleBukkit woolbattle) {
        super(ActivationType.ACTIVE, MINIGUN, 10, 1, CostType.PER_SHOT, Item.PERK_MINIGUN, (user, perk, id, perkSlot, wb) -> new CooldownUserPerk(user, id, perkSlot, perk, Item.PERK_MINIGUN_COOLDOWN, woolbattle));
        addListener(new ListenerMinigun(this, woolbattle));
    }

    public static class ListenerMinigun extends BasicPerkListener {

        private final Key DATA_SCHEDULER = new Key(woolbattle, "minigunScheduler");

        public ListenerMinigun(Perk perk, WoolBattleBukkit woolbattle) {
            super(perk, woolbattle);
        }

        @Override protected boolean activateRight(UserPerk perk) {
            WBUser user = perk.owner();
            if (user.user().getMetaDataStorage().has(DATA_SCHEDULER)) {
                return false;
            }
            user.user().getMetaDataStorage().set(DATA_SCHEDULER, new Scheduler(woolbattle) {
                private int count = 0;

                {
                    runTaskTimer(3);
                }

                @Override public void cancel() {
                    perk.cooldown(perk.perk().cooldown().cooldown());
                    super.cancel();
                }

                @Override public void run() {
                    Player p = user.getBukkitEntity();
                    ItemStack item = p.getItemInHand();

                    if (count >= 20 || item == null || !item.equals(perk.currentPerkItem().calculateItem())) {
                        stop(user);
                        return;
                    }
                    count++;
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 3, false, false), true);
                    Snowball s = p.getWorld().spawn(p.getEyeLocation(), Snowball.class);
                    s.setShooter(p);
                    s.setVelocity(p.getLocation().getDirection().multiply(2.5));
                    s.setMetadata("type", new FixedMetadataValue(woolbattle, "minigun"));
                    payForThePerk(perk);
                }
            });

            return false;
        }

        @EventHandler public void handle(EntityDamageByEntityEvent event) {
            if (event.getEntity() instanceof Player) {
                WBUser target = WBUser.getUser((Player) event.getEntity());
                if (event.getDamager() instanceof Snowball) {
                    Snowball ball = (Snowball) event.getDamager();
                    if (ball.getShooter() instanceof Player) {
                        Player p = (Player) ball.getShooter();
                        WBUser user = WBUser.getUser(p);

                        if (ball.hasMetadata("type") && ball.getMetadata("type").get(0).asString().equals("minigun")) {
                            event.setCancelled(true);
                            if (target.projectileImmunityTicks() > 0) {
                                return;
                            }
                            if (!woolbattle.ingame().playerUtil().attack(user, target)) {
                                return;
                            }
                            target.getBukkitEntity().damage(0);

                            target
                                    .getBukkitEntity()
                                    .setVelocity(ball
                                            .getVelocity()
                                            .setY(0)
                                            .normalize()
                                            .multiply(.47 + new Random().nextDouble() / 70 + 1.1)
                                            .setY(.400023));
                        }
                    }
                }
            }
        }

        @EventHandler public void handle(PlayerItemHeldEvent event) {
            WBUser user = WBUser.getUser(event.getPlayer());
            stop(user);
        }

        private void stop(WBUser user) {
            if (user.user().getMetaDataStorage().has(DATA_SCHEDULER)) {
                user.user().getMetaDataStorage().<Scheduler>remove(DATA_SCHEDULER).cancel();
            }
        }
    }

}

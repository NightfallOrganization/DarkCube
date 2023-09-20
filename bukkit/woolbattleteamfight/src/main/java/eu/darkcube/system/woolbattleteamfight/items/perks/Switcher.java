/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.items.perks;

import eu.darkcube.system.woolbattleteamfight.Main;
import eu.darkcube.system.woolbattleteamfight.game.GameItemManager;
import eu.darkcube.system.woolbattleteamfight.wool.WoolReducer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Switcher implements Listener {

    private static final int SWITCHER_SLOT = 3; // Slots beginnen bei 0, daher ist Slot 5 eigentlich Index 3
    private GameItemManager gameItemManager;

    public Switcher(GameItemManager gameItemManager) {
        this.gameItemManager = gameItemManager;
    }

    @EventHandler
    public void onPlayerUseSwitcher(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack itemInHand = player.getItemInHand();

        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                && itemInHand != null
                && itemInHand.getType() == Material.SNOW_BALL) {

            if (!WoolReducer.hasEnoughWool(player, 8)) {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
                event.setCancelled(true);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                    ItemStack customSwitcher = gameItemManager.createItem(Material.SNOW_BALL, 1, "&aTauscher",
                            new String[]{"&7Tausche den Platz mit deinem Gegner!"},
                            new Enchantment[]{Enchantment.DURABILITY},
                            new int[]{1}, true);
                    player.getInventory().setItem(SWITCHER_SLOT, customSwitcher);
                }, 1L);

                return;
            }

            WoolReducer.removeWool(player, 8);

            new BukkitRunnable() {
                int secondsLeft = 7;

                @Override
                public void run() {
                    if (secondsLeft > 0) {
                        ItemStack customFireworkCharge = gameItemManager.createItem(Material.SNOW_BALL, secondsLeft, "&cTauscher",
                                new String[]{"&7Tausche den Platz mit deinem Gegner!"},
                                new Enchantment[]{},
                                new int[]{},
                                false);

                        player.getInventory().setItem(SWITCHER_SLOT, customFireworkCharge);
                        secondsLeft--;
                    } else {
                        player.getInventory().setItem(SWITCHER_SLOT,
                                gameItemManager.createItem(Material.SNOW_BALL, 1, "&aTauscher",
                                        new String[]{"&7Tausche den Platz mit deinem Gegner!"},
                                        new Enchantment[]{Enchantment.DURABILITY},
                                        new int[]{1}, true));
                        this.cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        }
    }



    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Player) {
            Player hitPlayer = (Player) event.getEntity();

            if (event.getDamager() instanceof Snowball) {
                Snowball snowball = (Snowball) event.getDamager();

                if (snowball.getShooter() instanceof Player) {
                    Player shooter = (Player) snowball.getShooter();

                    Location shooterLoc = shooter.getLocation();
                    Location hitPlayerLoc = hitPlayer.getLocation();

                    shooter.teleport(hitPlayerLoc);
                    hitPlayer.teleport(shooterLoc);

                    shooter.playSound(shooter.getLocation(), Sound.ITEM_PICKUP, 1, 1.8F);
                    hitPlayer.playSound(hitPlayer.getLocation(), Sound.ITEM_PICKUP, 1, 1.8F);

                    event.setCancelled(true);
                }
            }
        }
    }


}

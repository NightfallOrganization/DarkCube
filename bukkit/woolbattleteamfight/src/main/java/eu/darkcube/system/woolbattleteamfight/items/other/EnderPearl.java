/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.items.other;

import eu.darkcube.system.woolbattleteamfight.Main;
import eu.darkcube.system.woolbattleteamfight.game.GameItemManager;
import eu.darkcube.system.woolbattleteamfight.wool.WoolReducer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnderPearl implements Listener {
    private static final int ENDER_PEARL_SLOT = 4; // Slots beginnen bei 0, daher ist Slot 5 eigentlich Index 4
    private GameItemManager gameItemManager;

    public EnderPearl(GameItemManager gameItemManager) {
        this.gameItemManager = gameItemManager;
    }

    @EventHandler
    public void onPlayerUseEnderPearl(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack itemInHand = player.getItemInHand();

        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                && itemInHand != null
                && itemInHand.getType() == Material.ENDER_PEARL) {

            if (!WoolReducer.hasEnoughWool(player, 8)) {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
                event.setCancelled(true); // Verhindert, dass die Enderperle geworfen wird

                // Setze die Enderperle kurz danach wieder in den Slot des Spielers ein
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                    ItemStack customEnderPearl = gameItemManager.createItem(Material.ENDER_PEARL, 1, "&aEnderperle",
                            new String[]{"&7Teleportiere dich hinter deinem Gegner!"},
                            new Enchantment[]{Enchantment.DURABILITY},
                            new int[]{1}, true);
                    player.getInventory().setItem(ENDER_PEARL_SLOT, customEnderPearl);
                }, 1L);

                return;
            }

            WoolReducer.removeWool(player, 8); // Entfernt 8 Wolle aus dem Inventar des Spielers

            new BukkitRunnable() {
                int secondsLeft = 5;

                @Override
                public void run() {
                    if (secondsLeft > 0) {
                        ItemStack customFireworkCharge = gameItemManager.createItem(Material.FIREWORK_CHARGE, secondsLeft, "&cEnderperle",
                                new String[]{"&7Teleportiere dich hinter deinem Gegner!"},
                                new Enchantment[]{},
                                new int[]{},
                                false);

                        player.getInventory().setItem(ENDER_PEARL_SLOT, customFireworkCharge);
                        secondsLeft--;
                    } else {
                        player.getInventory().setItem(ENDER_PEARL_SLOT,
                                gameItemManager.createItem(Material.ENDER_PEARL, 1, "&aEnderperle",
                                        new String[]{"&7Teleportiere dich hinter deinem Gegner!"},
                                        new Enchantment[]{Enchantment.DURABILITY},
                                        new int[]{1}, true));
                        this.cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        }
    }

}

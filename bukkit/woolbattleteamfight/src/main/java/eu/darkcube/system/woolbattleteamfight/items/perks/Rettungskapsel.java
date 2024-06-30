/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.items.perks;

import eu.darkcube.system.woolbattleteamfight.Main;
import eu.darkcube.system.woolbattleteamfight.game.GameItemManager;
import eu.darkcube.system.woolbattleteamfight.wool.WoolManager;
import eu.darkcube.system.woolbattleteamfight.wool.WoolReducer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Rettungskapsel implements Listener {
    private static final int RETTUNGSKAPSEL_SLOT = 2;
    private GameItemManager gameItemManager;

    public Rettungskapsel(GameItemManager gameItemManager) {
        this.gameItemManager = gameItemManager;
    }

    public void centerPlayer(Player player) {
        Location loc = player.getLocation();
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getY() + 0);
        loc.setZ(loc.getBlockZ() + 0.5);
        player.teleport(loc);
    }
    @EventHandler
    public void onPlayerUseRettungskapsel(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack itemInHand = player.getItemInHand();

        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
                && itemInHand != null
                && itemInHand.getType() == Material.STAINED_GLASS
                && itemInHand.getDurability() == 14) {  // Rotes Glas ist STAINED_GLASS mit Durability 14

            if (!WoolReducer.hasEnoughWool(player, 24)) {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
                event.setCancelled(true);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                    ItemStack rettungskapsel = gameItemManager.createItem(Material.STAINED_GLASS, 1, "&aRettungskapsel",
                            new String[]{"&7Rette dich in einer Kapsel aus Wolle!"},
                            new Enchantment[]{Enchantment.DURABILITY},
                            new int[]{1}, true);
                    player.getInventory().setItem(RETTUNGSKAPSEL_SLOT, rettungskapsel);
                }, 1L);

                return;
            }

            WoolReducer.removeWool(player, 24);

            centerPlayer(player);

            WoolManager woolManager = Main.getInstance().getWoolManager();
            World world = player.getWorld();
            Location playerLoc = player.getLocation();

            Block blockAbove = world.getBlockAt(playerLoc.getBlockX(), playerLoc.getBlockY() + 2, playerLoc.getBlockZ());
            if (blockAbove.getType() == Material.AIR) {
                blockAbove.setType(Material.WOOL);
                woolManager.addPlayerPlacedBlock(blockAbove, player);
            }

            Block blockBelow = world.getBlockAt(playerLoc.getBlockX(), playerLoc.getBlockY() - 1, playerLoc.getBlockZ());
            if (blockBelow.getType() == Material.AIR) {
                blockBelow.setType(Material.WOOL);
                woolManager.addPlayerPlacedBlock(blockBelow, player);
            }

            // Zwei Blöcke (im Fuß- und im Kopfbereich) an jeder Seite vom Spieler
            Block[] surroundingBlocks = {
                    world.getBlockAt(playerLoc.getBlockX() + 1, playerLoc.getBlockY(), playerLoc.getBlockZ()),
                    world.getBlockAt(playerLoc.getBlockX() + 1, playerLoc.getBlockY() + 1, playerLoc.getBlockZ()),
                    world.getBlockAt(playerLoc.getBlockX() - 1, playerLoc.getBlockY(), playerLoc.getBlockZ()),
                    world.getBlockAt(playerLoc.getBlockX() - 1, playerLoc.getBlockY() + 1, playerLoc.getBlockZ()),
                    world.getBlockAt(playerLoc.getBlockX(), playerLoc.getBlockY(), playerLoc.getBlockZ() + 1),
                    world.getBlockAt(playerLoc.getBlockX(), playerLoc.getBlockY() + 1, playerLoc.getBlockZ() + 1),
                    world.getBlockAt(playerLoc.getBlockX(), playerLoc.getBlockY(), playerLoc.getBlockZ() - 1),
                    world.getBlockAt(playerLoc.getBlockX(), playerLoc.getBlockY() + 1, playerLoc.getBlockZ() - 1)
            };

            for (Block block : surroundingBlocks) {
                if (block.getType() == Material.AIR) {
                    block.setType(Material.WOOL);
                    woolManager.addPlayerPlacedBlock(block, player);
                }
            }

            new BukkitRunnable() {
                int secondsLeft = 30;

                @Override
                public void run() {
                    if (secondsLeft > 0) {
                        ItemStack cooldownItem = gameItemManager.createItem(Material.STAINED_GLASS, secondsLeft, "&cRettungskapsel",
                                new String[]{"&7Rette dich in einer Kapsel aus Wolle!"},
                                new Enchantment[]{},
                                new int[]{},
                                false);
                        cooldownItem.setDurability((short) 0);  // Weißes Glas ist STAINED_GLASS mit Durability 0

                        player.getInventory().setItem(RETTUNGSKAPSEL_SLOT, cooldownItem);
                        secondsLeft--;
                    } else {
                        player.getInventory().setItem(RETTUNGSKAPSEL_SLOT,
                                gameItemManager.createItem(Material.STAINED_GLASS, 1, "&aRettungskapsel",
                                        new String[]{"&7Rette dich in einer Kapsel aus Wolle!"},
                                        new Enchantment[]{Enchantment.DURABILITY},
                                        new int[]{1}, true));
                        this.cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 20);
        }
    }
}

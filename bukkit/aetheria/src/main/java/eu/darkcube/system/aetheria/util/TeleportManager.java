/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.util;

import com.viaversion.viaversion.util.ChatColorUtil;
import eu.darkcube.system.aetheria.Aetheria;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class TeleportManager implements Listener {

    private final Aetheria plugin;
    private final HashMap<Location, Inventory> teleportInventories = new HashMap<>();

    public TeleportManager(Aetheria plugin) {
        this.plugin = plugin;
    }

    public static ItemStack getTeleportCatalyst() {
        ItemStack catalyst = new ItemStack(Material.PURPUR_PILLAR);
        ItemMeta meta = catalyst.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Teleport Catalyst");
        catalyst.setItemMeta(meta);
        return catalyst;
    }

    public static ItemStack getManaStone() {
        ItemStack manaStone = new ItemStack(Material.PURPUR_BLOCK);
        ItemMeta meta = manaStone.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Mana Stone");
        manaStone.setItemMeta(meta);
        return manaStone;
    }

    public static ItemStack getManaStairs() {
        ItemStack manaStairs = new ItemStack(Material.PURPUR_STAIRS);
        ItemMeta meta = manaStairs.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Mana Stairs");
        manaStairs.setItemMeta(meta);
        return manaStairs;
    }

    private boolean isOnCorrectPattern(Location catalystLocation) {
        Block baseBlock = catalystLocation.getBlock();
        return
                // Überprüfe den Teleport Catalyst
                checkBlock(baseBlock, Material.PURPUR_PILLAR, "TeleportCatalyst") &&

                        // Überprüfe die Mana Stones
                        checkBlock(baseBlock.getRelative(BlockFace.NORTH), Material.PURPUR_BLOCK, "ManaStone") &&
                        checkBlock(baseBlock.getRelative(BlockFace.WEST), Material.PURPUR_BLOCK, "ManaStone") &&
                        checkBlock(baseBlock.getRelative(BlockFace.EAST), Material.PURPUR_BLOCK, "ManaStone") &&
                        checkBlock(baseBlock.getRelative(BlockFace.SOUTH), Material.PURPUR_BLOCK, "ManaStone") &&

                        // Überprüfe die Mana Stairs
                        checkBlock(baseBlock.getRelative(1, 0, -1), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(-1, 0, -1), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(-1, 0, 1), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(1, 0, 1), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(1, 0, 2), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(-1, 0, 2), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(-2, 0, 1), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(-2, 0, -1), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(2, 0, 1), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(2, 0, -1), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(1, 0, -2), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(-1, 0, -2), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(2, 0, 0), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(-2, 0, 0), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(0, 0, 2), Material.PURPUR_STAIRS, "ManaStairs") &&
                        checkBlock(baseBlock.getRelative(0, 0, -2), Material.PURPUR_STAIRS, "ManaStairs");

    }

    private Location getCatalystNearPlayerIfCorrectPattern(Player player) {
        Block blockBelow = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

        // Die Koordinaten, die Sie angegeben haben
        int[][] coordinates = {
                {1, 0, -1},
                {-1, 0, -1},
                {-1, 0, 1},
                {1, 0, 1},
                {1, 0, 2},
                {-1, 0, 2},
                {-2, 0, 1},
                {-2, 0, -1},
                {2, 0, 1},
                {2, 0, -1},
                {1, 0, -2},
                {-1, 0, -2},
                {2, 0, 0},
                {-2, 0, 0},
                {0, 0, 2},
                {0, 0, -2},
                {0, 0, 1},
                {0, 0, -1},
                {1, 0, 0},
                {-1, 0, 0},
                {0, 0, 0}
        };

        for (int[] coord : coordinates) {
            Block checkBlock = blockBelow.getRelative(coord[0], coord[1], coord[2]);
            if (checkBlock.getType() == Material.PURPUR_PILLAR && checkBlock.hasMetadata("TeleportCatalyst")) {
                if(isOnCorrectPattern(checkBlock.getLocation())) {
                    return checkBlock.getLocation();
                }
            }
        }
        return null;
    }

    private boolean checkBlock(Block block, Material material, String metadataKey) {
        return block.getType() == material && block.hasMetadata(metadataKey);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking()) {
            Location catalystLocation = getCatalystNearPlayerIfCorrectPattern(player);
            if(catalystLocation != null) {
                openTeleportMenu(player);
            }
        }
    }


    private void openTeleportMenu(Player player) {
        Inventory teleportMenu = getOrCreateTeleportMenu(player);
        player.openInventory(teleportMenu);
    }


    private void removeTeleportCatalystFromInventories(Location removedLocation) {
        int locationHashCode = removedLocation.hashCode();

        for (Inventory inv : teleportInventories.values()) {
            for (ItemStack item : inv.getContents()) {
                if (item != null && item.hasItemMeta() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == locationHashCode) {
                    inv.remove(item);
                    break;
                }
            }
        }
    }



    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().isSimilar(getTeleportCatalyst())) {
            event.getBlockPlaced().setMetadata("TeleportCatalyst", new FixedMetadataValue(plugin, true));
            teleportInventories.put(event.getBlockPlaced().getLocation(), null);
        } else if (event.getItemInHand().isSimilar(getManaStone())) {
            event.getBlockPlaced().setMetadata("ManaStone", new FixedMetadataValue(plugin, true));

        } else if (event.getItemInHand().isSimilar(getManaStairs())) {
            event.getBlockPlaced().setMetadata("ManaStairs", new FixedMetadataValue(plugin, true));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (block.getType() == Material.PURPUR_PILLAR && block.hasMetadata("TeleportCatalyst")) {
            // Entfernt den Teleport Catalyst aus den Inventaren
            teleportInventories.remove(block.getLocation());

            event.setDropItems(false);
            player.getWorld().dropItemNaturally(block.getLocation(), getTeleportCatalyst());

        } else if (block.getType() == Material.PURPUR_BLOCK && block.hasMetadata("ManaStone")) {
            event.setDropItems(false);
            player.getWorld().dropItemNaturally(block.getLocation(), getManaStone());

        } else if (block.getType() == Material.PURPUR_STAIRS && block.hasMetadata("ManaStairs")) {
            event.setDropItems(false);
            player.getWorld().dropItemNaturally(block.getLocation(), getManaStairs());
        }
    }

    private Inventory getOrCreateTeleportMenu(Player player) {
        int size = 45;
        Inventory teleportMenu = Bukkit.createInventory(null, size, ChatColor.WHITE + "\udaff\udfefⲊ");

        // Erstellen Sie ein Set der Slots, die Sie überspringen möchten
        Set<Integer> emptySlots = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 26, 35, 44, 18, 27, 36, 37, 38, 39, 40, 41, 42, 43));

        int index = 0;
        for (Location loc : teleportInventories.keySet()) {
            ItemStack teleportItem = new ItemStack(Material.PURPUR_PILLAR);
            ItemMeta meta = teleportItem.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + "Teleport zu:");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
            lore.add(ChatColor.DARK_GRAY + "World: " + loc.getWorld().getName() + " ");
            meta.setLore(lore);
            meta.setCustomModelData(loc.hashCode());
            teleportItem.setItemMeta(meta);

            // Finden Sie den nächsten verfügbaren Slot, der nicht im Set ist
            while (emptySlots.contains(index) && index < size) {
                index++;
            }

            if (index < size) {
                teleportMenu.setItem(index, teleportItem);
                index++;
            }
        }

        return teleportMenu;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasCustomModelData()) {
            return;  // Überprüfen Sie, ob das Item gültig ist
        }

        int customModelData = clickedItem.getItemMeta().getCustomModelData();

        for (Location loc : teleportInventories.keySet()) {
            if (loc.hashCode() == customModelData) {
                Player player = (Player) event.getWhoClicked();
                Location teleportLocation = loc.clone().add(0.5, 3, 0.5);
                player.teleport(teleportLocation);
                player.closeInventory();
                return;
            }
        }
    }

}
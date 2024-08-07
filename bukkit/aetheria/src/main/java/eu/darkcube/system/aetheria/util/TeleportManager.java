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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TeleportManager implements Listener {

    private final Aetheria plugin;
    private final HashMap<Location, Inventory> teleportInventories = new HashMap<>();
    private File dataFile;
    private YamlConfiguration yamlConfig;

    public TeleportManager(Aetheria plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "teleporters.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.yamlConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadTeleporters();
    }

    private void saveTeleporters() {
        ConfigurationSection teleportersSection = yamlConfig.createSection("teleporters");
        for (Map.Entry<Location, Inventory> entry : teleportInventories.entrySet()) {
            String locStr = entry.getKey().toString();
            teleportersSection.set(locStr, entry.getValue().getContents());
        }

        try {
            yamlConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadTeleporters() {
        ConfigurationSection teleportersSection = yamlConfig.getConfigurationSection("teleporters");
        if (teleportersSection != null) {
            for (String key : teleportersSection.getKeys(false)) {
                Location loc = Location.deserialize(StringToMap(key));
                ItemStack[] items = (ItemStack[]) teleportersSection.getList(key).toArray(new ItemStack[0]);
                Inventory inv = Bukkit.createInventory(null, 45, ChatColor.WHITE + "\udaff\udfefⲊ");
                inv.setContents(items);
                teleportInventories.put(loc, inv);
            }
        }
    }

    private void updateTeleportMenu() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory openInv = player.getOpenInventory().getTopInventory();
            if (openInv != null && openInv.getHolder() instanceof InventoryView && ((InventoryView) openInv.getHolder()).getTitle().equals(ChatColor.WHITE + "\udaff\udfefⲊ")) {
                openTeleportMenu(player);
            }
        }
    }


    private Map<String, Object> StringToMap(String str) {
        String[] entries = str.substring(1, str.length() - 1).split(",");
        Map<String, Object> map = new HashMap<>();
        for (String entry : entries) {
            String[] split = entry.split("=");
            map.put(split[0].trim(), split[1].trim());
        }
        return map;
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



    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().isSimilar(getTeleportCatalyst())) {
            event.getBlockPlaced().setMetadata("TeleportCatalyst", new FixedMetadataValue(plugin, true));

            // Erstellen Sie das Inventory hier:
            Inventory newTeleportInventory = Bukkit.createInventory(null, 45, ChatColor.WHITE + "\udaff\udfefⲊ");
            teleportInventories.put(event.getBlockPlaced().getLocation(), newTeleportInventory);

            saveTeleporters();
        } else if (event.getItemInHand().isSimilar(getManaStone())) {
            event.getBlockPlaced().setMetadata("ManaStone", new FixedMetadataValue(plugin, true));

        } else if (event.getItemInHand().isSimilar(getManaStairs())) {
            event.getBlockPlaced().setMetadata("ManaStairs", new FixedMetadataValue(plugin, true));
        }
        updateTeleportMenu();
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getType() == Material.PURPUR_PILLAR && block.hasMetadata("TeleportCatalyst")) {
            teleportInventories.remove(block.getLocation());
            saveTeleporters();
            event.setDropItems(false);
            player.getWorld().dropItemNaturally(block.getLocation(), getTeleportCatalyst());

        } else if (block.getType() == Material.PURPUR_BLOCK && block.hasMetadata("ManaStone")) {
            event.setDropItems(false);
            player.getWorld().dropItemNaturally(block.getLocation(), getManaStone());

        } else if (block.getType() == Material.PURPUR_STAIRS && block.hasMetadata("ManaStairs")) {
            event.setDropItems(false);
            player.getWorld().dropItemNaturally(block.getLocation(), getManaStairs());
        }
        updateTeleportMenu();
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
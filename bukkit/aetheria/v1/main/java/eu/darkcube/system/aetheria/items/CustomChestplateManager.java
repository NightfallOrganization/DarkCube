/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.items;

import eu.darkcube.system.aetheria.Aetheria;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CustomChestplateManager implements Listener {
    private final Aetheria plugin;
    public final NamespacedKey itemLevelKey;
    public final NamespacedKey durabilityKey;
    public final NamespacedKey maxDurabilityKey;

    public CustomChestplateManager(Aetheria plugin) {
        this.plugin = plugin;
        this.itemLevelKey = new NamespacedKey(plugin, "item_level");
        this.durabilityKey = new NamespacedKey(plugin, "durability");
        this.maxDurabilityKey = new NamespacedKey(plugin, "max_durability");
    }

    public ItemStack getCustomChestplate(int level) {
        return createCustomChestplate(level);
    }

    public void increaseItemLevel(ItemStack item, int levelToAdd) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            int currentLevel = meta.getPersistentDataContainer().getOrDefault(itemLevelKey, PersistentDataType.INTEGER, 0);
            int newLevel = currentLevel + levelToAdd;

            meta.getPersistentDataContainer().set(itemLevelKey, PersistentDataType.INTEGER, newLevel);
            item.setItemMeta(meta);
            updateChestplateLore(item, newLevel);
        }
    }

    public ItemStack createCustomChestplate(int level) {
        // Erstellen Sie ein neues Netherite Chestplate ItemStack
        ItemStack customChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE, 1);
        ItemMeta meta = customChestplate.getItemMeta();

        // Setzen Sie den Displaynamen
        meta.setDisplayName("§7« §fNormal Chestplate §7»");

        // Hinzufügen von Persistent Data (z.B. Level, Durability, etc.)
        meta.getPersistentDataContainer().set(itemLevelKey, PersistentDataType.INTEGER, level);
        int durability = 200;
        int maxDurability = getMaxDurabilityForLevel(level);
        meta.getPersistentDataContainer().set(durabilityKey, PersistentDataType.INTEGER, durability);
        meta.getPersistentDataContainer().set(maxDurabilityKey, PersistentDataType.INTEGER, maxDurability);

        // Erstellen Sie eine Lore für das Item
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§7§m      §7« §bStats §7»§m      ");
        lore.add(" ");
        lore.add("§7Durability: §a" + durability + " §7/ §a" + maxDurability);
        lore.add(" ");
        lore.add("§7§m      §7« §dReqir §7»§7§m      ");
        lore.add(" ");
        lore.add("§7Level: §6" + level);
        lore.add("§7Rarity: " + "§aOrdinary");
        lore.add(" ");
        meta.setLore(lore);

        // Hinzufügen von weiteren Eigenschaften
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        // Anwenden des ItemMeta auf das ItemStack
        customChestplate.setItemMeta(meta);

        return customChestplate;
    }


    private int getMaxDurabilityForLevel(int level) {
        int baseDurability = 200;
        int increasePerLevel = 20;
        return baseDurability + (level - 1) * increasePerLevel;
    }

    public int getMaxDurabilityOfChestplate(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            int level = meta.getPersistentDataContainer().getOrDefault(itemLevelKey, PersistentDataType.INTEGER, 0);
            return getMaxDurabilityForLevel(level);
        }
        return 0;
    }

    public void updateChestplateLore(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            int durability = meta.getPersistentDataContainer().getOrDefault(durabilityKey, PersistentDataType.INTEGER, 0);
            int maxDurability = getMaxDurabilityForLevel(level);

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7§m      §7« §bStats §7»§m      ");
            lore.add(" ");
            lore.add("§7Durability: §a" + durability + " §7/ §a" + maxDurability);
            lore.add(" ");
            lore.add("§7§m      §7« §dReqir §7»§7§m      ");
            lore.add(" ");
            lore.add("§7Level: §6" + level);
            lore.add("§7Rarity: " + "§aOrdinary");
            lore.add(" ");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    @EventHandler
    public void handle(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return; // Prüfen, ob das beschädigte Entity ein Spieler ist.
        ItemStack chestplate = player.getInventory().getChestplate(); // Brustpanzer des Spielers erhalten.

        // Überprüfen, ob der Brustpanzer ein Meta-Item ist und den Durability-Schlüssel hat.
        if (chestplate == null || !chestplate.hasItemMeta()) return;

        ItemMeta meta = chestplate.getItemMeta();
        if (!meta.getPersistentDataContainer().has(durabilityKey, PersistentDataType.INTEGER)) return;

        int durability = meta.getPersistentDataContainer().get(durabilityKey, PersistentDataType.INTEGER);

        durability -= 1;
        meta.getPersistentDataContainer().set(durabilityKey, PersistentDataType.INTEGER, durability);

        // Lesen Sie den maxDurability Wert aus der PersistentDataContainer
        int maxDurability = getMaxDurabilityOfChestplate(chestplate);

        // Aktualisiere die Lore
        List<String> lore = meta.getLore();
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith("§7Durability: §a")) {
                lore.set(i, "§7Durability: §a" + durability + " §7/ §a" + maxDurability);
                break;
            }
        }
        meta.setLore(lore);
        chestplate.setItemMeta(meta);

        if (durability <= 0) {
            player.getInventory().setChestplate(null); // Brustpanzer des Spielers entfernen, wenn die Haltbarkeit 0 erreicht.
        }
    }
}

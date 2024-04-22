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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

public class CustomAxeManager implements Listener {
    private final Aetheria plugin;
    public final NamespacedKey itemLevelKey;
    public final NamespacedKey durabilityKey;
    public final NamespacedKey maxDurabilityKey;
    public final NamespacedKey efficiencyKey;

    public CustomAxeManager(Aetheria plugin) {
        this.plugin = plugin;
        this.itemLevelKey = new NamespacedKey(plugin, "item_level");
        this.durabilityKey = new NamespacedKey(plugin, "durability");
        this.maxDurabilityKey = new NamespacedKey(plugin, "max_durability");
        this.efficiencyKey = new NamespacedKey(plugin, "efficiency");
    }

    public ItemStack getCustomAxe(int level) {
        return createCustomAxe(level);
    }

    private int getEfficiencyForLevel(int level) {
        int efficiency = level / 10;
        return Math.min(efficiency, 10);
    }

    public void increaseItemLevel(ItemStack item, int levelToAdd) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            int currentLevel = meta.getPersistentDataContainer().getOrDefault(itemLevelKey, PersistentDataType.INTEGER, 0);
            int newLevel = currentLevel + levelToAdd;

            meta.getPersistentDataContainer().set(itemLevelKey, PersistentDataType.INTEGER, newLevel);

            // Effizienz basierend auf dem neuen Level aktualisieren
            int newEfficiency = getEfficiencyForLevel(newLevel);
            meta.getPersistentDataContainer().set(efficiencyKey, PersistentDataType.INTEGER, newEfficiency);
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, newEfficiency);

            item.setItemMeta(meta);
            updateAxeLore(item, newLevel);
        }
    }


    public ItemStack createCustomAxe(int level) {
        ItemStack customAxe = new ItemStack(Material.NETHERITE_AXE, 1);
        ItemMeta meta = customAxe.getItemMeta();

        meta.setDisplayName("§7« §fNormal Axe §7»");

        meta.getPersistentDataContainer().set(itemLevelKey, PersistentDataType.INTEGER, level);
        int durability = 200;
        int maxDurability = getMaxDurabilityForLevel(level);
        int efficiency = getEfficiencyForLevel(level);
        meta.getPersistentDataContainer().set(efficiencyKey, PersistentDataType.INTEGER, efficiency);
        customAxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, efficiency); // Das kann geändert werden, falls eine andere Verzauberung für Äxte verwendet werden soll.
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.getPersistentDataContainer().set(durabilityKey, PersistentDataType.INTEGER, durability);
        meta.getPersistentDataContainer().set(maxDurabilityKey, PersistentDataType.INTEGER, maxDurability);

        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§7§m      §7« §bStats §7»§m      ");
        lore.add(" ");
        lore.add("§7Durability: §a" + durability + " §7/ §a" + maxDurability);
        lore.add("§7Efficiency: §b" + efficiency);
        lore.add(" ");
        lore.add("§7§m      §7« §dReqir §7»§7§m      ");
        lore.add(" ");
        lore.add("§7Level: §6" + level);
        lore.add("§7Rarity: " + "§aOrdinary");
        lore.add(" ");
        meta.setLore(lore);

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        customAxe.setItemMeta(meta);

        return customAxe;
    }

    private int getMaxDurabilityForLevel(int level) {
        int baseDurability = 200;
        int increasePerLevel = 20;
        return baseDurability + (level - 1) * increasePerLevel;
    }

    public int getMaxDurabilityOfAxe(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            int level = meta.getPersistentDataContainer().getOrDefault(itemLevelKey, PersistentDataType.INTEGER, 0);
            return getMaxDurabilityForLevel(level);
        }
        return 0;
    }

    public void updateAxeLore(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            int durability = meta.getPersistentDataContainer().getOrDefault(durabilityKey, PersistentDataType.INTEGER, 0);
            int maxDurability = getMaxDurabilityForLevel(level);
            int efficiency = meta.getPersistentDataContainer().getOrDefault(efficiencyKey, PersistentDataType.INTEGER, 0);

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7§m      §7« §bStats §7»§m      ");
            lore.add(" ");
            lore.add("§7Durability: §a" + durability + " §7/ §a" + maxDurability);
            lore.add("§7Efficiency: §b" + efficiency);
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
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack axe = player.getInventory().getItemInMainHand();

        if (axe.getType() != Material.NETHERITE_AXE) return;
        if (!axe.hasItemMeta() || !axe.getItemMeta().getDisplayName().equals("§7« §fNormal Axe §7»")) return;

        ItemMeta meta = axe.getItemMeta();
        int efficiency = meta.getPersistentDataContainer().getOrDefault(efficiencyKey, PersistentDataType.INTEGER, 0);

        // Überprüfen, ob das Effizienz-Verzauberungslevel korrekt ist, und wenn nicht, korrigieren Sie es.
        if (axe.getEnchantmentLevel(Enchantment.DIG_SPEED) != efficiency) {
            axe.addUnsafeEnchantment(Enchantment.DIG_SPEED, efficiency);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack axe = player.getInventory().getItemInMainHand();
        ItemMeta meta = axe.getItemMeta();

        if (axe.getType() != Material.NETHERITE_AXE) return;

        // Überprüfen Sie, ob es Ihre benutzerdefinierte Axt ist (zum Beispiel durch Überprüfen des DisplayNamens).
        if (!axe.hasItemMeta() || !axe.getItemMeta().getDisplayName().equals("§7« §fNormal Axe §7»")) return;

        if (!meta.getPersistentDataContainer().has(durabilityKey, PersistentDataType.INTEGER)) {
            player.sendMessage("§cDie Axt hat keinen Haltbarkeits-Tag!"); // Debug-Ausgabe
            return;
        }

        int durability = meta.getPersistentDataContainer().get(durabilityKey, PersistentDataType.INTEGER);
        durability -= 1;
        meta.getPersistentDataContainer().set(durabilityKey, PersistentDataType.INTEGER, durability);

        int maxDurability = getMaxDurabilityOfAxe(axe);

        List<String> lore = meta.getLore();
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith("§7Durability: §a")) {
                lore.set(i, "§7Durability: §a" + durability + " §7/ §a" + maxDurability);
                break;
            }
        }
        meta.setLore(lore);
        axe.setItemMeta(meta);

        if (durability <= 0) {
            player.getInventory().setItemInMainHand(null); // Axt des Spielers entfernen, wenn die Haltbarkeit 0 erreicht.
        }
    }

}

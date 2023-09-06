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

public class CustomPickaxeManager implements Listener {
    private final Aetheria plugin;
    public final NamespacedKey itemLevelKey;
    public final NamespacedKey durabilityKey;
    public final NamespacedKey maxDurabilityKey;
    public final NamespacedKey efficiencyKey;

    public CustomPickaxeManager(Aetheria plugin) {
        this.plugin = plugin;
        this.itemLevelKey = new NamespacedKey(plugin, "item_level");
        this.durabilityKey = new NamespacedKey(plugin, "durability");
        this.maxDurabilityKey = new NamespacedKey(plugin, "max_durability");
        this.efficiencyKey = new NamespacedKey(plugin, "efficiency");
    }

    public ItemStack getCustomPickaxe(int level) {
        return createCustomPickaxe(level);
    }

    private int getEfficiencyForLevel(int level) {
        int efficiency = level / 10;  // erhöht sich alle 10 Level um 1
        return Math.min(efficiency, 10);  // stellt sicher, dass die Effizienz nicht über 10 steigt
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
            updatePickaxeLore(item, newLevel);
        }
    }


    public ItemStack createCustomPickaxe(int level) {
        ItemStack customPickaxe = new ItemStack(Material.NETHERITE_PICKAXE, 1);
        ItemMeta meta = customPickaxe.getItemMeta();

        meta.setDisplayName("§7« §fNormal Pickaxe §7»");

        meta.getPersistentDataContainer().set(itemLevelKey, PersistentDataType.INTEGER, level);
        int durability = 200;
        int maxDurability = getMaxDurabilityForLevel(level);
        int efficiency = getEfficiencyForLevel(level);
        meta.getPersistentDataContainer().set(efficiencyKey, PersistentDataType.INTEGER, efficiency);
        customPickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, efficiency);
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

        customPickaxe.setItemMeta(meta);

        return customPickaxe;
    }

    private int getMaxDurabilityForLevel(int level) {
        int baseDurability = 200;
        int increasePerLevel = 20;
        return baseDurability + (level - 1) * increasePerLevel;
    }

    public int getMaxDurabilityOfPickaxe(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            int level = meta.getPersistentDataContainer().getOrDefault(itemLevelKey, PersistentDataType.INTEGER, 0);
            return getMaxDurabilityForLevel(level);
        }
        return 0;
    }

    public void updatePickaxeLore(ItemStack item, int level) {
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
        ItemStack pickaxe = player.getInventory().getItemInMainHand();

        if (pickaxe.getType() != Material.NETHERITE_PICKAXE) return;
        if (!pickaxe.hasItemMeta() || !pickaxe.getItemMeta().getDisplayName().equals("§7« §fNormal Pickaxe §7»")) return;

        ItemMeta meta = pickaxe.getItemMeta();
        int efficiency = meta.getPersistentDataContainer().getOrDefault(efficiencyKey, PersistentDataType.INTEGER, 0);

        // Überprüfen, ob das Effizienz-Verzauberungslevel korrekt ist, und wenn nicht, korrigieren Sie es.
        if (pickaxe.getEnchantmentLevel(Enchantment.DIG_SPEED) != efficiency) {
            pickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, efficiency);
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack pickaxe = player.getInventory().getItemInMainHand();
        ItemMeta meta = pickaxe.getItemMeta();

        int durability = meta.getPersistentDataContainer().get(durabilityKey, PersistentDataType.INTEGER);

        if (durability <= 0) {
            event.setCancelled(true);
            return;
        }

        // Überprüfen Sie, ob der Spieler überhaupt eine Spitzhacke in der Hand hat.
        if (pickaxe.getType() != Material.NETHERITE_PICKAXE) return;

        // Überprüfen Sie, ob es Ihre benutzerdefinierte Spitzhacke ist (zum Beispiel durch Überprüfen des DisplayNamens).
        if (!pickaxe.hasItemMeta() || !pickaxe.getItemMeta().getDisplayName().equals("§7« §fNormal Pickaxe §7»")) return;

        meta = pickaxe.getItemMeta();
        if (!meta.getPersistentDataContainer().has(durabilityKey, PersistentDataType.INTEGER)) {
            player.sendMessage("§cDie Spitzhacke hat keinen Haltbarkeits-Tag!"); // Debug-Ausgabe
            return;
        }

        durability = meta.getPersistentDataContainer().get(durabilityKey, PersistentDataType.INTEGER);

        durability -= 1;
        meta.getPersistentDataContainer().set(durabilityKey, PersistentDataType.INTEGER, durability);

        int maxDurability = getMaxDurabilityOfPickaxe(pickaxe);

        List<String> lore = meta.getLore();
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith("§7Durability: §a")) {
                lore.set(i, "§7Durability: §a" + durability + " §7/ §a" + maxDurability);
                break;
            }
        }
        meta.setLore(lore);
        pickaxe.setItemMeta(meta);

        if (durability <= 0) {
            player.getInventory().setItemInMainHand(null); // Spitzhacke des Spielers entfernen, wenn die Haltbarkeit 0 erreicht.
        }
    }
}

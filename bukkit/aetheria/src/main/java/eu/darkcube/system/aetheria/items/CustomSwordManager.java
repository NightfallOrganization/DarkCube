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

public class CustomSwordManager implements Listener {
    public final NamespacedKey itemLevelKey;
    public final NamespacedKey durabilityKey;
    public final NamespacedKey maxDurabilityKey;
    public final NamespacedKey attackDamageKey;
    private final Aetheria plugin;

    public CustomSwordManager(Aetheria plugin) {
        this.plugin = plugin;
        this.itemLevelKey = new NamespacedKey(plugin, "item_level");
        this.durabilityKey = new NamespacedKey(plugin, "durability");
        this.maxDurabilityKey = new NamespacedKey(plugin, "max_durability");
        this.attackDamageKey = new NamespacedKey(plugin, "attack_damage");
    }

    public ItemStack getCustomSword(int level) {
        return createCustomSword(level);
    }

    public void increaseItemLevel(ItemStack item, int levelToAdd) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            int currentLevel = meta.getPersistentDataContainer().getOrDefault(itemLevelKey, PersistentDataType.INTEGER, 0);
            int newLevel = currentLevel + levelToAdd;

            meta.getPersistentDataContainer().set(itemLevelKey, PersistentDataType.INTEGER, newLevel);
            item.setItemMeta(meta);
            updateSwordLore(item, newLevel);
        }
    }

    public void setSwordAttackDamage(ItemStack item, int damage) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(attackDamageKey, PersistentDataType.INTEGER, damage);
            item.setItemMeta(meta);
        }
    }

    public int getSwordAttackDamage(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.getPersistentDataContainer().getOrDefault(attackDamageKey, PersistentDataType.INTEGER, 0);
        }
        return 0;
    }

    public ItemStack createCustomSword(int level) {
        ItemStack customSword = new ItemStack(Material.NETHERITE_SWORD, 1);
        ItemMeta meta = customSword.getItemMeta();

        meta.setDisplayName("§7« §fNormal Sword §7»");
        meta.getPersistentDataContainer().set(itemLevelKey, PersistentDataType.INTEGER, level);
        int durability = 200;
        int maxDurability = getMaxDurabilityForLevel(level);
        meta.getPersistentDataContainer().set(durabilityKey, PersistentDataType.INTEGER, durability);
        meta.getPersistentDataContainer().set(maxDurabilityKey, PersistentDataType.INTEGER, maxDurability);
        int damage = level * 2;  // Berechne den Schaden hier
        meta.getPersistentDataContainer().set(attackDamageKey, PersistentDataType.INTEGER, damage);  // Setze den Schaden hier
        meta.setUnbreakable(true);
        meta.setCustomModelData(3);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        customSword.setItemMeta(meta);

        // Setze die Lore mit dem Schaden
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("§7§m      §7« §bStats §7»§m      ");
        lore.add(" ");
        lore.add("§7Durability: §a" + durability + " §7/ §a" + maxDurability);
        lore.add("§7Damage: §c" + damage * 2);  // Zeige den Schaden
        lore.add(" ");
        lore.add("§7§m      §7« §dReqir §7»§7§m      ");
        lore.add(" ");
        lore.add("§7Level: §6" + level);
        lore.add("§7Rarity: " + "§aOrdinary");
        lore.add(" ");
        meta.setLore(lore);
        customSword.setItemMeta(meta);

        return customSword;
    }


    private int getMaxDurabilityForLevel(int level) {
        int baseDurability = 200;
        int increasePerLevel = 20;
        return baseDurability + (level - 1) * increasePerLevel;
    }

    public int getMaxDurabilityOfSword(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            int level = meta.getPersistentDataContainer().getOrDefault(itemLevelKey, PersistentDataType.INTEGER, 0);
            return getMaxDurabilityForLevel(level);
        }
        return 0;
    }

    public void updateSwordLore(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            int durability = meta.getPersistentDataContainer().getOrDefault(durabilityKey, PersistentDataType.INTEGER, 0);
            int maxDurability = getMaxDurabilityForLevel(level);
            int damage = getSwordAttackDamage(item); // Hol dir den Schaden

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§7§m      §7« §bStats §7»§m      ");
            lore.add(" ");
            lore.add("§7Durability: §a" + durability + " §7/ §a" + maxDurability);
            lore.add("§7Damage: §c" + damage * 2);  // Zeige den Schaden
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


    @EventHandler public void handle(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(durabilityKey, PersistentDataType.INTEGER)) return;

        int durability = meta.getPersistentDataContainer().get(durabilityKey, PersistentDataType.INTEGER);

        durability -= 1;
        meta.getPersistentDataContainer().set(durabilityKey, PersistentDataType.INTEGER, durability);

        // Lesen Sie den maxDurability Wert aus der PersistentDataContainer
        int maxDurability = getMaxDurabilityOfSword(item);

        // Aktualisiere die Lore
        List<String> lore = meta.getLore();
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).startsWith("§7Durability: §a")) {
                lore.set(i, "§7Durability: §a" + durability + " §7/ §a" + maxDurability);
                break;
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        if (durability <= 0) {
            player.getInventory().removeItem(item);
        }
    }
}

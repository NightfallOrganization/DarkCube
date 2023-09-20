/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GameItemManager implements Listener {

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getName().equalsIgnoreCase("world")) {
            setGameInventory(player);
        }
    }

    private void setGameInventory(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, createItem(Material.BOW, 1, "&aBogen",
                new String[]{"&7Schieße deinen Gegner in den Tod!"},
                new Enchantment[]{Enchantment.ARROW_KNOCKBACK, Enchantment.ARROW_INFINITE, Enchantment.KNOCKBACK},
                new int[]{2, 1, 5}));

        player.getInventory().setItem(1, createItem(Material.SHEARS, 1, "&aSchere",
                new String[]{"&7Wer baut am schnellsten Wolle ab?"},
                new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.KNOCKBACK},
                new int[]{5, 5}));

        player.getInventory().setItem(2, createItem(Material.STAINED_GLASS, 1, "&aRettungskapsel",
                new String[]{"&7Rette dich in einer Kapsel aus Wolle!"},
                new Enchantment[]{Enchantment.DURABILITY},
                new int[]{1}, true));

        player.getInventory().setItem(3, createItem(Material.SNOW_BALL, 1, "&aTauscher",
                new String[]{"&7Tausche den Platz mit deinem Gegner!"},
                new Enchantment[]{Enchantment.DURABILITY},
                new int[]{1}, true));

        player.getInventory().setItem(4, createItem(Material.ENDER_PEARL, 1, "&aEnderperle",
                new String[]{"&7Teleportiere dich hinter deinem Gegner!"},
                new Enchantment[]{Enchantment.DURABILITY},
                new int[]{1}, true));

        player.getInventory().setItem(8, createItem(Material.RABBIT_FOOT, 1, "&aLongjump",
                new String[]{"&7WEITER SPRINGEN WIE EIN KARNICKEL"},
                new Enchantment[]{Enchantment.DURABILITY},
                new int[]{1}, true));

        player.getInventory().setItem(17, createItem(Material.ARROW, 1, "&aPfeil",
                new String[]{"&7Ich bin ein Pfeil"},
                new Enchantment[]{Enchantment.DURABILITY},
                new int[]{1}, true));

    }

    private ItemStack createItem(Material material, int amount, String displayName, String[] lore, Enchantment[] enchantments, int[] levels) {
        return createItem(material, amount, displayName, lore, enchantments, levels, false);
    }

    public ItemStack createItem(Material material, int amount, String displayName, String[] lore, Enchantment[] enchantments, int[] levels, boolean hideEnchants) {
        short data = 0;

        if (material == Material.STAINED_GLASS) {
            data = 14;  // Für rotes Glas
        }

        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        List<String> loreList = new ArrayList<>();
        for (String s : lore) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(loreList);

        if (enchantments != null) {
            for (int i = 0; i < enchantments.length; i++) {
                meta.addEnchant(enchantments[i], levels[i], true);
            }
        }

        if (hideEnchants) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        // Setzt den Bogen und die Schere als unzerstörbar und versteckt dieses Merkmal
        if (material == Material.BOW || material == Material.SHEARS) {
            meta.spigot().setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        item.setItemMeta(meta);
        return item;
    }


}

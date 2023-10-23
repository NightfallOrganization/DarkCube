/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.game;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Team;

public class ArmorManager {

    public void setArmor(Player player, Team team) {
        if (team == null) {
            clearArmor(player);
            return;
        }

        Color armorColor;
        ChatColor chatColor;
        switch (team.getName()) {
            case "Black":
                armorColor = Color.fromRGB(30, 30, 30);
                chatColor = ChatColor.DARK_GRAY;
                break;
            case "White":
                armorColor = Color.WHITE;
                chatColor = ChatColor.WHITE;
                break;
            default:
                armorColor = Color.GRAY;
                chatColor = ChatColor.GRAY;
                break;
        }

        player.getInventory().setHelmet(createArmorItem(Material.LEATHER_HELMET, armorColor, chatColor + "Kopfbedeckung <3"));
        player.getInventory().setChestplate(createArmorItem(Material.LEATHER_CHESTPLATE, armorColor, chatColor + "Harnisch"));
        player.getInventory().setLeggings(createArmorItem(Material.LEATHER_LEGGINGS, armorColor, chatColor + "Beinkleid"));
        player.getInventory().setBoots(createArmorItem(Material.LEATHER_BOOTS, armorColor, chatColor + "Sporenträger"));
    }

    private ItemStack createArmorItem(Material type, Color color, String name) {
        ItemStack item = new ItemStack(type);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public void clearArmor(Player player) {
        player.getInventory().setArmorContents(null);
    }
}
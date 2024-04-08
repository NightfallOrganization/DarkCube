/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.manager;

import eu.darkcube.system.sumo.items.game.ItemStick;
import eu.darkcube.system.sumo.items.game.ItemWool;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class ItemManager {

    private static ItemStack createItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> metaLore = new ArrayList<String>();
        metaLore.add(lore);
        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }

    public static void setStartingItems(Player player) {
//        player.getInventory().setItem(0, createItem(Material.BREWING_STAND_ITEM, "§dFähigkeiten", "§7Wähle deine Macht!"));
//        player.getInventory().setItem(1, createItem(Material.BOOK, "§9Teams", "§7Wähle dein Team!"));
//        player.getInventory().setItem(4, createItem(Material.BLAZE_ROD, "§6Partikel §aAn §8╏ §7Ausschalten?", "§7Klicke um Partikel zu deaktivieren!"));
//        player.getInventory().setItem(7, createItem(Material.REDSTONE_COMPARATOR, "§cEinstellungen", "§7Stelle alles nach belieben ein!"));
//        player.getInventory().setItem(8, createItem(Material.PAPER, "§6Voting", "§7Stimme für Spielszenarien ab!"));

        player.getInventory().setItem(3, createItem(Material.BOOK, "§9Teams", "§7Wähle dein Team!"));
        player.getInventory().setItem(5, createItem(Material.PAPER, "§6Voting", "§7Stimme für Spielszenarien ab!"));
    }

    public static void setPlayingItems(Player player) {
        player.getInventory().setItem(1, ItemStick.createStick());
        player.getInventory().setItem(0, ItemWool.createWool(player));
    }


    public static void setEndingItems(Player player) {

    }
}

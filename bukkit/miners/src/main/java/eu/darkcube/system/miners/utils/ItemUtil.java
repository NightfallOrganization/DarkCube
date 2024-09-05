/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.utils;

import static eu.darkcube.system.miners.enums.InventoryItems.*;

import eu.darkcube.system.miners.enums.InventoryItems;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.entity.Player;

public class ItemUtil {

    public static void setLobbyPhaseItems(Player player) {
        setItem(HOTBAR_ITEM_ABILITIES, player, 0);
        setItem(HOTBAR_ITEM_TEAMS, player, 1);
        setItem(HOTBAR_ITEM_SETTINGS, player, 4);
        setItem(HOTBAR_ITEM_SHOP, player, 7);
        setItem(HOTBAR_ITEM_VOTING, player, 8);
        System.out.println("setLobbyPhaseItems");
    }

    public static void setMiningPhaseItems(Player player) {

    }

    public static void setEndPhaseItems(Player player) {

    }

    private static void setItem(InventoryItems item, Player player, int slot) {
        User user = UserAPI.instance().user(player.getUniqueId());
        player.getInventory().setItem(slot, item.getItem(user).build());
    }
}

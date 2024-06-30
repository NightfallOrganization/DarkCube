/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.wool;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WoolReducer {

    private static final Material WOOL = Material.WOOL;

    public static boolean hasEnoughWool(Player player, int amount) {
        int totalWoolCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == WOOL) {
                totalWoolCount += item.getAmount();
            }
        }
        return totalWoolCount >= amount;
    }

    public static void removeWool(Player player, int amount) {
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item != null && item.getType() == WOOL) {
                int removeFromStack = Math.min(amount, item.getAmount());
                item.setAmount(item.getAmount() - removeFromStack);
                amount -= removeFromStack;

                // Prüfe, ob der ItemStack leer ist und entferne ihn gegebenenfalls
                if (item.getAmount() <= 0) {
                    player.getInventory().setItem(i, null);
                }

                if (amount <= 0) {
                    break;
                }
            }
        }
    }

}


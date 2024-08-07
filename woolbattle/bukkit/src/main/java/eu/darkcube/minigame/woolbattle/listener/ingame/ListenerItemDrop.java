/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerItemDrop implements Listener {
    @EventHandler
    public void handle(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getType() != Material.WOOL) {
            e.setCancelled(true);
        } else {
            ItemStack item = e.getItemDrop().getItemStack();
            int amount = item.getAmount();
            WBUser user = WBUser.getUser(e.getPlayer());
            amount = user.removeWool(amount, false); // update the wool count quietly
            item.setAmount(amount);
            e.getItemDrop().setItemStack(item);
            if (amount == 0) e.setCancelled(true);
        }
    }
}

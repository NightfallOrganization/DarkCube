/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerInventoryClick extends Listener<InventoryClickEvent> {
    private final WoolBattleBukkit woolbattle;

    public ListenerInventoryClick(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override @EventHandler public void handle(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        String itemId = ItemManager.getItemId(item);
        if (itemId == null) {
            return;
        }
        if (woolbattle.lobby().enabled() && e.getHotbarButton() != -1) {
            e.setCancelled(true);
        }

        if (e.isCancelled()) {
            return;
        }
        if (e.getRawSlot() != -1 && e.getRawSlot() != -999) {
            EventInteract pe = new EventInteract(p, e.getCurrentItem(), e.getClickedInventory(), e.getClick());
            Bukkit.getPluginManager().callEvent(pe);
            e.setCancelled(pe.isCancelled());
            e.setCurrentItem(pe.getItem());
        } else {
            e.setCancelled(true);
        }
    }
}

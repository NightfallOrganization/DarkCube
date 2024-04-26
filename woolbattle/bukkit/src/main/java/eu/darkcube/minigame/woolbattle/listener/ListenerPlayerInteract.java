/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener;

import eu.darkcube.minigame.woolbattle.event.EventInteract;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;

public class ListenerPlayerInteract extends Listener<PlayerInteractEvent> {

    @Override
    @EventHandler
    public void handle(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem() != null) {
            EventInteract pe = new EventInteract(e.getPlayer(), e.getItem(), e.getPlayer().getInventory(), ClickType.RIGHT, true);
            Bukkit.getPluginManager().callEvent(pe);
            e.setCancelled(pe.isCancelled());
            if (!pe.getItem().equals(e.getPlayer().getItemInHand())) e.getPlayer().setItemInHand(pe.getItem());
        }
    }
}

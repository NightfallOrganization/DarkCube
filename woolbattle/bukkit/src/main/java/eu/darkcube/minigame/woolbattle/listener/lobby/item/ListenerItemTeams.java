/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.lobby.item;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.EventInteract;
import eu.darkcube.minigame.woolbattle.inventory.TeamsInventory;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class ListenerItemTeams extends Listener<EventInteract> {
    private final WoolBattle woolbattle;

    public ListenerItemTeams(WoolBattle woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    @EventHandler
    public void handle(EventInteract e) {
        ItemStack item = e.getItem();
        if (item != null) {
            if (item.hasItemMeta()) {
                if (ItemManager.getItemId(item).equals(ItemManager.getItemId(Item.LOBBY_TEAMS))) {
                    e.setCancelled(true);
                    e.getUser().setOpenInventory(new TeamsInventory(woolbattle, e.getUser()));
                }
            }
        }
    }
}

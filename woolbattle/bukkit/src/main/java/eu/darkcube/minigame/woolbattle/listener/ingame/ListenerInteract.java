/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.inventory.CompassTeleportInventory;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class ListenerInteract extends Listener<PlayerInteractEvent> {
    private final WoolBattleBukkit woolbattle;

    public ListenerInteract(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    @EventHandler
    public void handle(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        WBUser user = WBUser.getUser(p);
        if (!user.getTeam().isSpectator()) {
            return;
        }
        e.setCancelled(true);
        if (e.getItem() != null && e.getItem().getType() != Material.AIR && ItemManager.getItemId(e.getItem()).equals(Item.TELEPORT_COMPASS.getItemId())) {
            user.setOpenInventory(new CompassTeleportInventory(woolbattle, user));
        }
    }
}

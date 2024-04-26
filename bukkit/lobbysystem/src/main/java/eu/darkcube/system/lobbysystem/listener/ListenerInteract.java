/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.inventory.InventoryCompass;
import eu.darkcube.system.lobbysystem.inventory.InventoryGadget;
import eu.darkcube.system.lobbysystem.inventory.InventoryLobbySwitcher;
import eu.darkcube.system.lobbysystem.inventory.InventorySettings;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServerOwn;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class ListenerInteract extends BaseListener {

    private static final Collection<Material> DENIED = Arrays.asList(Material.FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.SPRUCE_FENCE_GATE, Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.DARK_OAK_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR, Material.WOOD_DOOR, Material.WOODEN_DOOR, Material.TRAP_DOOR, Material.STONE_BUTTON, Material.WOOD_BUTTON);

    @EventHandler public void handle(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        LobbyUser luser = UserWrapper.fromUser(UserAPI.instance().user(p.getUniqueId()));
        User user = luser.user();
        if (!luser.isBuildMode() && e.getAction() == Action.RIGHT_CLICK_BLOCK && DENIED.contains(e.getClickedBlock().getType())) {
            e.setCancelled(true);
        }
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = e.getItem();
        if (item == null) {
            return;
        }
        String itemid = Item.getItemId(item);
        if (itemid == null) {
            return;
        }
        if (itemid.equals(Item.INVENTORY_LOBBY_SWITCHER.getItemId())) {
            luser.setOpenInventory(new InventoryLobbySwitcher(user));
        } else if (itemid.equals(Item.INVENTORY_COMPASS.getItemId())) {
            luser.setOpenInventory(new InventoryCompass(user));
        } else if (itemid.equals(Item.INVENTORY_GADGET.getItemId())) {
            luser.setOpenInventory(new InventoryGadget(user));
        } else if (itemid.equals(Item.INVENTORY_SETTINGS.getItemId())) {
            luser.setOpenInventory(new InventorySettings(user));
        } else if (itemid.equals(Item.PSERVER_MAIN_ITEM.getItemId())) {
            luser.setOpenInventory(new InventoryPServerOwn(user));
        } else if (itemid.equals(Item.JUMPANDRUN_STOP.getItemId())) {
            luser.stopJaR();
        }
    }
}

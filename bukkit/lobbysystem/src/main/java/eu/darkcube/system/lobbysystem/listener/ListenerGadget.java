/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.gadget.Gadget;
import eu.darkcube.system.lobbysystem.inventory.InventoryGadget;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerGadget extends BaseListener {

    public static ListenerGadget instance;

    public ListenerGadget() {
        ListenerGadget.instance = this;
    }

    @EventHandler public void handle(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        LobbyUser user = UserWrapper.fromUser(UserAPI.instance().user(p.getUniqueId()));
        if (user.getOpenInventory().getType() != InventoryGadget.type_gadget) {
            return;
        }
        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        String itemid = Item.getItemId(item);
        if (itemid == null || itemid.isEmpty()) {
            return;
        }
        boolean b = true;
        if (itemid.equals(Item.GADGET_GRAPPLING_HOOK.getItemId())) {
            user.setGadget(Gadget.GRAPPLING_HOOK);
        } else if (itemid.equals(Item.GADGET_HOOK_ARROW.getItemId())) {
            user.setGadget(Gadget.HOOK_ARROW);
        } else {
            b = false;
        }
        if (b) {
            p.closeInventory();
        }
    }

}

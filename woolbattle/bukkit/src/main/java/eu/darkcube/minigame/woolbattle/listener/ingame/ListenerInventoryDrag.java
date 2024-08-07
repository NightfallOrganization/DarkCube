/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.listener.ingame.ListenerInventoryClick.Handle;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

public class ListenerInventoryDrag extends Listener<InventoryDragEvent> {
    @Override
    @EventHandler
    public void handle(InventoryDragEvent e) {
        if (e.getOldCursor() != null && e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            WBUser user = WBUser.getUser(p);
            ItemStack item = e.getOldCursor();
            boolean var1 = item != null && item.getType() != Material.AIR;
            int perkId = ListenerInventoryClick.perkId(item);

            if (e.getView().getType() == InventoryType.CRAFTING) {
                if (e.getRawSlots().size() != 1) {
                    e.setCancelled(true);
                    return;
                }
                int slot = e.getRawSlots().stream().findFirst().orElseThrow(RuntimeException::new);
                SlotType slotType = null;
                switch (e.getView().getType()) {
                    case PLAYER:
                    case CRAFTING:
                        if (slot < 5) {
                            slotType = SlotType.CRAFTING;
                        } else if (slot < 9) {
                            slotType = SlotType.ARMOR;
                        } else if (slot < 18) {
                            slotType = SlotType.QUICKBAR;
                        } else {
                            slotType = SlotType.CONTAINER;
                        }
                        break;
                    default:
                        break;
                }
                if (slotType == null) {
                    e.setCancelled(true);
                    p.sendMessage("§cInvalid Inventory: " + e.getView().getType() + ", " + slot);
                    return;
                }
                if (slotType == SlotType.ARMOR || slotType == SlotType.CRAFTING) {
                    e.setCancelled(true);
                    return;
                }

                Handle[] handles = new Handle[0];
                for (UserPerk perk : user.perks().perks()) {
                    handles = Arrays.addAfter(handles, new Handle(perk));
                }

                for (Handle handle : handles) {
                    int id = handle.perk().id();

                    if (var1) {
                        if (id == perkId) {
                            handle.invoke(slot);
                            e.setCursor(null);
                        }
                    }
                }

            } else {
                if (!user.isTrollMode() && p.getGameMode() == GameMode.SURVIVAL) {
                    e.setCancelled(true);
                }
            }
        }
    }
}

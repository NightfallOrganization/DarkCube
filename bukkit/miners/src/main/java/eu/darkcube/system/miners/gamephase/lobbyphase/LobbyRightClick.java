/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.lobbyphase;

import static eu.darkcube.system.miners.enums.Sounds.NO;

import eu.darkcube.system.miners.Miners;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class LobbyRightClick implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getItem() == null) return;

        if (event.getMaterial() == Material.OMINOUS_BOTTLE) {
            Miners.getInstance().getOwnAbilitiesInventory().openInventory(player);
        } else if (event.getMaterial() == Material.BOOK) {
            Miners.getInstance().getTeamInventory().openInventory(player);
        } else if (event.getMaterial() == Material.COMPARATOR) {
            NO.playSound(player);
        } else if (event.getMaterial() == Material.ENDER_CHEST) {
            NO.playSound(player);
        } else if (event.getMaterial() == Material.PAPER) {
            NO.playSound(player);
        }

    }

    // @EventHandler
    // public void onPlayerInteract(PlayerInteractEvent event) {
    //     Player player = event.getPlayer();
    //     User user = UserAPI.instance().user(player.getUniqueId());
    //     if (event.getHand() != EquipmentSlot.HAND) return;
    //     if (event.getItem() == null) return;
    //     var item = event.getItem();
    //
    //     var itemBuilder = ItemBuilder.item(item);
    //     var clickedItem = InventoryItems.getItemID(itemBuilder);
    //
    //     if (clickedItem.equals(HOTBAR_ITEM_ABILITIES.itemID())) {
    //         player.sendMessage("§7Test");
    //     }
    // }

}

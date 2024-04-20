/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerItemPickup extends Listener<PlayerPickupItemEvent> {
    @Override
    @EventHandler
    public void handle(PlayerPickupItemEvent e) {
        if (e.getItem().getItemStack().getType() != Material.WOOL) {
            return;
        }
        Player p = e.getPlayer();
        WBUser user = WBUser.getUser(p);
        if (user.getTeam().isSpectator()) {
            e.setCancelled(true);
            return;
        }
        Item entity = e.getItem();
        ItemStack item = entity.getItemStack();
        int tryadd = item.getAmount();
        int added = user.addWool(tryadd);
        if (added != 0) {
            playSound(user.getBukkitEntity());
        }
        int missed = tryadd - added;
        if (missed > 0) {
            item.setAmount(missed);
            entity.setItemStack(item);
            entity.setPickupDelay(4);
        } else {
            e.getItem().remove();
        }
        e.setCancelled(true);
    }

    private void playSound(Player p) {
        p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
    }
}

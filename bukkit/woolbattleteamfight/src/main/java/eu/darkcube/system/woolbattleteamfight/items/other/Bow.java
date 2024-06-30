/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.items.other;

import eu.darkcube.system.woolbattleteamfight.wool.WoolReducer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class Bow implements Listener {

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!WoolReducer.hasEnoughWool(player, 1)) {
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
            event.setCancelled(true);
            return;
        }

        WoolReducer.removeWool(player, 1);
    }
}

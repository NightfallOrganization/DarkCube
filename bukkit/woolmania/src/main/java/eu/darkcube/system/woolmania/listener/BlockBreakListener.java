/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import static eu.darkcube.system.woolmania.enums.Hallpools.HALLPOOL1;
import static eu.darkcube.system.woolmania.enums.Locations.isWithinBoundLocations;
import static eu.darkcube.system.woolmania.manager.WorldManager.HALLS;

import eu.darkcube.system.woolmania.WoolMania;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location location = block.getLocation();

        if (!isWithinBoundLocations(location, new Location(HALLS, HALLPOOL1.getX1(), HALLPOOL1.getY1(), HALLPOOL1.getZ1()), new Location(HALLS, HALLPOOL1.getX2(), HALLPOOL1.getY2(), HALLPOOL1.getZ2()))) {
            return;
        }

        if (block.getType().toString().endsWith("_WOOL")) {
            event.setDropItems(false);
            ItemStack woolItem = new ItemStack(block.getType());
            WoolMania.getInstance().getLevelXPHandler().manageLevelXP(player);

            if (player.getInventory().addItem(woolItem).isEmpty()) {
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 2.0f);
            } else {
                block.getWorld().dropItemNaturally(block.getLocation(), woolItem);
            }
        }
    }

}

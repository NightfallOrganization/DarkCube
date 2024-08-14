/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Hall;
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Light;
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
        WoolManiaPlayer p = WoolMania.getStaticPlayer(player);
        Location location = block.getLocation();
        Hall hall = p.getHall();

        if (hall == null || !hall.getPool().isWithinBounds(location)) {
            return;
        }

        if (block.getType().toString().endsWith("_WOOL")) {
            event.setDropItems(false);
            WoolMania.getInstance().getLevelXPHandler().manageLevelXP(player);
            WoolMania.getStaticPlayer(player).addFarmedBlocks(1, player);
            ItemStack woolItem = new ItemStack(block.getType());
            CustomItem customItem = new CustomItem(woolItem);

            dropBlocks(player, block, customItem, hall, "§7« §fWool §7»");

            Light l = (Light) Material.LIGHT.createBlockData();
            l.setLevel(12);
            block.setBlockData(l);
        }
    }

    public void dropBlocks(Player player, Block block, CustomItem customItem, Hall hall, String name) {
        customItem.setDisplayName(name);
        customItem.setTier(hall.getTier());
        customItem.setDurability(-2);
        customItem.updateItemLore();

        if (player.getInventory().addItem(customItem.getItemStack()).isEmpty()) {
            WoolMania.getStaticPlayer(player).getSound().playSound(player);
        } else {
            block.getWorld().dropItemNaturally(block.getLocation(), customItem.getItemStack());
        }
    }

}

/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import static eu.darkcube.system.woolmania.enums.Sounds.NO;
import static eu.darkcube.system.woolmania.util.message.Message.LEVEL_TO_LOW;
import static eu.darkcube.system.woolmania.util.message.Message.NOT_ENOUGHT_DURABILITY;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.hall.Hall;
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.items.WoolItem;
import eu.darkcube.system.woolmania.registry.WoolRegistry;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import eu.darkcube.system.woolmania.util.message.CustomItemNames;
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
        User user = UserAPI.instance().user(player.getUniqueId());
        Location location = block.getLocation();
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        Hall hall = woolManiaPlayer.getHall();

        if (hall == null || !hall.getPool().isWithinBounds(location)) {
            return;
        }

        handleBlockBreakItem(player, user, event, woolManiaPlayer);
        handleWool(block, user, player, event);
    }

    public void handleBlockBreakItem(Player player, User user, BlockBreakEvent event, WoolManiaPlayer woolManiaPlayer) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemBuilder item = ItemBuilder.item(itemInHand);
        CustomItem customItem = new CustomItem(item);
        int itemLevel = customItem.getLevel();
        int playerLevel = woolManiaPlayer.getLevel();

        if (itemLevel > playerLevel) {
            user.sendMessage(LEVEL_TO_LOW);
            NO.playSound(player);
            event.setCancelled(true);
            return;
        }

        if (customItem.getDurability() == 0) {
            user.sendMessage(NOT_ENOUGHT_DURABILITY);
            NO.playSound(player);
            event.setCancelled(true);
            return;
        }

        if (customItem.hasItemID()) {
            customItem.reduceDurability();
            player.getInventory().setItemInMainHand(customItem.getItemStack());
        }
    }

    public void handleWool(Block block, User user, Player player, BlockBreakEvent event) {
        WoolRegistry registry = WoolMania.getInstance().getWoolRegistry();
        Material material = block.getType();

        if (event.isCancelled()) return;
        
        if (registry.contains(material)) {
            WoolRegistry.Entry entry = registry.get(material);

            event.setDropItems(false);
            WoolMania.getInstance().getLevelXPHandler().manageLevelXP(player);
            WoolMania.getStaticPlayer(player).addFarmedBlocks(1, player);

            CustomItemNames woolName = entry.name();

            WoolItem woolItem = new WoolItem(user, block.getType(), entry.tier(), woolName);
            dropBlocks(player, block, woolItem);

            Light l = (Light) Material.LIGHT.createBlockData();
            l.setLevel(12);
            block.setBlockData(l);
        }
    }

    public void dropBlocks(Player player, Block block, CustomItem customItem) {
        if (player.getInventory().addItem(customItem.getItemStack()).isEmpty()) {
            WoolMania.getStaticPlayer(player).getFarmingSound().playSound(player);
        } else {
            block.getWorld().dropItemNaturally(block.getLocation(), customItem.getItemStack());
        }
    }

}

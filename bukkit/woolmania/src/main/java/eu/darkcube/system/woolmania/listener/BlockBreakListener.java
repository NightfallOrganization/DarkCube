/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import static eu.darkcube.system.woolmania.enums.Sounds.*;
import static eu.darkcube.system.woolmania.util.message.Message.*;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.hall.Halls;
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.items.WoolItem;
import eu.darkcube.system.woolmania.registry.WoolRegistry;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        User user = UserAPI.instance().user(player.getUniqueId());
        Location location = block.getLocation();
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        Halls hall = woolManiaPlayer.getHall();

        if (hall == null || !hall.getPool().isWithinBounds(location)) {
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE) return;

        handleBlockBreakItem(player, user, event, woolManiaPlayer);
        handleBreakedItem(block, user, player, event);
    }

    public void handleBlockBreakItem(Player player, User user, BlockBreakEvent event, WoolManiaPlayer woolManiaPlayer) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemBuilder item = ItemBuilder.item(itemInHand);
        CustomItem customItem = new CustomItem(item);
        int itemLevel = customItem.getLevel();
        int playerLevel = woolManiaPlayer.getLevel();
        Halls hall = woolManiaPlayer.getHall();
        int itemTier = customItem.getTierID();
        int hallValue = hall.getHallValue().getValue();

        if (itemLevel > playerLevel && itemInHand.getType() != Material.AIR) {
            user.sendMessage (ITEM_LEVEL_TO_LOW);
            NO.playSound (player);
            event.setCancelled (true);
            return;
        }

        if (hallValue > itemTier && itemInHand.getType() != Material.AIR) {
            user.sendMessage (TIER_TO_LOW);
            NO.playSound (player);
            event.setCancelled (true);
            return;
        }

        if (customItem.getDurability () == 0 && itemInHand.getType() != Material.AIR) {
            user.sendMessage (NOT_ENOUGHT_DURABILITY);
            NO.playSound (player);
            event.setCancelled (true);
            return;
        }

        if (customItem.hasItemID()) {
            customItem.reduceDurability();
            player.getInventory().setItemInMainHand(customItem.getItemStack());
        }
    }

    public void handleBreakedItem(Block block, User user, Player player, BlockBreakEvent event) {
        WoolRegistry registry = WoolMania.getInstance().getWoolRegistry();

        if (event.isCancelled()) return;
        
        if (registry.contains(block)) {
            WoolRegistry.Entry entry = registry.get(block);

            event.setDropItems(false);
            WoolMania.getInstance().getLevelXPHandler().manageLevelXP(player);
            WoolMania.getStaticPlayer(player).addFarmedBlocks(1, player);

            WoolItem woolItem = new WoolItem(user, entry);
            ItemStack itemStack = woolItem.getItemStack();

            if (itemStack.getType() != Material.COPPER_BULB || itemStack.getType() == Material.COPPER_GRATE) {
                WOOL_BREAK.playSound(player);
            }

            checkInventoryFullWithOneSlotEmpty(player, user);
            dropBlocks(player, block, woolItem);

            Light lightBlock = (Light) Material.LIGHT.createBlockData();
            lightBlock.setLevel(12);
            block.setBlockData(lightBlock);


        }
    }

    public void dropBlocks(Player player, Block block, CustomItem customItem) {
        if (player.getInventory().addItem(customItem.getItemStack()).isEmpty()) {
            WoolMania.getStaticPlayer(player).getFarmingSound().playSound(player);
        } else {
            block.getWorld().dropItemNaturally(block.getLocation(), customItem.getItemStack());
        }
    }

    private boolean hasSentMessage = false;

    public void checkInventoryFullWithOneSlotEmpty(Player player, User user) {
        Inventory inventory = player.getInventory();
        int emptySlots = 0;

        for (int i = 0; i < 36; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                emptySlots++;
            }
        }

        if (emptySlots == 1 && !hasSentMessage) {
            user.sendMessage(INVENTORY_ALMOST_FULL);
            INVENTORY_ALMOST_FULL_SOUND.playSound(player);
            hasSentMessage = true;
        } else if (emptySlots > 1) {
            hasSentMessage = false;
        }

    }
}

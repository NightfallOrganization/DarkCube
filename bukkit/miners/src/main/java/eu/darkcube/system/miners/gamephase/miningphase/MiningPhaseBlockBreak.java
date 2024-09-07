/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.miningphase;

import static eu.darkcube.system.server.item.ItemBuilder.item;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.server.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MiningPhaseBlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemBuilder itemBuilder = item(block.getType());
        ItemStack itemStack = getItemStack(itemBuilder.build());

        dropBlocks(player, block, itemStack);

        if (block.getType() == Material.REDSTONE_ORE || block.getType() == Material.DEEPSLATE_REDSTONE_ORE) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 3));
        }

        Light l = (Light) Material.LIGHT.createBlockData();
        l.setLevel(12);
        block.setBlockData(l);
    }

    public void dropBlocks(Player player, Block block, ItemStack itemStack) {
        if (block.getType() == Material.EMERALD_ORE
                || block.getType() == Material.DEEPSLATE_EMERALD_ORE
                || block.getType() == Material.REDSTONE_ORE
                || block.getType() == Material.DEEPSLATE_REDSTONE_ORE) return;

        if (player.getInventory().addItem(itemStack).isEmpty()) {
            Miners.getStaticPlayer(player).getFarmingSound().playSound(player);
        } else {
            block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
        }

    }

    private ItemStack getItemStack(ItemStack itemStack) {
        try {
            return switch (itemStack.getType()) {
                case STONE -> item(Material.COBBLESTONE).build();
                case IRON_ORE -> item(Material.RAW_IRON).build();
                case GOLD_ORE -> item(Material.RAW_GOLD).build();
                case DIAMOND_ORE -> item(Material.DIAMOND).build();
                case COAL_ORE -> item(Material.COAL).build();
                default -> itemStack;
            };
        } catch (Exception e) {
            return itemStack;
        }
    }
}

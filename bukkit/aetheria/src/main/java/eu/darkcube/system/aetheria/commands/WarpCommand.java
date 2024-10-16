/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.Overlay;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WarpCommand implements CommandExecutor {

    @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        // TODO: Sei kein idiot
        Inventory inv = Bukkit.createInventory(null, 45, LegacyComponentSerializer
                .legacySection()
                .serialize(Overlay.WARP_INVENTORY.component()));

        // Create ItemStacks
        ItemStack grassBlock = new ItemStack(Material.GRASS_BLOCK);
        ItemStack crimsonNylium = new ItemStack(Material.ENDER_EYE);
        ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
        ItemStack endStone = new ItemStack(Material.ENDER_EYE);
        ItemStack deepslate = new ItemStack(Material.DEEPSLATE_TILE_SLAB);

        // Set the names
        ItemMeta grassBlockMeta = grassBlock.getItemMeta();
        grassBlockMeta.setDisplayName("§aBeastrealm");
        grassBlock.setItemMeta(grassBlockMeta);

        ItemMeta crimsonNyliumMeta = crimsonNylium.getItemMeta();
        crimsonNyliumMeta.setDisplayName("§eSoon...");
        crimsonNylium.setItemMeta(crimsonNyliumMeta);

        ItemMeta netherStarMeta = netherStar.getItemMeta();
        netherStarMeta.setDisplayName("§bSpawn");
        netherStar.setItemMeta(netherStarMeta);

        ItemMeta endStoneMeta = endStone.getItemMeta();
        endStoneMeta.setDisplayName("§eSoon...");
        endStone.setItemMeta(endStoneMeta);

        ItemMeta deepslateMeta = deepslate.getItemMeta();
        deepslateMeta.setDisplayName("§6Plot");
        deepslate.setItemMeta(deepslateMeta);

        // Add items to specific slots
        inv.setItem(11, grassBlock); // Set to slot 11 (0-indexed)
        inv.setItem(15, crimsonNylium); // Set to slot 15
        inv.setItem(22, netherStar); // Set to slot 22
        inv.setItem(29, endStone); // Set to slot 29
        inv.setItem(33, deepslate); // Set to slot 33

        player.openInventory(inv);
        return true;
    }

}

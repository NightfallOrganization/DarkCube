/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import eu.darkcube.system.skyland.equipment.*;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GetItem implements CommandExecutor {

    @Override public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (s instanceof Player player) {
            Inventory inventory = Bukkit.createInventory(null, 9 * 4, net.kyori.adventure.text.Component
                    .text()
                    .content("               Items")
                    .color(TextColor.color(140, 202, 255))
                    .build());

            //inventory.setItem(1, CustomArmor.getEsdeathBootsItem());
            inventory.setItem(8, CustomArmor.getNetherblockHelmetItem());
            //inventory.setItem(17, Item.NETHERBLOCK_CHESTPLATE.getItem(user));
            inventory.setItem(26, CustomArmor.getNetherblockLeggingsItem());
            inventory.setItem(35, CustomArmor.getNetherblockBootsItem());
            //inventory.setItem(9, Item.STARTER_SWORD.getItem(user));
            inventory.setItem(10, CustomArmor.getNetherblockPickaxeItem());
            inventory.setItem(7, CustomArmor.getMiningHelmetItem());
            //inventory.setItem(16, Item.MINING_CHESTPLATE.getItem(user));
            inventory.setItem(25, CustomArmor.getMiningLeggingsItem());
            inventory.setItem(34, CustomArmor.getMiningBootsItem());
            inventory.setItem(6, CustomArmor.getSpeedHelmetItem());
            //inventory.setItem(15, Item.SPEED_CHESTPLATE.getItem(user));
            inventory.setItem(24, CustomArmor.getSpeedLeggingsItem());
            inventory.setItem(33, CustomArmor.getSpeedBootsItem());
            //inventory.setItem(0, Item.POOP.getItem(user));

            ArrayList<Component> comps = new ArrayList<>();
            comps.add(new Component(Material.DRAGON_SCALE, ComponentType.AXE));
            comps.add(new Component(Material.DRAGON_SCALE, ComponentType.AXE));
            inventory.setItem(13, Equipment
                    .createEquipment(1, new ItemStack(org.bukkit.Material.DIAMOND_SWORD), Rarity.RARE, comps, EquipmentType.HELMET)
                    .getModel());
            if (player.getInventory().getItemInMainHand().getItemMeta() != null) {
                Equipment equipment = Equipment.loadFromItem(player.getInventory().getItemInMainHand());
                if (equipment.getModel() != null) {
                    System.out.println("model nn");
                    inventory.setItem(1, equipment.getModel());
                } else {
                    //System.out.println();
                }

            }
            player.openInventory(inventory);

        } else {
            s.sendMessage("§7Du bist kein Spieler!");
        }

        return false;
    }
}

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.aetheria.util.CustomItemManager;
import eu.darkcube.system.aetheria.util.TeleportManager;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GetItemCommand implements CommandExecutor {

    private final Aetheria aetheria;

    public GetItemCommand(Aetheria aetheria) {
        this.aetheria = aetheria;
    }

    @Override public boolean onCommand(@NotNull CommandSender s, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (s instanceof Player player) {
            Inventory inventory = Bukkit.createInventory(null, 9 * 4, Component
                    .text()
                    .content("§f\uDAFF\uDFEFḊ")
                    .color(TextColor.color(140, 202, 255))
                    .build());

            ItemStack zombieEgg = new ItemStack(Material.ZOMBIE_SPAWN_EGG, 64);
            ItemStack manaStones = TeleportManager.getManaStone();
            manaStones.setAmount(32);
            inventory.setItem(6, manaStones);
            ItemStack manaStairs = TeleportManager.getManaStairs();
            manaStairs.setAmount(16);
            inventory.setItem(7, manaStairs);

            inventory.setItem(0, CustomItemManager.getSwiftSword());
            inventory.setItem(1, CustomItemManager.getOrdinaryBag());
            inventory.setItem(2, CustomItemManager.getEnderBag());
            inventory.setItem(3, CustomItemManager.getRingOfHealing());
            inventory.setItem(4, CustomItemManager.getRingOfSpeed());
            inventory.setItem(5, TeleportManager.getTeleportCatalyst());
            inventory.setItem(9, aetheria.customSwordManager().getCustomSword(1));
            inventory.setItem(10, aetheria.customChestplateManager().getCustomChestplate(1));
            inventory.setItem(11, aetheria.customPickaxeManager().getCustomPickaxe(1));
            inventory.setItem(12, aetheria.customAxeManager().getCustomAxe(1));
            inventory.setItem(8, zombieEgg);

            player.openInventory(inventory);

        } else {
            s.sendMessage("§7Du bist kein Spieler!");
        }

        return false;
    }
}
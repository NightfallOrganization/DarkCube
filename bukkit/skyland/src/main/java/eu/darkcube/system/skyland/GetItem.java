/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import eu.darkcube.system.skyland.Equipment.*;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
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

import java.util.ArrayList;
import java.util.Arrays;

public class GetItem implements CommandExecutor {


	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (s instanceof Player player) {
			Inventory inventory = Bukkit.createInventory(null, 9 * 4,
					Component.text().content("               Items")
							.color(TextColor.color(140, 202, 255)).build());
			User user = UserAPI.getInstance().getUser(player);

			//inventory.setItem(1, CustomArmor.getEsdeathBootsItem());
			inventory.setItem(8, CustomArmor.getNetherblockHelmetItem());
			inventory.setItem(17, Item.NETHERBLOCK_CHESTPLATE.getItem(user));
			inventory.setItem(26, CustomArmor.getNetherblockLeggingsItem());
			inventory.setItem(35, CustomArmor.getNetherblockBootsItem());
			inventory.setItem(9, Item.STARTER_SWORD.getItem(user));
			inventory.setItem(10, CustomArmor.getNetherblockPickaxeItem());
			inventory.setItem(7, CustomArmor.getMiningHelmetItem());
			inventory.setItem(16, Item.MINING_CHESTPLATE.getItem(user));
			inventory.setItem(25, CustomArmor.getMiningLeggingsItem());
			inventory.setItem(34, CustomArmor.getMiningBootsItem());
			inventory.setItem(6, CustomArmor.getSpeedHelmetItem());
			inventory.setItem(15, Item.SPEED_CHESTPLATE.getItem(user));
			inventory.setItem(24, CustomArmor.getSpeedLeggingsItem());
			inventory.setItem(33, CustomArmor.getSpeedBootsItem());
			inventory.setItem(0, Item.POOP.getItem(user));

			ArrayList<Components> comps = new ArrayList<>();
			comps.add(new Components(Materials.DRAGON_SCALE, ComponentTypes.AXE));
			inventory.setItem(13, new Equipments(1, new ItemStack(Material.DIAMOND_SWORD), Rarity.RARE, 10, comps, EquipmentType.HELMET).getModel());

			player.openInventory(inventory);

		} else {
			s.sendMessage("§7Du bist kein Spieler!");
		}

		return false;
	}
}

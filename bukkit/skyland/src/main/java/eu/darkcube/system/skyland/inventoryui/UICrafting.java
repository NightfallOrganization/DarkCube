/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.inventoryui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class UICrafting extends InventoryUI{
	Set<Integer> inputSlots =  Set.of(11, 20, 29);
	Set<Integer> resultSlots  =  Set.of(16, 25);
	Set<Integer> buttons = Set.of(23);
	public UICrafting(String name, Player p) {
		super( 5, name, p);
		int row = 5;
		for (int i = 0; i < 9*row; i++) {
			setInvSlot(new UIitemStack(true, new ItemStack(Material.BLACK_STAINED_GLASS_PANE)), i);
		}
		for (Set<Integer> set : Set.of(inputSlots, resultSlots)){
			for (Integer i : set) {
				setInvSlot(new UIitemStack(false, new ItemStack(Material.AIR)), i);
			}
		}
		for (Integer i : buttons) {
			setInvSlot(new UIitemStack(false, new ItemStack(Material.OAK_BUTTON)), i);
		}
		//perhaps add crafting lvl?


	}

	@Override
	public boolean invClickEvent(InventoryClickEvent e) {
		if (buttons.contains(e.getSlot())){
			if (e.getSlot() == 23){

			}
		}
		//todo check here for confirm button
		return super.invClickEvent(e);
	}

	@Override public boolean invCloseEvent(InventoryCloseEvent e) {
		for(int i : inputSlots){
			if (e.getInventory().getItem(i) != null){
				if(p.getInventory().firstEmpty() < 0){
					p.getInventory().addItem(e.getInventory().getItem(i));
				}else {
					p.getWorld().dropItem(p.getLocation(), e.getInventory().getItem(i));
				}
			}
		}

		return super.invCloseEvent(e);
	}
}

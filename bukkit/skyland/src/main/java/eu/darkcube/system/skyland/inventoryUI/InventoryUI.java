/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.inventoryUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUI {

	UIitemStack[][] inv;
	String name;
	Player p;

	Inventory inventory;
	public InventoryUI(int row, String name, Player p) {
		inv = new UIitemStack[row][9];
		this.name = name;
		for (int i = 0; i<inv.length; i++) {
			for (int j = 0; j<inv[i].length; j++){
				inv[i][j] = new UIitemStack(false, new ItemStack(Material.AIR));
			}
		}
		this.p = p;
		inventory = Bukkit.createInventory(p, inv.length * 9, name);

		AllInventory.getInstance().getAllCustomInvs().add(this);

	}

	public void openInv() {
		p.openInventory(inventory);
	}

	public void setInvSlot(UIitemStack us, int x, int y){
		if (x < inv.length){
			if (y < 9){
				inv[x][y] = us;
				inventory.setItem(x*9+y, us.getItemStack());
			}else {
				System.out.println("y out of bounds at setInvSlot() in UIinventory");
			}
		}else {
			System.out.println("x out of bounds at setInvSlot() in UIinventory");
		}


	}
	public void setInvSlot(UIitemStack us, int slot){
		setInvSlot(us, slot/9, slot%9);
	}

	public boolean invClickEvent(InventoryClickEvent e){
		e.setCancelled(inv[e.getSlot()/9][e.getSlot()%9].isUnmoveble());

		return false;
	}

	public boolean invDragEvent(InventoryDragEvent e){
		//todo
		return false;
	}

	public boolean invCloseEvent(InventoryCloseEvent e){

		return true;
	}

	public Inventory getInventory() {
		return inventory;
	}

}

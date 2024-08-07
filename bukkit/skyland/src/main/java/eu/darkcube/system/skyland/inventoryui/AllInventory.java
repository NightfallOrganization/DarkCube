/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.inventoryui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class AllInventory implements Listener {

	private static ArrayList<InventoryUI> allCustomInvs = new ArrayList<>();

	private static AllInventory instance;

	public static AllInventory getInstance(){
		if (instance == null){
			instance = new AllInventory();
		}

		return instance;
	}


	private AllInventory(){

	}

	public ArrayList<InventoryUI> getAllCustomInvs() {
		return allCustomInvs;
	}

	public void removeInv(Inventory inv){
		for (InventoryUI invui:allCustomInvs) {
			if (invui.getInventory().equals(inv)){
				allCustomInvs.remove(invui);
			}
		}
	}

	@EventHandler
	public void invClickEvent(InventoryClickEvent e){
		e.getInventory();
		for (InventoryUI invUI:allCustomInvs) {
			if(invUI.getInventory().equals(e.getClickedInventory())){
				boolean isToBeClosed = invUI.invClickEvent(e);
			}

		}
	}

	@EventHandler
	public void invDragEvent(InventoryDragEvent e){
		for (InventoryUI invUI:allCustomInvs) {
			if(invUI.getInventory().equals(e.getInventory())) {
				invUI.invDragEvent(e);
			}
		}
	}

	@EventHandler
	public void invCloseEvent(InventoryCloseEvent e){
		for (InventoryUI invUI:allCustomInvs) {

			if(invUI.getInventory().equals(e.getInventory())) {
				invUI.invCloseEvent(e);
			}
		}
	}

}

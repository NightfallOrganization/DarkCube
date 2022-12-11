/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.shop;

import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.pixel.bedwars.shop.site.ShopSite;
import de.pixel.bedwars.util.ItemManager;

public class ShopListener implements Listener {

	@EventHandler
	public void handle(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getWhoClicked();
		if (!Shop.open.contains(p)) {
			return;
		}
		if (e.getClickedInventory() == e.getView().getTopInventory()) {
			e.setCancelled(true);
		}
		ItemStack item = e.getCurrentItem();
		if (item == null || !item.hasItemMeta()) {
			return;
		}
		String itemid = ShopItem.getItemId(item);
		if (itemid == null) {
			return;
		}
		first: for (ShopItem sitem : ShopItem.values()) {
			if (sitem.getItemId().equals(itemid)) {
				if (sitem.isBuyable()) {
					buy(e, p, sitem);
					break first;
				}
				for (ShopSite<?> site : ShopSite.SITES) {
					ShopItem it = site.getRepresentation();
					if (sitem == it) {
						Shop.open(p, site);
						break first;
					}
				}
			}
		}
	}

	private void buy(InventoryClickEvent e, Player p, ShopItem item) {
		Inventory inv = p.getInventory();
		boolean stack = e.isShiftClick();
		Cost cost = item.getCost();
		ItemStack type = cost.getItem();

		ItemStack buyItem = item.getItem(p);

		final int availableMoneyCount = ItemManager.countItems(type, inv);

		int buy = stack ? buyItem.getMaxStackSize() / buyItem.getAmount() : 1;
		if (buy > availableMoneyCount / cost.getCount()) {
			buy = availableMoneyCount / cost.getCount();
		}

		ItemManager.removeItems(inv, type, buy * cost.getCount());

		int addCount = doStuff(inv, type, buyItem, buy * buyItem.getAmount(), item) / buyItem.getAmount();
		if (addCount == 0) {
			p.playSound(p.getLocation(), Sound.VILLAGER_NO, 10, 1);
//			p.sendMessage(Message.INVENTORY_FULL.getMessage(p));
			return;
		}
		ItemStack[] add = new ItemStack[addCount];
		for (int i = 0; i < add.length; i++) {
			add[i] = buyItem.clone();
		}

		Map<Integer, ItemStack> leftOver = ItemManager.addItem(inv, add);
		for (int slot : leftOver.keySet()) {
			System.out.println("SHOP Buy Operation LEFTOVER: " + slot + ": " + leftOver.get(slot));
			Thread.dumpStack();
		}

//		while (inv.contains(type) && buyed < maxBuy) {
//			int firstEmpty = inv.firstEmpty();
//			if (firstEmpty == -1) {
//				p.playSound(p.getLocation(), Sound.VILLAGER_NO, 10, 1);
//				p.sendMessage(Message.INVENTORY_FULL.getMessage(p));
//				return;
//			}
//
//		}
	}

	private int doStuff(Inventory inv, ItemStack type, ItemStack buyItem, int addCount, ShopItem sitem) {
		int free = 0;
		for (ItemStack i : inv.getContents()) {
			if (i == null || buyItem.isSimilar(i)) {
				free += i == null ? buyItem.getMaxStackSize() : (buyItem.getMaxStackSize() - i.getAmount());
			}
		}
		if (free < buyItem.getAmount()) {
			free = 0;
		}

		if (addCount > free) {
			int tooMuchItems = addCount - free;
			int tooMuchPayed = (int) Math.ceil(tooMuchItems * sitem.getCost().getCount() / (float) buyItem.getAmount());
			if (free == 0) {
				ItemStack[] add = new ItemStack[tooMuchPayed];
				for (int i = 0; i < add.length; i++) {
					add[i] = type.clone();
				}
				ItemManager.addItem(inv, add);
				return 0;
			}
//			int tooMuchPayed =
//					(int) Math.floor((addCount - free) * sitem.getCost().getCount() / (float) buyItem.getAmount());
			addCount = free / buyItem.getAmount() * buyItem.getAmount();
			ItemStack[] add = new ItemStack[tooMuchPayed];
			for (int i = 0; i < add.length; i++) {
				add[i] = type.clone();
			}
			ItemManager.addItem(inv, add);
			return doStuff(inv, type, buyItem, addCount, sitem);
		}
		return addCount;
	}

//	private int remove(Inventory inv, ItemStack item) {
//		int amount = item.getAmount();
//		item.setAmount(1);
//		ItemStack[] contents = inv.getContents();
//		int removed = 0;
//		boolean next = true;
//		for (int i = 0; i < contents.length && removed < amount;) {
//			ItemStack it = contents[i];
//			if (it != null && it.isSimilar(item)) {
//				if (it.getAmount() >= 1) {
//					next = false;
//					it.setAmount(it.getAmount() - 1);
//					if (it.getAmount() == 0) {
//						it = null;
//					}
//					removed++;
//				} else {
//					it = null;
//				}
//				if (it == null) {
//					next = true;
//				}
//				inv.setItem(i, it);
//			} else {
//				next = true;
//			}
//			if (next) {
//				i++;
//			}
//		}
//
//		return amount - removed;
//	}

	@EventHandler
	public void handle(InventoryDragEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getWhoClicked();
		if (!Shop.open.contains(p)) {
			return;
		}
		e.getRawSlots().stream().filter(s -> 0 <= s && e.getView().getTopInventory().getSize() > s).findAny()
				.ifPresent(s -> {
					e.setCancelled(true);
				});
	}
}

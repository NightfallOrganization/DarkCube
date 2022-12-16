/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.pixel.bedwars.shop.ShopItem;
import eu.darkcube.system.inventory.api.util.ItemBuilder;

public class ItemManager {

	public static boolean contains(Inventory inv, ItemStack item, int amount) {
		return countItems(item, inv) >= amount;
	}

	public static int countItems(ItemStack item, Inventory inv) {
		int counted = 0;
		ItemStack[] contents = inv.getContents();
		for (int j = 0; j < contents.length; j++) {
			ItemStack it = contents[j];
			if (it != null && it.isSimilar(item)) {
				counted += it.getAmount();
			}
		}

		int i = counted;
		if (inv instanceof PlayerInventory) {
			PlayerInventory t = (PlayerInventory) inv;
			List<ItemStack> items = new ArrayList<>();
			items.add(t.getHolder().getItemOnCursor());
			items.add(t.getBoots());
			items.add(t.getChestplate());
			items.add(t.getLeggings());
			items.add(t.getHelmet());
			for (ItemStack s : items) {
				if (item.equals(s)) {
					i += s.getAmount();
				}
			}
		}
		return i;
	}

	public static Map<Integer, ItemStack> removeItems(Inventory invToRemoveFrom, ItemStack itemToRemove,
			int count) {
		Map<Integer, ItemStack> ret = new HashMap<>();
		for (int i = 0; i < count; i++) {
			Map<Integer, ItemStack> m = invToRemoveFrom.removeItem(itemToRemove);
			for (int k : m.keySet()) {
				if (ret.containsKey(k)) {
					ret.get(k).setAmount(ret.get(k).getAmount() + m.get(k).getAmount());
				} else {
					ret.put(k, m.get(k));
				}
			}
		}
		return ret;
	}

	private static int firstPartial(Inventory inv, ItemStack item, Map<Integer, ItemStack> toSet) {
		ItemStack[] inventory = inv.getContents();
		for (int slot : toSet.keySet()) {
			inventory[slot] = toSet.get(slot);
		}
		CraftItemStack filteredItem = CraftItemStack.asCraftCopy(item);
		if (item == null) {
			return -1;
		}
		for (int i = 0; i < inventory.length; i++) {
			ItemStack cItem = inventory[i];
			if (cItem != null && cItem.getAmount() < cItem.getMaxStackSize()
					&& cItem.isSimilar(filteredItem)) {
				return i;
			}
		}
		return -1;
	}

	public static HashMap<Integer, ItemStack> addItem(Inventory inv, ItemStack... items) {
		CraftInventory cinv = (CraftInventory) inv;
		HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();
		Map<Integer, ItemStack> toSet = new HashMap<>();
		int i = 0;
		while (i < items.length) {
			ItemStack item = items[i];
			do {
				int firstPartial;
				int partialAmount;
				int maxAmount;
				if ((firstPartial = firstPartial(inv, item, toSet)) == -1) {
					int firstFree = firstEmpty(inv, toSet);
					if (firstFree == -1) {
						leftover.put(i, item);
						break;
					}
					if (item.getAmount() > cinv.getInventory().getMaxStackSize()) {
						CraftItemStack stack = CraftItemStack.asCraftCopy(item);
						stack.setAmount(cinv.getInventory().getMaxStackSize());
						toSet.put(firstFree, stack);
//						inv.setItem(firstFree, stack);
						item.setAmount(item.getAmount() - cinv.getInventory().getMaxStackSize());
						continue;
					}
					toSet.put(firstFree, item);
//					inv.setItem(firstFree, item);
					break;
				}
				ItemStack partialItem =
						toSet.getOrDefault(firstPartial, inv.getItem(firstPartial));
				int amount = item.getAmount();
				if (amount + (partialAmount = partialItem.getAmount()) <= (maxAmount = partialItem.getMaxStackSize())) {
					partialItem.setAmount(amount + partialAmount);
					toSet.put(firstPartial, partialItem);
//					inv.setItem(firstPartial, partialItem);
					break;
				}
				partialItem.setAmount(maxAmount);
				toSet.put(firstPartial, partialItem);
//				inv.setItem(firstPartial, partialItem);
				item.setAmount(amount + partialAmount - maxAmount);
			} while (true);
			++i;
		}
		for (int slot : toSet.keySet()) {
			inv.setItem(slot, toSet.get(slot));
		}

		return leftover;
	}

	public static int freeSpace(Inventory inv, ItemStack type) {
		int free = 0;
		for (ItemStack i : inv.getContents()) {
			if (i == null || (type != null && type.isSimilar(i))) {
				free += type == null ? 64 : type.getMaxStackSize();
			}
		}
		return free;
	}

	private static int firstEmpty(Inventory inv, Map<Integer, ItemStack> toSet) {
		ItemStack[] inventory = inv.getContents();
		for (int slot : toSet.keySet()) {
			inventory[slot] = toSet.get(slot);
		}
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public static int countItems(Material item, Inventory inv) {
		int i = 1;
		for (; inv.contains(item, i); i++) {
		}
		if (inv instanceof PlayerInventory) {
			PlayerInventory t = (PlayerInventory) inv;
			List<ItemStack> items = new ArrayList<>();
			items.add(t.getHolder().getItemOnCursor());
			items.add(t.getBoots());
			items.add(t.getChestplate());
			items.add(t.getLeggings());
			items.add(t.getHelmet());
			for (ItemStack s : items) {
				if (s != null)
					if (s.getType() == item) {
						i += s.getAmount();
					}
			}
		}
		return i - 1;
	}

	public static ItemStack getItem(Item item, Player user, String... replacements) {
		return getItem(item, user, replacements, new String[0]);
	}

	public static ItemStack getItem(Item item, Player user, String[] replacements, String... loreReplacements) {
//		ItemBuilder builder = item.getBuilder().getUnsafe().setString("itemId", getItemId(item)).builder();
		ItemBuilder builder = setItemId(item.getBuilder(), getItemId(item));
		String name = getDisplayName(item, user, replacements);
		builder.displayname(name);
		if (builder.getLores().size() != 0) {
			builder.getLores().clear();
			String last = null;
			for (String lore : Message.getMessage(Message.ITEM_PREFIX + Message.LORE_PREFIX + item.name(),
					I18n.getPlayerLanguage(user), loreReplacements).split("\\%n")) {
				if (last != null) {
					lore = ChatColor.getLastColors(last) + lore;
				}
				last = lore;
				builder.lore(last);
			}
		}
		return builder.build();
	}

	public static String getItemId(Item item) {
		return Message.ITEM_PREFIX + item.getId();
	}

	public static String getItemId(ShopItem item) {
		return item.getId();
	}

	public static String getMapId(ItemStack item) {
		return getNBTValue(new ItemBuilder(item), "map");
	}

	public static int getLifes(ItemStack item) {
		return Integer.parseInt(getNBTValue(new ItemBuilder(item), "lifes"));
	}

	public static ItemBuilder setItemId(ItemBuilder b, String id) {
		b.getUnsafe().setString("itemId", id);
		return b;
	}

	public static String getItemId(ItemStack item) {
		return getNBTValue(new ItemBuilder(item), "itemId");
	}

	public static String getTeamId(ItemStack item) {
		return getNBTValue(new ItemBuilder(item), "team");
	}

	private static String getNBTValue(ItemBuilder builder, String key) {
		return builder.getUnsafe().getString(key);
	}

	public static String getDisplayName(Item item, Player user, String... replacements) {
		return Message.getMessage(getItemId(item), I18n.getPlayerLanguage(user), replacements);
	}
}

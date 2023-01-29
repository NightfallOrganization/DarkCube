/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;

public class ItemManager {

	public static int countItems(ItemStack item, Inventory inv) {
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
				if (item.equals(s)) {
					i += s.getAmount();
				}
			}
		}
		return i - 1;
	}

	public static void removeItems(User user, Inventory invToRemoveFrom, ItemStack itemToRemove, int count) {
		if (user.getData().getWoolSubtractDirection() == WoolSubtractDirection.RIGHT_TO_LEFT) {
			Map<Integer, ItemStack> leftOver = new HashMap<>();
			itemToRemove = new ItemStack(itemToRemove);
			itemToRemove.setAmount(1);
			int toDelete = count;
			int did = 0;
			for (int i = count - 1; i >= 0; i--) {
				do {
					int last;
					if ((last = ItemManager.last(invToRemoveFrom, itemToRemove, false)) == -1) {
						itemToRemove.setAmount(toDelete);
						leftOver.put(i, itemToRemove);
						break;
					}
					ItemStack item = invToRemoveFrom.getItem(last);
					int amount = item.getAmount();
					if (amount <= toDelete) {
						toDelete -= amount;
						invToRemoveFrom.clear(last);
						continue;
					}
					item.setAmount(amount - toDelete);
					invToRemoveFrom.setItem(last, item);
					toDelete = 0;
				} while (toDelete > 0);
				did++;
				if (did > 50) {
					break;
				}
			}
		} else {
			for (int i = 0; i < count; i++) {
				invToRemoveFrom.removeItem(itemToRemove);
			}
		}
	}

	public static int last(Inventory inv, ItemStack item, boolean withAmount) {
		if (item == null) {
			return -1;
		}
		ItemStack[] inve = inv.getContents();
		for (int i = inve.length - 1; i >= 0; i--) {
			if (inve[i] != null && (withAmount ? item.equals(inve[i]) : item.isSimilar(inve[i]))) {
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

	public static ItemStack getItem(Item item, User user, Object... replacements) {
		return ItemManager.getItem(item, user, replacements, new Object[0]);
	}

	public static ItemStack getItem(Item item, User user, Object[] replacements, Object... loreReplacements) {
		ItemBuilder builder = item.getBuilder().getUnsafe().setString("itemId", ItemManager.getItemId(item)).builder();
		String name = ItemManager.getDisplayName(item, user, replacements);
		builder.setDisplayName(name);
		if (builder.getLores().size() != 0) {
			builder.getLores().clear();
			String last = null;
			for (String lore : Message
					.getMessage(Message.ITEM_PREFIX + Message.LORE_PREFIX + item.name(), user.getLanguage(),
							loreReplacements)
					.split("\\R")) {
				if (last != null) {
					lore = ChatColor.getLastColors(last) + lore;
				}
				last = lore;
				builder.addLore(last);
			}
		}
		if (item.getPerk() != null && WoolBattle.getInstance().getLobby().isEnabled()) {
			PerkType p = item.getPerk();
			if (p.getCost() != 0) {
				if (p.isCostPerBlock()) {
					builder.addLore(Message.COSTS_PER_BLOCK.getMessage(user, Integer.toString(p.getCost())));
				} else {
					builder.addLore(Message.COSTS.getMessage(user, Integer.toString(p.getCost())));
				}
			}
			if (p.hasCooldown())
				builder.addLore(Message.COOLDOWN.getMessage(user, Integer.toString(p.getCooldown())));
		}
		return builder.build();
	}

	public static ItemBuilder setItemId(ItemBuilder b, String itemId) {
		return ItemManager.setId(b, "itemId", itemId);
	}

	public static ItemStack setItemId(ItemStack s, String itemId) {
		return ItemManager.setItemId(new ItemBuilder(s), itemId).build();
	}

	public static ItemBuilder setId(ItemBuilder b, String key, String id) {
		b.getUnsafe().setString(key, id);
		return b;
	}

	public static ItemStack setId(ItemStack s, String key, String id) {
		return ItemManager.setId(new ItemBuilder(s), key, id).build();
	}

	public static String getId(ItemStack s, String key) {
		if (!s.hasItemMeta())
			return null;
		return ItemManager.getNBTValue(new ItemBuilder(s), key);
	}

	public static String getItemId(Item item) {
		return Message.ITEM_PREFIX + item.getKey();
	}

	public static String getMapId(ItemStack item) {
		return ItemManager.getNBTValue(new ItemBuilder(item), "map");
	}

	public static int getLifes(ItemStack item) {
		return Integer.parseInt(ItemManager.getNBTValue(new ItemBuilder(item), "lifes"));
	}

	public static String getItemId(ItemStack item) {
		return ItemManager.getId(item, "itemId");
	}

	public static String getTeamId(ItemStack item) {
		return ItemManager.getNBTValue(new ItemBuilder(item), "team");
	}

	private static String getNBTValue(ItemBuilder builder, String key) {
		return builder.getUnsafe().getString(key);
	}

	public static String getDisplayName(Item item, User user, Object... replacements) {
		return Message.getMessage(ItemManager.getItemId(item), user.getLanguage(), replacements);
	}

}

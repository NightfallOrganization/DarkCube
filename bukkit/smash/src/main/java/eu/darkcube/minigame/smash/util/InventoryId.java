/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.util;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import eu.darkcube.minigame.smash.Main;
import eu.darkcube.minigame.smash.map.Map;
import eu.darkcube.minigame.smash.user.Message;
import eu.darkcube.minigame.smash.user.User;

public abstract class InventoryId {

	public static final InventoryId VOTING_LIFES = new InventoryId() {

		@Override
		public Inventory build(User user) {
			Inventory inv = Bukkit.createInventory(null, 9, getMessage().getMessage(user));
			setItem(inv, 1, 3, user);
			setItem(inv, 3, 5, user);
			setItem(inv, 5, 7, user);
			setItem(inv, 7, 9, user);
			return inv;
		}

		private void setItem(Inventory inv, int slot, int lifes, User user) {
			ItemBuilder b = new ItemBuilder(Item.VOTING_LIFES_ITEM.getItem(user)).unsafeStackSize(true)
					.setAmount(lifes);
			for (int i = 0; i < b.getLores().size(); i++) {
				String lore = b.getLores().get(i);
				lore = lore.replace("{0}", Integer.toString(lifes));
				b.getLores().set(i, lore);
			}
			b.getUnsafe().setInt("lifes", lifes);
			b.setDisplayName(b.getDisplayname().replace("{0}", Integer.toString(lifes)));
			if (Main.getInstance().getLobby().VOTE_LIFES.containsKey(user)
					&& Main.getInstance().getLobby().VOTE_LIFES.get(user) == lifes) {
				b.glow();
			}
			inv.setItem(slot, b.build());
		}

		@Override
		public Message getMessage() {
			return Message.INVENTORY_TITLE_VOTING_LIFES;
		}
	};

	public static final InventoryId VOTING_MAPS = new InventoryId() {

		@Override
		public Message getMessage() {
			return Message.INVENTORY_TITLE_VOTING_MAPS;
		}

		@Override
		public Inventory build(User user) {
			Set<Map> maps = new HashSet<>();
			Inventory inv = Bukkit.createInventory(null, size(maps.size()), getMessage().getMessage(user));
			int slot = 0;
			for (Map map : maps) {
				ItemBuilder builder = new ItemBuilder(map.getIcon().getMaterial());
				builder.setDurability(map.getIcon().getId());
				builder.getUnsafe().setString("itemId", "map").setString("map", map.getName());
				if (map.equals(Main.getInstance().getLobby().VOTE_MAPS.get(user))) {
					builder.glow();
				}
				builder.setDisplayName(ChatColor.GREEN + map.getName());
				inv.setItem(slot, builder.build());
				slot++;
			}
			return inv;
		}

		public int size(int slots) {
			int size = slots;
			if (size == 0)
				size = 9;
			while (size % 9 != 0)
				size++;
			return size;
		}
	};

	public abstract Message getMessage();

	public abstract Inventory build(User user);

}

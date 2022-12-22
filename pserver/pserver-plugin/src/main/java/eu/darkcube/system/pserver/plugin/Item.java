/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin;

import eu.darkcube.system.inventoryapi.ItemBuilder;
import eu.darkcube.system.pserver.plugin.user.User;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

import static eu.darkcube.system.inventoryapi.ItemBuilder.item;
import static org.bukkit.Material.ARROW;
import static org.bukkit.Material.BOOK;

public enum Item {

	ARROW_NEXT(item(ARROW)), ARROW_PREVIOUS(item(ARROW)), USER_MANAGMENT_PERMISSIONS(item(BOOK)),

	;

	public static final String PREFIX = "ITEM_";
	public static final String PREFIX_LORE = "LORE_";
	public static final String ITEMID_KEY = "itemid";

	private final String key;
	private final boolean hasLore;
	private final ItemBuilder builder;

	private Item(ItemBuilder builder) {
		this(builder, false);
	}

	private Item(ItemBuilder builder, boolean hasLore) {
		this.builder = builder;
		this.hasLore = hasLore;
		this.key = name();
	}

	public Collection<String> getMessageKeys() {
		Collection<String> keys = new ArrayList<>();
		keys.add(PREFIX + key);
		if (hasLore())
			keys.add(PREFIX + PREFIX_LORE + keys);
		return keys;
	}

	public boolean hasLore() {
		return hasLore;
	}

	public String getKey() {
		return key;
	}

	public ItemStack getItem(User user) {
		return this.getItem(user, new Object[0]);
	}

	public ItemStack getItem(User user, Object... displayNameArgs) {
		return this.getItem(user, displayNameArgs, new Object[0]);
	}

	public ItemStack getItem(User user, Object[] displayNameArgs, Object[] loreArgs) {
		@SuppressWarnings("deprecation")
		ItemBuilder b = new ItemBuilder(builder);
		b.displayname(getDisplayName(user, displayNameArgs));
		if (hasLore()) {
			b.lore(getLore(user, loreArgs));
		}
		b.getUnsafe().setString(ITEMID_KEY, getKey());
		return b.build();
	}

	public boolean equals(ItemStack item) {
		return hasItemId(item) && getKey().equals(getItemId(item));
	}

	public static boolean hasItemId(ItemStack item) {
		return new ItemBuilder(item).getUnsafe().containsKey(ITEMID_KEY);
	}

	public static String getItemId(ItemStack item) {
		return new ItemBuilder(item).getUnsafe().getString(ITEMID_KEY);
	}

	public String[] getLore(User user, Object... args) {
		return Message.getMessageString(key, new String[] { PREFIX, PREFIX_LORE }, user.getLanguage(), args)
				.split("\n");
	}

	public String getDisplayName(User user, Object... args) {
		return Message.getMessageString(key, new String[] { PREFIX }, user.getLanguage(), args);
	}
}

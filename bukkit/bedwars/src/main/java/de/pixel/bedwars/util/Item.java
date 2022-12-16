/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.inventory.api.util.ItemBuilder;

import static eu.darkcube.system.inventory.api.util.ItemBuilder.*;
import static org.bukkit.Material.*;

public enum Item {

	LOBBY_VOTING_IRON("LOBBY_VOTING_IRON", item(IRON_INGOT)),
	LOBBY_VOTING_IRON_YES("LOBBY_VOTING_IRON_YES", item(INK_SACK).durability((short) 10).lore("")),
	LOBBY_VOTING_IRON_NO("LOBBY_VOTING_IRON_NO", item(INK_SACK).durability((short) 1).lore("")),

	LOBBY_VOTING_GOLD("LOBBY_VOTING_GOLD", item(GOLD_INGOT)),
	LOBBY_VOTING_GOLD_YES("LOBBY_VOTING_GOLD_YES", item(INK_SACK).durability((short) 10).lore("")),
	LOBBY_VOTING_GOLD_NO("LOBBY_VOTING_GOLD_NO", item(INK_SACK).durability((short) 1).lore("")),

	LOBBY_TEAMS("LOBBY_TEAMS", item(BOOK)),

	LOBBY_MAPS("LOBBY_MAPS", item(PAPER)),

	;

	private final ItemBuilder builder;
	private final String itemId;

	Item(String itemId, ItemBuilder builder) {
		this.itemId = itemId;
		this.builder = builder;
	}

	public String getId() {
		return itemId;
	}

	@SuppressWarnings("deprecation")
	public ItemBuilder getBuilder() {
		return new ItemBuilder(builder);
	}

	public String getDisplayName(Player user) {
		return getDisplayName(user, new String[0]);
	}

	public String getItemId() {
		return ItemManager.getItemId(this);
	}

	public String getDisplayName(Player user, String... replacements) {
		return ItemManager.getDisplayName(this, user, replacements);
	}

	public ItemStack getItem(Player user) {
		return ItemManager.getItem(this, user);
	}

	public ItemStack getItem(Player user, String... replacements) {
		return ItemManager.getItem(this, user, replacements);
	}

	public ItemStack getItem(Player user, String[] replacements, String... loreReplacements) {
		return ItemManager.getItem(this, user, replacements, loreReplacements);
	}
}

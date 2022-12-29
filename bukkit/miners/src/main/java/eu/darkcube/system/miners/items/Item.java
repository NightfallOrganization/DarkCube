/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.items;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

import static eu.darkcube.system.inventoryapi.item.ItemBuilder.item;

public enum Item {

	PICKAXE_DEFAULT(item(Material.STONE_PICKAXE).unbreakable(true)),
	PICKAXE_IRON(item(Material.IRON_PICKAXE).unbreakable(true)),
	PICKAXE_GOLD(item(Material.GOLD_PICKAXE).unbreakable(true)),
	PICKAXE_DIAMOND(item(Material.DIAMOND_PICKAXE).unbreakable(true)),

	INGOT_IRON(item(Material.IRON_INGOT)),
	INGOT_GOLD(item(Material.GOLD_INGOT)),
	DIAMOND(item(Material.DIAMOND)),
	COBBLESTONE(item(Material.COBBLESTONE)),
	TNT(item(Material.TNT)),
	SHEARS(item(Material.SHEARS)),
	CRAFTING_TABLE(item(Material.WORKBENCH)),
	STICK(item(Material.STICK));

	public static final String ITEM_PREFIX = "ITEM_";
	public static final String NAME_SUFFIX = "_NAME";
	public static final String LORE_SUFFIX = "_LORE";
	private static final Key itemId = new Key(Miners.getInstance(), "itemId");
	private final ItemBuilder itemBuilder;
	private final String KEY_NAME;
	private final String KEY_LORE;

	Item(ItemBuilder ib) {
		this.itemBuilder = ib;
		this.KEY_NAME = ITEM_PREFIX + this.name() + NAME_SUFFIX;
		this.KEY_LORE = ITEM_PREFIX + this.name() + LORE_SUFFIX;
	}

	public Component getName(Language lang) {
		return Message.getMessage(KEY_NAME, lang);
	}

	public Component getLore(Language lang) {
		return Message.getMessage(KEY_LORE, lang);
	}

	public ItemStack getItem(Language lang, int amount) {
		Component lore = getLore(lang);
		ItemBuilder itemBuilder = this.itemBuilder.clone();
		itemBuilder.persistentDataStorage().iset(itemId, PersistentDataTypes.STRING, this.name());
		return "".equals(PlainTextComponentSerializer.plainText().serialize(lore))
				? itemBuilder.displayname(getName(lang)).amount(amount).build()
				: itemBuilder.displayname(getName(lang))
						.lore(Collections.singletonList(getLore(lang))).amount(amount).build();
	}

	public ItemStack getItem(Player player, int amount) {
		return getItem(Miners.getPlayerManager().getMinersPlayer(player).getLanguage(), amount);
	}

	public ItemStack getItem(Player player) {
		return getItem(player, 1);
	}

}

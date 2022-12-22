/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.shop;

import com.google.common.collect.Multimap;
import de.pixel.bedwars.util.I18n;
import de.pixel.bedwars.util.ItemManager;
import de.pixel.bedwars.util.Message;
import eu.darkcube.system.inventoryapi.ItemBuilder;
import net.minecraft.server.v1_8_R3.AttributeModifier;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Locale;
import java.util.Map.Entry;

import static de.pixel.bedwars.shop.Cost.*;
import static de.pixel.bedwars.shop.ShopEnchant.*;
import static org.bukkit.Material.*;
import static eu.darkcube.system.inventoryapi.ItemBuilder.*;

public enum ShopItem {

	S_SANDSTONE_2("S_SANDSTONE_2", item(SANDSTONE).amount(2), BRONZE.of(1)),
	S_GLASS("S_GLASS", item(GLASS), BRONZE.of(1)), S_IRONBLOCK("S_IRONBLOCK", item(IRON_BLOCK), IRON.of(2)),
	S_CHEST("S_CHEST", item(CHEST), IRON.of(1)), S_OBSIDIAN("S_OBSIDIAN", item(OBSIDIAN), GOLD.of(6)),
	S_KNOCKBACK_STICK("S_KNOCKBACK_STICK", item(STICK).enchant(S_KNOCKBACK.getEnchant(), 1), BRONZE.of(12)),
	S_SWORD_1("S_SWORD_1", item(GOLD_SWORD).unbreakable(true).enchant(S_SHARPNESS.getEnchant(), 1), IRON.of(1)),
	S_SWORD_2("S_SWORD_2", item(GOLD_SWORD).unbreakable(true).enchant(S_SHARPNESS.getEnchant(), 2), IRON.of(3)),
	S_SWORD_3("S_SWORD_3",
			item(IRON_SWORD).unbreakable(true).enchant(S_SHARPNESS.getEnchant(), 2).enchant(S_KNOCKBACK.getEnchant(),
					1),
			GOLD.of(3)),
	S_PICKAXE_1("S_PICKAXE_1", item(WOOD_PICKAXE).enchant(S_EFFICIENCY.getEnchant(), 1).unbreakable(true),
			BRONZE.of(4)),
	S_PICKAXE_2("S_PICKAXE_2", item(STONE_PICKAXE).enchant(S_EFFICIENCY.getEnchant(), 1).unbreakable(true), IRON.of(2)),
	S_PICKAXE_3("S_PICKAXE_3", item(IRON_PICKAXE).enchant(S_EFFICIENCY.getEnchant(), 2).unbreakable(true), GOLD.of(1)),
	S_GLASSBREAKER("S_GLASSBREAKER", item(DIAMOND_HOE).glow().unbreakable(true), IRON.of(3)),

	S_SWORDS("S_SWORDS", item(GOLD_SWORD), NONE.of(1), false),
	S_BUILDING_BLOCKS("S_BUILDING_BLOCKS", item(SANDSTONE), NONE.of(1), false),
	S_PICKAXES("S_PICKAXES", item(WOOD_PICKAXE), NONE.of(1), false),

	;

	private static final DecimalFormat format = new DecimalFormat("#.##",
			DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	private final boolean buyable;
	private final Cost cost;
	private final ItemBuilder builder;
	private final String itemId;

	private ShopItem(String itemId, final ItemBuilder builder, final Cost cost) {
		this(itemId, builder, cost, true);
	}

	private ShopItem(String itemId, final ItemBuilder builder, final Cost cost, final boolean buyable) {
		this.itemId = itemId;
		this.builder = builder;
		this.cost = cost;
		this.buyable = buyable;
	}

	public static final ShopItem getItem(String itemid) {
		for (ShopItem item : ShopItem.values()) {
			if (item.itemId.equals(itemid)) {
				return item;
			}
		}
		return null;
	}

	public final ItemStack getItem(final Player p) {
		Locale locale = I18n.getPlayerLanguage(p);
		String msg = I18n.translate(locale, itemId);
		@SuppressWarnings("deprecation")
		ItemBuilder b = new ItemBuilder(builder);
		b.displayname(msg);
		ItemManager.setItemId(b, getItemId());
		b.getUnsafe().setBoolean("shopitem", true);
		// TODO
		// ItemStack item = b.s_build(p);
		ItemStack item = b.build();
		if (this.isBuyable()) {
			double damage = getAttackDamage(item);
			if (damage != 0) {
				b.lore("");
				b.lore(Message.ATTACK_DAMAGE.getMessage(p, format.format(damage)));
			}
			b.lore("");
			b.lore(ChatColor.AQUA + Integer.toString(cost.getCount()) + ' ' + cost.getTranslation().getMessage(p));
		}
//		return b.s_build(p);
		return b.build();
	}

	private double getAttackDamage(ItemStack itemStack) {
		double attackDamage = 0.0;
		net.minecraft.server.v1_8_R3.ItemStack craftItemStack = CraftItemStack.asNMSCopy(itemStack);
		Multimap<String, AttributeModifier> map = craftItemStack.B();
		Collection<AttributeModifier> attributes = map.get(GenericAttributes.ATTACK_DAMAGE.getName());
		if (!attributes.isEmpty()) {

			for (AttributeModifier am : attributes) {
				if (am.c() == 0)
					attackDamage += am.d();
			}
			double y = 1;
			for (AttributeModifier am : attributes) {
				if (am.c() == 1)
					y += am.d();
			}
			attackDamage *= y;
			for (AttributeModifier am : attributes) {
				if (am.c() == 2)
					attackDamage *= (1 + am.d());
			}
		}
		for (Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
			if (entry.getKey() == Enchantment.DAMAGE_ALL) {
				attackDamage += entry.getValue() * 1.25;
			}
		}
		return attackDamage;
	}

	public String getItemId() {
		return getItemId(this);
	}

	public static String getItemId(ShopItem item) {
		return ItemManager.getItemId(item);
	}

	public String getId() {
		return itemId;
	}

	public Cost getCost() {
		return cost;
	}

	public final boolean isBuyable() {
		return buyable;
	}

	public static final int getSlots(int slots) {
		return slots == 0 ? 9 : (slots % 9 == 0 ? slots : (9 * (slots / 9 + 1)));
	}

	public static String getItemId(ItemStack item) {
		return ItemManager.getItemId(item);
	}
}

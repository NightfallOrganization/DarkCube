/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class CustomArmor {



	public static ItemStack getNetherblockHelmetItem() {
		ItemStack item = new ItemStack(Material.NETHERITE_HELMET);
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		meta.addEnchant(Enchantment.DURABILITY, 10, true);

		ComponentLike netherblock =
				Component.text().content("Netherblock").color(TextColor.color(120, 0, 0));
		ComponentLike helmet = Component.text().content("Helmet").color(TextColor.color(80, 0, 0));

		meta.displayName(Component.join(JoinConfiguration.separator(Component.space()), netherblock,
				helmet));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getNetherblockChestplateItem() {
		ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		meta.addEnchant(Enchantment.DURABILITY, 10, true);

		ComponentLike netherblock =
				Component.text().content("Netherblock").color(TextColor.color(120, 0, 0));
		ComponentLike chestplate =
				Component.text().content("Chestplate").color(TextColor.color(80, 0, 0));

		meta.displayName(Component.join(JoinConfiguration.separator(Component.space()), netherblock,
				chestplate));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getNetherblockLeggingsItem() {
		ItemStack itemLeggings = new ItemStack(Material.NETHERITE_LEGGINGS);
		ItemMeta metaLeggings = itemLeggings.getItemMeta();
		metaLeggings.addEnchant(Enchantment.MENDING, 1, true);
		metaLeggings.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		metaLeggings.addEnchant(Enchantment.DURABILITY, 10, true);

		ComponentLike netherblock =
				Component.text().content("Netherblock").color(TextColor.color(120, 0, 0));
		ComponentLike leggings =
				Component.text().content("Leggings").color(TextColor.color(80, 0, 0));

		metaLeggings.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), netherblock,
						leggings));

		itemLeggings.setItemMeta(metaLeggings);
		return itemLeggings;
	}

	public static ItemStack getNetherblockBootsItem() {
		ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		meta.addEnchant(Enchantment.DURABILITY, 10, true);

		ComponentLike netherblock =
				Component.text().content("Netherblock").color(TextColor.color(120, 0, 0));
		ComponentLike boots = Component.text().content("Boots").color(TextColor.color(80, 0, 0));

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), netherblock, boots));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getNetherblockPickaxeItem() {
		ItemStack item = new ItemStack(Material.NETHERITE_PICKAXE);
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.DURABILITY, 10, true);
		meta.addEnchant(Enchantment.DIG_SPEED, 10, true);

		ComponentLike netherblock =
				Component.text().content("Netherblock").color(TextColor.color(120, 0, 0));
		ComponentLike pickaxe =
				Component.text().content("Pickaxe").color(TextColor.color(80, 0, 0));

		meta.displayName(Component.join(JoinConfiguration.separator(Component.space()), netherblock,
				pickaxe));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getStarterSwordItem() {
		ItemStack item = new ItemStack(Material.STONE_SWORD);
		ItemMeta meta = item.getItemMeta();
		((Damageable) meta).setDamage(7800);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
				new AttributeModifier("generic.attackdamage", 10.0,
						AttributeModifier.Operation.MULTIPLY_SCALAR_1));
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
				new AttributeModifier("generic.attackspeed", 50.0,
						AttributeModifier.Operation.MULTIPLY_SCALAR_1));
		meta.setLore(Arrays.asList(" ", "§7§m      §7« §bStats §7»§m      ", " ", "§7Damage §a+55",
				"§7Health §a+90", "§7Healing §a+15", "§7Speed §c-10%", " ",
				"§7§m      §7« §dReqir §7»§7§m      ", " ", "§7Level §a40", "§7Rarity §3Rare", " ",
				"§7§m      §7« §eSmith §7»§7§m      ", " ", "§7Klinge §9Saphir", "§7Segen NONE",
				" "));

		ComponentLike starter = Component.text().content("§7« §fStarter");
		ComponentLike sword = Component.text().content("§7Sword §7»");

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), starter, sword));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getMiningHelmetItem() {
		ItemStack item = new ItemStack(Material.LEATHER_HELMET);
		ItemMeta meta = item.getItemMeta();
		LeatherArmorMeta leatherarmormeta = (LeatherArmorMeta) meta;
		leatherarmormeta.setColor(Color.fromBGR(134, 93, 135));
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.DURABILITY, 100, true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				new AttributeModifier("generic.movementSpeed", 0.5,
						AttributeModifier.Operation.MULTIPLY_SCALAR_1));

		ComponentLike mining =
				Component.text().content("Mining").color(TextColor.color(117, 131, 134));
		ComponentLike helmet =
				Component.text().content("Helmet").color(TextColor.color(77, 91, 94));

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), mining, helmet));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getMiningChestplateItem() {
		ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		LeatherArmorMeta leatherarmormeta = (LeatherArmorMeta) meta;
		leatherarmormeta.setColor(Color.fromBGR(134, 93, 135));
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.DURABILITY, 100, true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				new AttributeModifier("generic.movementSpeed", 0.5,
						AttributeModifier.Operation.MULTIPLY_SCALAR_1));

		ComponentLike mining =
				Component.text().content("Mining").color(TextColor.color(117, 131, 134));
		ComponentLike chestplate =
				Component.text().content("Chestplate").color(TextColor.color(77, 91, 94));

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), mining, chestplate));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getMiningLeggingsItem() {
		ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		LeatherArmorMeta leatherarmormeta = (LeatherArmorMeta) meta;
		leatherarmormeta.setColor(Color.fromBGR(134, 93, 135));
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.DURABILITY, 100, true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				new AttributeModifier("generic.movementSpeed", 0.5,
						AttributeModifier.Operation.MULTIPLY_SCALAR_1));

		ComponentLike mining =
				Component.text().content("Mining").color(TextColor.color(117, 131, 134));
		ComponentLike chestplate =
				Component.text().content("Leggings").color(TextColor.color(77, 91, 94));

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), mining, chestplate));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getMiningBootsItem() {
		ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
		ItemMeta meta = item.getItemMeta();
		LeatherArmorMeta leatherarmormeta = (LeatherArmorMeta) meta;
		leatherarmormeta.setColor(Color.fromBGR(134, 93, 135));
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.DURABILITY, 100, true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		meta.setUnbreakable(true);

		ComponentLike mining =
				Component.text().content("Mining").color(TextColor.color(117, 131, 134));
		ComponentLike chestplate =
				Component.text().content("Boots").color(TextColor.color(77, 91, 94));

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), mining, chestplate));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getSpeedHelmetItem() {
		ItemStack item = new ItemStack(Material.LEATHER_HELMET);
		ItemMeta meta = item.getItemMeta();
		LeatherArmorMeta leatherarmormeta = (LeatherArmorMeta) meta;
		leatherarmormeta.setColor(Color.fromBGR(35, 255, 255));
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.DURABILITY, 100, true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				new AttributeModifier("generic.movementSpeed", 0.5,
						AttributeModifier.Operation.MULTIPLY_SCALAR_1));

		ComponentLike mining =
				Component.text().content("Speed").color(TextColor.color(255, 202, 66));
		ComponentLike helmet =
				Component.text().content("Helmet").color(TextColor.color(222, 171, 40));

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), mining, helmet));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getSpeedChestplateItem() {
		ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		LeatherArmorMeta leatherarmormeta = (LeatherArmorMeta) meta;
		leatherarmormeta.setColor(Color.fromBGR(35, 255, 255));
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.DURABILITY, 100, true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				new AttributeModifier("generic.movementSpeed", 0.5,
						AttributeModifier.Operation.MULTIPLY_SCALAR_1));

		ComponentLike mining =
				Component.text().content("Speed").color(TextColor.color(255, 202, 66));
		ComponentLike chestplate =
				Component.text().content("Chestplate").color(TextColor.color(222, 171, 40));

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), mining, chestplate));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getSpeedLeggingsItem() {
		ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		LeatherArmorMeta leatherarmormeta = (LeatherArmorMeta) meta;
		leatherarmormeta.setColor(Color.fromBGR(35, 255, 255));
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.DURABILITY, 100, true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				new AttributeModifier("generic.movementSpeed", 0.5,
						AttributeModifier.Operation.MULTIPLY_SCALAR_1));

		ComponentLike mining =
				Component.text().content("Speed").color(TextColor.color(255, 202, 66));
		ComponentLike leggings =
				Component.text().content("Leggings").color(TextColor.color(222, 171, 40));

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), mining, leggings));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getSpeedBootsItem() {
		ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
		ItemMeta meta = item.getItemMeta();
		LeatherArmorMeta leatherarmormeta = (LeatherArmorMeta) meta;
		leatherarmormeta.setColor(Color.fromBGR(35, 255, 255));
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addEnchant(Enchantment.DURABILITY, 100, true);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,
				new AttributeModifier("generic.movementSpeed", 0.5,
						AttributeModifier.Operation.MULTIPLY_SCALAR_1));

		ComponentLike mining =
				Component.text().content("Speed").color(TextColor.color(255, 202, 66));
		ComponentLike boots =
				Component.text().content("Boots").color(TextColor.color(222, 171, 40));

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), mining, boots));

		item.setItemMeta(meta);
		return item;
	}

	/*public static ItemStack getEsdeathBootsItem() {
		ItemBuilder item =
				ItemBuilder.item(Material.LEATHER_BOOTS).enchant(Enchantment.FROST_WALKER, 30)
						.enchant(Enchantment.MENDING, 1).enchant(Enchantment.DURABILITY, 100)
						.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10)
						.meta(new LeatherArmorBuilderMeta(Color.fromRGB(100, 100, 0)));

		eu.darkcube.system.libs.net.kyori.adventure.text.Component esdeath =
				eu.darkcube.system.libs.net.kyori.adventure.text.Component.text().content("Esdeath")
						.color(eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor.color(
								86, 207, 255)).build();
		eu.darkcube.system.libs.net.kyori.adventure.text.Component boots =
				eu.darkcube.system.libs.net.kyori.adventure.text.Component.text().content("Boots")
						.color(eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor.color(
								73, 177, 218)).build();
		item.displayname(eu.darkcube.system.libs.net.kyori.adventure.text.Component.join(
				eu.darkcube.system.libs.net.kyori.adventure.text.JoinConfiguration.separator(
						eu.darkcube.system.libs.net.kyori.adventure.text.Component.space()),
				esdeath, boots));

		item.unbreakable(true);
		return item.build();
	}
 */

	public static ItemStack getGUI1() {
		ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta meta = item.getItemMeta();
		((Damageable) meta).setDamage(3);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		meta.setUnbreakable(true);

		ComponentLike starter = Component.text().content("");
		ComponentLike sword = Component.text().content("");

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), starter, sword));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getGUI2() {
		ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta meta = item.getItemMeta();
		((Damageable) meta).setDamage(2);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		meta.setUnbreakable(true);

		ComponentLike starter = Component.text().content("");
		ComponentLike sword = Component.text().content("");

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), starter, sword));

		item.setItemMeta(meta);
		return item;
	}


	public static ItemStack getS1() {
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = item.getItemMeta();
		((Damageable) meta).setDamage(1);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);

		meta.setLore(Arrays.asList(" ", "§7§m      §7« §bStats §7»§m      ", " ", "§7Damage §a+55",
				"§7Health §a+90", "§7Healing §a+15", "§7Speed §c-10%", " ",
				"§7§m      §7« §dReqir §7»§7§m      ", " ", "§7Level §a40", "§7Rarity §3Rare", " ",
				"§7§m      §7« §eSmith §7»§7§m      ", " ", "§7Klinge §9Saphir", "§7Segen NONE",
				" "));

		ComponentLike starter = Component.text().content("§7« §fSword");
		ComponentLike sword = Component.text().content("§7One §7»");

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), starter, sword));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getS2() {
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = item.getItemMeta();
		((Damageable) meta).setDamage(2);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);

		meta.setLore(Arrays.asList(" ", "§7§m      §7« §bStats §7»§m      ", " ", "§7Damage §a+55",
				"§7Health §a+90", "§7Healing §a+15", "§7Speed §c-10%", " ",
				"§7§m      §7« §dReqir §7»§7§m      ", " ", "§7Level §a40", "§7Rarity §3Rare", " ",
				"§7§m      §7« §eSmith §7»§7§m      ", " ", "§7Klinge §9Saphir", "§7Segen NONE",
				" "));

		ComponentLike starter = Component.text().content("§7« §fSword");
		ComponentLike sword = Component.text().content("§7Two §7»");

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), starter, sword));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getS3() {
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = item.getItemMeta();
		((Damageable) meta).setDamage(3);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);

		meta.setLore(Arrays.asList(" ", "§7§m      §7« §bStats §7»§m      ", " ", "§7Damage §a+55",
				"§7Health §a+90", "§7Healing §a+15", "§7Speed §c-10%", " ",
				"§7§m      §7« §dReqir §7»§7§m      ", " ", "§7Level §a40", "§7Rarity §3Rare", " ",
				"§7§m      §7« §eSmith §7»§7§m      ", " ", "§7Klinge §9Saphir", "§7Segen NONE",
				" "));

		ComponentLike starter = Component.text().content("§7« §fSword");
		ComponentLike sword = Component.text().content("§7Three §7»");

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), starter, sword));

		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getS4() {
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = item.getItemMeta();
		((Damageable) meta).setDamage(4);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);

		meta.setLore(Arrays.asList(" ", "§7§m      §7« §bStats §7»§m      ", " ", "§7Damage §a+55",
				"§7Health §a+90", "§7Healing §a+15", "§7Speed §c-10%", " ",
				"§7§m      §7« §dReqir §7»§7§m      ", " ", "§7Level §a40", "§7Rarity §3Rare", " ",
				"§7§m      §7« §eSmith §7»§7§m      ", " ", "§7Klinge §9Saphir", "§7Segen NONE",
				" "));

		ComponentLike starter = Component.text().content("§7« §fSword");
		ComponentLike sword = Component.text().content("§7Four §7»");

		meta.displayName(
				Component.join(JoinConfiguration.separator(Component.space()), starter, sword));

		item.setItemMeta(meta);
		return item;
	}
}

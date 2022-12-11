/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import static org.bukkit.Material.*;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;

public enum Item {

	LOBBY_TEAMS(ItemBuilder.item(BOOK).addLore()),

	LOBBY_PERKS(ItemBuilder.item(BOW).glow().addLore()),

	LOBBY_VOTING(ItemBuilder.item(PAPER).addLore()),
	LOBBY_VOTING_MAPS(ItemBuilder.item(PAPER).addLore()),
	LOBBY_VOTING_EP_GLITCH(ItemBuilder.item(ENDER_PEARL).addLore()),
	LOBBY_VOTING_LIFES(ItemBuilder.item(NAME_TAG).addLore()),

	GENERAL_VOTING_FOR(ItemBuilder.item(INK_SACK).setDurability(2).addLore()),
	GENERAL_VOTING_AGAINST(ItemBuilder.item(INK_SACK).setDurability(1).addLore()),

	SETTINGS(ItemBuilder.item(REDSTONE_COMPARATOR).addLore()),
	SETTINGS_WOOL_DIRECTION(ItemBuilder.item(WOOL).addLore()),
	SETTINGS_HEIGHT_DISPLAY(ItemBuilder.item(CARPET).addLore()),
	SETTINGS_HEIGHT_DISPLAY_COLOR(ItemBuilder.item(WOOL).setDurability(2).addLore()),
	SETTINGS_WOOL_DIRECTION_LEFT_TO_RIGHT(
			ItemBuilder.item(WOOL).setDurability(5).addLore()),
	SETTINGS_WOOL_DIRECTION_RIGHT_TO_LEFT(
			ItemBuilder.item(WOOL).setDurability(14).addLore()),

	LOBBY_PERKS_1(ItemBuilder.item(CHEST).addLore()),
	LOBBY_PERKS_2(ItemBuilder.item(CHEST).addLore()),
	LOBBY_PERKS_3(ItemBuilder.item(ENDER_CHEST).addLore()),

	PERK_CAPSULE(ItemBuilder.item(STAINED_GLASS).setDurability(14).addLore()),
	PERK_CAPSULE_COOLDOWN(ItemBuilder.item(STAINED_GLASS).addLore()),

	PERK_SWITCHER(ItemBuilder.item(SNOW_BALL).addLore()),
	PERK_SWITCHER_COOLDOWN(ItemBuilder.item(SNOW_BALL).addLore()),

	PERK_LINE_BUILDER(ItemBuilder.item(STICK).addLore()),
	PERK_LINE_BUILDER_COOLDOWN(ItemBuilder.item(STICK).addLore()),

	PERK_SLIME_PLATFORM(ItemBuilder.item(SLIME_BALL).addLore()),
	PERK_SLIME_PLATFORM_COOLDOWN(ItemBuilder.item(SLIME_BALL).addLore()),

	PERK_WOOL_BOMB(ItemBuilder.item(TNT).addLore()),
	PERK_WOOL_BOMB_COOLDOWN(ItemBuilder.item(TNT).addLore()),

	PERK_EXTRA_WOOL(ItemBuilder.item(CHEST).addLore()),

//	PERK_DOUBLE_WOOL(item(SUGAR_CANE).addLore()),

//	PERK_BACKPACK(item(CHEST).addLore()),

	PERK_LONGJUMP(ItemBuilder.item(RABBIT_FOOT).addLore()),

	PERK_ROCKETJUMP(ItemBuilder.item(DIAMOND_BOOTS).addLore()),

	PERK_ARROW_RAIN(ItemBuilder.item(DISPENSER).addLore()),

	PERK_ARROW_RAIN_COOLDOWN(ItemBuilder.item(DISPENSER).addLore()),

	PERK_RONJAS_TOILET_SPLASH(ItemBuilder.item(POTION).addLore()),
	PERK_RONJAS_TOILET_SPLASH_COOLDOWN(ItemBuilder.item(GLASS_BOTTLE).addLore()),

	PERK_BLINK_COOLDOWN(ItemBuilder.item(ENDER_PEARL).addLore()),

	PERK_BLINK(ItemBuilder.item(EYE_OF_ENDER).addLore()),

	PERK_SAFETY_PLATFORM(ItemBuilder.item(BLAZE_ROD).addLore()),

	PERK_SAFETY_PLATFORM_COOLDOWN(ItemBuilder.item(STICK).addLore()),

	PERK_WALL_GENERATOR(ItemBuilder.item(STAINED_GLASS_PANE).setDurability(14).addLore()),

	PERK_WALL_GENERATOR_COOLDOWN(ItemBuilder.item(STAINED_GLASS_PANE).addLore()),

	PERK_GRANDPAS_CLOCK(ItemBuilder.item(WATCH).addLore()),

	PERK_GRANDPAS_CLOCK_COOLDOWN(ItemBuilder.item(WATCH).addLore()),

	PERK_GHOST(ItemBuilder.item(GHAST_TEAR).addLore()),

	PERK_GHOST_COOLDOWN(ItemBuilder.item(SULPHUR).addLore()),

	PERK_MINIGUN(ItemBuilder.item(DIAMOND_BARDING).addLore()),

	PERK_MINIGUN_COOLDOWN(ItemBuilder.item(DIAMOND_BARDING).addLore()),

	PERK_GRABBER(ItemBuilder.item(STICK).addLore()),

	PERK_GRABBER_GRABBED(ItemBuilder.item(BLAZE_ROD).addLore()),

	PERK_GRABBER_COOLDOWN(ItemBuilder.item(STICK).addLore()),

	PERK_BOOSTER(ItemBuilder.item(FEATHER).addLore()),

	PERK_BOOSTER_COOLDOWN(ItemBuilder.item(FEATHER).addLore()),

	PERK_FAST_ARROW(ItemBuilder.item(SUGAR).addLore()),

	PERK_TNT_ARROW(ItemBuilder.item(TNT).addLore()),

	PERK_TNT_ARROW_COOLDOWN(ItemBuilder.item(TNT).addLore()),

	PERK_GRAPPLING_HOOK(
			ItemBuilder.item(FISHING_ROD).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE).addLore()),

	PERK_GRAPPLING_HOOK_COOLDOWN(ItemBuilder.item(STICK).addLore()),

	PERK_ROPE(ItemBuilder.item(VINE).addLore()),

	PERK_ROPE_COOLDOWN(ItemBuilder.item(VINE).addLore()),

	DEFAULT_BOW(ItemBuilder.item(BOW)
			.addEnchant(Enchantment.ARROW_INFINITE, 1)
			.addEnchant(Enchantment.ARROW_KNOCKBACK, 2)
			.addEnchant(Enchantment.KNOCKBACK, 5)
			.addLore()
			.addFlag(ItemFlag.HIDE_UNBREAKABLE)
			.setUnbreakable(true)),
	DEFAULT_SHEARS(ItemBuilder.item(SHEARS)
			.addEnchant(Enchantment.KNOCKBACK, 5)
			.addEnchant(Enchantment.DIG_SPEED, 5)
			.setUnbreakable(true)
			.addFlag(ItemFlag.HIDE_UNBREAKABLE)
			.addLore()),
	DEFAULT_PEARL(ItemBuilder.item(ENDER_PEARL).glow().addLore()),
	DEFAULT_ARROW(ItemBuilder.item(ARROW).addLore()),
	DEFAULT_PEARL_COOLDOWN(ItemBuilder.item(FIREWORK_CHARGE).addLore()),

	ARMOR_LEATHER_BOOTS(
			ItemBuilder.item(LEATHER_BOOTS).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)),
	ARMOR_LEATHER_LEGGINGS(
			ItemBuilder.item(LEATHER_LEGGINGS).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)),
	ARMOR_LEATHER_CHESTPLATE(
			ItemBuilder.item(LEATHER_CHESTPLATE).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)),
	ARMOR_LEATHER_HELMET(
			ItemBuilder.item(LEATHER_HELMET).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)),

	TELEPORT_COMPASS(ItemBuilder.item(COMPASS).glow().addLore()),

	LOBBY_PARTICLES_ON(ItemBuilder.item(BLAZE_ROD).addLore()),
	LOBBY_PARTICLES_OFF(ItemBuilder.item(BLAZE_ROD).addLore()),

	HEIGHT_DISPLAY_ON(ItemBuilder.item(STAINED_CLAY).setDurability(5).addLore()),
	HEIGHT_DISPLAY_OFF(ItemBuilder.item(STAINED_CLAY).setDurability(14).addLore()),

	;

	private final ItemBuilder builder;

	private final String key = this.name();

	private PerkType perk;

	Item(ItemBuilder builder) {
		this.builder = builder;
	}

	public ItemBuilder getBuilder() {
		return new ItemBuilder(this.builder);
	}

	public String getDisplayName(User user) {
		return this.getDisplayName(user, new Object[0]);
	}

	public PerkType getPerk() {
		return this.perk;
	}

	public void setPerk(PerkType perk) {
		this.perk = perk;
	}

	public String getItemId() {
		return ItemManager.getItemId(this);
	}

	public String getKey() {
		return this.key;
	}

	public String getDisplayName(User user, Object... replacements) {
		return ItemManager.getDisplayName(this, user, replacements);
	}

	public ItemStack getItem(User user) {
		return ItemManager.getItem(this, user);
	}

	public ItemStack getItem(User user, Object... replacements) {
		return ItemManager.getItem(this, user, replacements);
	}

	public ItemStack getItem(User user, Object[] replacements, Object... loreReplacements) {
		return ItemManager.getItem(this, user, replacements, loreReplacements);
	}

}

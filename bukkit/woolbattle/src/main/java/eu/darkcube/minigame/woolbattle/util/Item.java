package eu.darkcube.minigame.woolbattle.util;

import static eu.darkcube.minigame.woolbattle.util.ItemBuilder.*;
import static org.bukkit.Material.*;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;

public enum Item {

	LOBBY_TEAMS(item(BOOK).addLore()),

	LOBBY_PERKS(item(BOW).glow().addLore()),

	LOBBY_VOTING(item(PAPER).addLore()),
	LOBBY_VOTING_MAPS(item(PAPER).addLore()),
	LOBBY_VOTING_EP_GLITCH(item(ENDER_PEARL).addLore()),
	LOBBY_VOTING_LIFES(item(NAME_TAG).addLore()),

	GENERAL_VOTING_FOR(item(INK_SACK).setDurability(2).addLore()),
	GENERAL_VOTING_AGAINST(item(INK_SACK).setDurability(1).addLore()),

	SETTINGS(item(REDSTONE_COMPARATOR).addLore()),
	SETTINGS_WOOL_DIRECTION(item(WOOL).addLore()),
	SETTINGS_HEIGHT_DISPLAY(item(CARPET).addLore()),
	SETTINGS_HEIGHT_DISPLAY_COLOR(item(WOOL).setDurability(2).addLore()),
	SETTINGS_WOOL_DIRECTION_LEFT_TO_RIGHT(
					item(WOOL).setDurability(5).addLore()),
	SETTINGS_WOOL_DIRECTION_RIGHT_TO_LEFT(
					item(WOOL).setDurability(14).addLore()),

	LOBBY_GADGET(item(ENDER_CHEST).glow().addLore()),

	LOBBY_PERKS_1(item(CHEST).addLore()),
	LOBBY_PERKS_2(item(CHEST).addLore()),
	LOBBY_PERKS_3(item(ENDER_CHEST).addLore()),

	PERK_CAPSULE(item(STAINED_GLASS).setDurability(14).addLore()),
	PERK_CAPSULE_COOLDOWN(item(STAINED_GLASS).addLore()),

	PERK_SWITCHER(item(SNOW_BALL).addLore()),
	PERK_SWITCHER_COOLDOWN(item(SNOW_BALL).addLore()),

	PERK_LINE_BUILDER(item(STICK).addLore()),
	PERK_LINE_BUILDER_COOLDOWN(item(STICK).addLore()),

	PERK_WOOL_BOMB(item(TNT).addLore()),
	PERK_WOOL_BOMB_COOLDOWN(item(TNT).addLore()),

	PERK_EXTRA_WOOL(item(CHEST).addLore()),

//	PERK_DOUBLE_WOOL(item(SUGAR_CANE).addLore()),

//	PERK_BACKPACK(item(CHEST).addLore()),

	PERK_LONGJUMP(item(RABBIT_FOOT).addLore()),

	PERK_ROCKETJUMP(item(DIAMOND_BOOTS).addLore()),

	PERK_ARROW_RAIN(item(DISPENSER).addLore()),

	PERK_ARROW_RAIN_COOLDOWN(item(DISPENSER).addLore()),

	PERK_RONJAS_TOILET_SPLASH(item(POTION).addLore()),
	PERK_RONJAS_TOILET_SPLASH_COOLDOWN(item(GLASS_BOTTLE).addLore()),

	PERK_BLINK_COOLDOWN(item(ENDER_PEARL).addLore()),

	PERK_BLINK(item(EYE_OF_ENDER).addLore()),

	PERK_SAFETY_PLATFORM(item(BLAZE_ROD).addLore()),

	PERK_SAFETY_PLATFORM_COOLDOWN(item(STICK).addLore()),

	PERK_WALL_GENERATOR(item(STAINED_GLASS_PANE).setDurability(14).addLore()),

	PERK_WALL_GENERATOR_COOLDOWN(item(STAINED_GLASS_PANE).addLore()),

	PERK_GRANDPAS_CLOCK(item(WATCH).addLore()),

	PERK_GRANDPAS_CLOCK_COOLDOWN(item(WATCH).addLore()),

	PERK_GHOST(item(GHAST_TEAR).addLore()),

	PERK_GHOST_COOLDOWN(item(SULPHUR).addLore()),

	PERK_MINIGUN(item(DIAMOND_BARDING).addLore()),

	PERK_MINIGUN_COOLDOWN(item(DIAMOND_BARDING).addLore()),

	PERK_GRABBER(item(STICK).addLore()),

	PERK_GRABBER_GRABBED(item(BLAZE_ROD).addLore()),

	PERK_GRABBER_COOLDOWN(item(STICK).addLore()),

	PERK_BOOSTER(item(FEATHER).addLore()),

	PERK_BOOSTER_COOLDOWN(item(FEATHER).addLore()),

	PERK_FAST_ARROW(item(SUGAR).addLore()),

	PERK_TNT_ARROW(item(TNT).addLore()),

	PERK_TNT_ARROW_COOLDOWN(item(TNT).addLore()),
	
	PERK_GRAPPLING_HOOK(item(FISHING_ROD).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE).addLore()),
	
	PERK_GRAPPLING_HOOK_COOLDOWN(item(STICK).addLore()),
	
	PERK_ROPE(item(VINE).addLore()),

	PERK_ROPE_COOLDOWN(item(VINE).addLore()),

	DEFAULT_BOW(item(BOW).addEnchant(Enchantment.ARROW_INFINITE, 1).addEnchant(Enchantment.ARROW_KNOCKBACK, 2).addEnchant(Enchantment.KNOCKBACK, 5).addLore().addFlag(ItemFlag.HIDE_UNBREAKABLE).setUnbreakable(true)),
	DEFAULT_SHEARS(item(SHEARS).addEnchant(Enchantment.KNOCKBACK, 5).addEnchant(Enchantment.DIG_SPEED, 5).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE).addLore()),
	DEFAULT_PEARL(item(ENDER_PEARL).glow().addLore()),
	DEFAULT_ARROW(item(ARROW).addLore()),
	DEFAULT_PEARL_COOLDOWN(item(FIREWORK_CHARGE).addLore()),

	ARMOR_LEATHER_BOOTS(
					item(LEATHER_BOOTS).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)),
	ARMOR_LEATHER_LEGGINGS(
					item(LEATHER_LEGGINGS).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)),
	ARMOR_LEATHER_CHESTPLATE(
					item(LEATHER_CHESTPLATE).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)),
	ARMOR_LEATHER_HELMET(
					item(LEATHER_HELMET).setUnbreakable(true).addFlag(ItemFlag.HIDE_UNBREAKABLE)),

	TELEPORT_COMPASS(item(COMPASS).glow().addLore()),

	LOBBY_PARTICLES_ON(item(BLAZE_ROD).addLore()),
	LOBBY_PARTICLES_OFF(item(BLAZE_ROD).addLore()),

	HEIGHT_DISPLAY_ON(item(STAINED_CLAY).setDurability(5).addLore()),
	HEIGHT_DISPLAY_OFF(item(STAINED_CLAY).setDurability(14).addLore()),



	;

	private final ItemBuilder builder;
	private final String key = name();
	private PerkType perk;

	Item(ItemBuilder builder) {
		this.builder = builder;
	}

	public ItemBuilder getBuilder() {
		return new ItemBuilder(builder);
	}

	public String getDisplayName(User user) {
		return getDisplayName(user, new Object[0]);
	}

	public PerkType getPerk() {
		return perk;
	}

	public void setPerk(PerkType perk) {
		this.perk = perk;
	}

	public String getItemId() {
		return ItemManager.getItemId(this);
	}

	public String getKey() {
		return key;
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

	public ItemStack getItem(User user, Object[] replacements,
					Object... loreReplacements) {
		return ItemManager.getItem(this, user, replacements, loreReplacements);
	}
}

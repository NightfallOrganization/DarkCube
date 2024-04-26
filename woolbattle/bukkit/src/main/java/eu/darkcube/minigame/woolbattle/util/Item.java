/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import static eu.darkcube.system.server.item.ItemBuilder.item;
import static org.bukkit.Material.*;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.meta.SpawnEggBuilderMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public enum Item {

    LOBBY_TEAMS(item(BOOK)),
    LOBBY_PERKS(item(BOW).glow(true)),
    LOBBY_VOTING(item(PAPER)),
    LOBBY_VOTING_MAPS(item(PAPER)),
    LOBBY_VOTING_EP_GLITCH(item(ENDER_PEARL)),
    LOBBY_VOTING_LIFES(item(NAME_TAG)),
    LOBBY_VOTING_LIFES_ENTRY(item(NAME_TAG)),
    GENERAL_VOTING_FOR(item(INK_SACK).damage(2)),
    GENERAL_VOTING_AGAINST(item(INK_SACK).damage(1)),
    SETTINGS(item(REDSTONE_COMPARATOR)),
    SETTINGS_WOOL_DIRECTION(item(WOOL)),
    SETTINGS_HEIGHT_DISPLAY(item(CARPET)),
    SETTINGS_HEIGHT_DISPLAY_COLOR(item(WOOL).damage(2)),
    SETTINGS_WOOL_DIRECTION_LEFT_TO_RIGHT(item(WOOL).damage(5)),
    SETTINGS_WOOL_DIRECTION_RIGHT_TO_LEFT(item(WOOL).damage(14)),
    PERKS_MISC(item(BARRIER)),
    PERKS_ACTIVE(item(CHEST)),
    PERKS_PASSIVE(item(ENDER_CHEST)),
    PERK_CAPSULE(item(STAINED_GLASS).damage(14)),
    PERK_CAPSULE_COOLDOWN(item(STAINED_GLASS)),
    PERK_SWITCHER(item(SNOW_BALL)),
    PERK_SWITCHER_COOLDOWN(item(SNOW_BALL)),
    PERK_LINE_BUILDER(item(STICK)),
    PERK_LINE_BUILDER_COOLDOWN(item(STICK)),
    PERK_SLIME_PLATFORM(item(SLIME_BALL)),
    PERK_SLIME_PLATFORM_COOLDOWN(item(SLIME_BALL)),
    PERK_WOOL_BOMB(item(TNT)),
    PERK_WOOL_BOMB_COOLDOWN(item(TNT)),
    PERK_EXTRA_WOOL(item(CHEST)),
    PERK_LONGJUMP(item(RABBIT_FOOT)),
    PERK_ROCKETJUMP(item(DIAMOND_BOOTS)),
    PERK_ARROW_RAIN(item(DISPENSER)),
    PERK_ARROW_RAIN_COOLDOWN(item(DISPENSER)),
    PERK_RONJAS_TOILET_SPLASH(item(POTION)),
    PERK_RONJAS_TOILET_SPLASH_COOLDOWN(item(GLASS_BOTTLE)),
    PERK_BLINK_COOLDOWN(item(ENDER_PEARL)),
    PERK_BLINK(item(EYE_OF_ENDER)),
    PERK_SAFETY_PLATFORM(item(BLAZE_ROD)),
    PERK_SAFETY_PLATFORM_COOLDOWN(item(STICK)),
    PERK_WALL_GENERATOR(item(STAINED_GLASS_PANE).damage(14)),
    PERK_WALL_GENERATOR_COOLDOWN(item(STAINED_GLASS_PANE)),
    PERK_GRANDPAS_CLOCK(item(WATCH)),
    PERK_GRANDPAS_CLOCK_COOLDOWN(item(WATCH)),
    PERK_GHOST(item(GHAST_TEAR)),
    PERK_GHOST_COOLDOWN(item(SULPHUR)),
    PERK_MINE(item(STONE_PLATE)),
    PERK_MINE_COOLDOWN(item(STONE_PLATE)),
    PERK_MINIGUN(item(DIAMOND_BARDING)),
    PERK_MINIGUN_COOLDOWN(item(DIAMOND_BARDING)),
    PERK_GRABBER(item(STICK)),
    PERK_GRABBER_GRABBED(item(BLAZE_ROD)),
    PERK_GRABBER_COOLDOWN(item(STICK)),
    PERK_BOOSTER(item(FEATHER)),
    PERK_BOOSTER_COOLDOWN(item(FEATHER)),
    PERK_FAST_ARROW(item(SUGAR)),
    PERK_PIERCING_ARROW(item(GOLD_AXE).unbreakable(true).flag(ItemFlag.HIDE_UNBREAKABLE)),
	PERK_KNOCKBACK_ARROW(item(ENCHANTED_BOOK)),PERK_TNT_ARROW(item(TNT)),
    PERK_TNT_ARROW_COOLDOWN(item(TNT)),
    PERK_ARROW_BOMB(item(FIREWORK_CHARGE)),
    PERK_ARROW_BOMB_COOLDOWN(item(FIREWORK_CHARGE)),
    PERK_REFLECTOR(item(CACTUS)),
    PERK_REFLECTOR_COOLDOWN(item(CACTUS)),
    PERK_FREEZER(item(ICE)),
    PERK_FREEZER_COOLDOWN(item(ICE)),
    PERK_PROTECTIVE_SHIELD(item(EMERALD)),
    PERK_PROTECTIVE_SHIELD_COOLDOWN(item(EMERALD)),
    PERK_GRAPPLING_HOOK(item(FISHING_ROD).unbreakable(true).flag(ItemFlag.HIDE_UNBREAKABLE)),
    PERK_GRAPPLING_HOOK_COOLDOWN(item(STICK)),
    PERK_ELEVATOR(item(PISTON_BASE)),
    PERK_ELEVATOR_COOLDOWN(item(PISTON_BASE)),
    PERK_ROPE(item(VINE)),
    PERK_ROPE_COOLDOWN(item(VINE)),
    PERK_STOMPER(item(DIAMOND_BOOTS)),
    PERK_HOOK_ARROW(item(REDSTONE_TORCH_ON)),
    PERK_HOOK_ARROW_COOLDOWN(item(REDSTONE_TORCH_ON)),
    DEFAULT_BOW(item(BOW)
            .enchant(Enchantment.ARROW_INFINITE, 1)
            .enchant(Enchantment.ARROW_KNOCKBACK, 2)
            .enchant(Enchantment.KNOCKBACK, 5)
            .flag(ItemFlag.HIDE_UNBREAKABLE)
            .unbreakable(true)),
    DEFAULT_SHEARS(item(SHEARS)
            .enchant(Enchantment.KNOCKBACK, 5)
            .enchant(Enchantment.DIG_SPEED, 5)
            .unbreakable(true)
            .flag(ItemFlag.HIDE_UNBREAKABLE)),
    DEFAULT_PEARL(item(ENDER_PEARL).glow(true)),
    DEFAULT_ARROW(item(ARROW)),
    DEFAULT_PEARL_COOLDOWN(item(FIREWORK_CHARGE)),
    ARMOR_LEATHER_BOOTS(item(LEATHER_BOOTS).unbreakable(true).flag(ItemFlag.HIDE_UNBREAKABLE)),
    ARMOR_LEATHER_LEGGINGS(item(LEATHER_LEGGINGS).unbreakable(true).flag(ItemFlag.HIDE_UNBREAKABLE)),
    ARMOR_LEATHER_CHESTPLATE(item(LEATHER_CHESTPLATE).unbreakable(true).flag(ItemFlag.HIDE_UNBREAKABLE)),
    ARMOR_LEATHER_HELMET(item(LEATHER_HELMET).unbreakable(true).flag(ItemFlag.HIDE_UNBREAKABLE)),
    TELEPORT_COMPASS(item(COMPASS).glow(true)),
    LOBBY_PARTICLES_ON(item(BLAZE_ROD)),
    LOBBY_PARTICLES_OFF(item(BLAZE_ROD)),
    HEIGHT_DISPLAY_ON(item(STAINED_CLAY).damage(5)),
    HEIGHT_DISPLAY_OFF(item(STAINED_CLAY).damage(14)),
    NEXT_PAGE(item(STAINED_GLASS_PANE).damage(5)),
    PREV_PAGE(item(STAINED_GLASS_PANE).damage(5)),
    NEXT_PAGE_UNUSABLE(item(STAINED_GLASS_PANE).damage(14)),
    PREV_PAGE_UNUSABLE(item(STAINED_GLASS_PANE).damage(14)),
    PERK_SCAMP(item(GOLD_INGOT)),
    PERK_SPIDER(item(MONSTER_EGG).meta(new SpawnEggBuilderMeta("{id:\"Spider\"}")).damage(52)),
    PERK_SPIDER_COOLDOWN(item(MONSTER_EGG).meta(new SpawnEggBuilderMeta("{id:\"Spider\"}")).damage(52)),
    PERK_DRAW_ARROW(item(TORCH)),
    PERK_DRAW_ARROW_COOLDOWN(item(TORCH)),
    PERK_FREEZE_ARROW(item(ARROW)),
    PERK_FREEZE_ARROW_COOLDOWN(item(ARROW)),
    PERK_BERSERKER(item(DIAMOND_SWORD)),
    ;

    private final ItemBuilder builder;

    private final String key = this.name();

    Item(ItemBuilder builder) {
        this.builder = builder;
    }

    public ItemBuilder getBuilder() {
        return this.builder.clone();
    }

    public String getItemId() {
        return ItemManager.getItemId(this);
    }

    public String getKey() {
        return this.key;
    }

    public ItemStack getItem(WBUser user) {
        return ItemManager.getItem(this, user);
    }

    public ItemStack getItem(WBUser user, Object... replacements) {
        return ItemManager.getItem(this, user, replacements);
    }

    public ItemStack getItem(WBUser user, Object[] replacements, Object... loreReplacements) {
        return ItemManager.getItem(this, user, replacements, loreReplacements);
    }
}

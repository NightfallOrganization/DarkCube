/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.util.item;

import static eu.darkcube.system.server.item.ItemBuilder.item;
import static eu.darkcube.system.server.item.component.ItemComponent.*;
import static net.minestom.server.item.Material.*;
import static net.minestom.server.item.enchant.Enchantment.*;

import java.util.List;

import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.components.AttributeList;
import eu.darkcube.system.server.item.component.components.DyedItemColor;
import eu.darkcube.system.server.item.component.components.Tool;
import eu.darkcube.system.server.item.component.components.util.BlockTypeFilter;
import eu.darkcube.system.util.Color;

public class MinestomItemsProvider implements Items.Provider {
    private final MinestomWoolBattle woolbattle;

    public MinestomItemsProvider(MinestomWoolBattle woolbattle) {
        this.woolbattle = woolbattle;
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public ItemBuilder builder(Items items) {
        return switch (items) {
            case LOBBY_TEAMS -> item(BOOK);
            case LOBBY_PERKS -> item(BOW).glow(true);
            case LOBBY_VOTING -> item(PAPER);
            case LOBBY_VOTING_MAPS -> item(PAPER);
            case LOBBY_VOTING_EP_GLITCH -> item(ENDER_PEARL);
            case LOBBY_VOTING_LIFES -> item(NAME_TAG);
            case LOBBY_VOTING_LIFES_ENTRY -> item(NAME_TAG);
            case GENERAL_VOTING_FOR -> item(GREEN_DYE);
            case GENERAL_VOTING_AGAINST -> item(RED_DYE);
            case SETTINGS -> item(COMPARATOR);
            case SETTINGS_WOOL_DIRECTION -> item(WHITE_WOOL);
            case SETTINGS_HEIGHT_DISPLAY -> item(WHITE_CARPET);
            case SETTINGS_HEIGHT_DISPLAY_COLOR -> item(MAGENTA_WOOL);
            case SETTINGS_WOOL_DIRECTION_LEFT_TO_RIGHT -> item(LIME_WOOL);
            case SETTINGS_WOOL_DIRECTION_RIGHT_TO_LEFT -> item(RED_WOOL);
            case PERKS_MISC -> item(BARRIER);
            case PERKS_ACTIVE -> item(CHEST);
            case PERKS_PASSIVE -> item(ENDER_CHEST);
            case PERK_CAPSULE -> item(RED_STAINED_GLASS);
            case PERK_CAPSULE_COOLDOWN -> item(WHITE_STAINED_GLASS);
            case PERK_SWITCHER -> item(SNOWBALL);
            case PERK_SWITCHER_COOLDOWN -> item(SNOWBALL);
            case PERK_LINE_BUILDER -> item(STICK);
            case PERK_LINE_BUILDER_COOLDOWN -> item(STICK);
            case PERK_SLIME_PLATFORM -> item(SLIME_BALL);
            case PERK_SLIME_PLATFORM_COOLDOWN -> item(SLIME_BALL);
            case PERK_WOOL_BOMB -> item(TNT);
            case PERK_WOOL_BOMB_COOLDOWN -> item(TNT);
            case PERK_EXTRA_WOOL -> item(CHEST);
            case PERK_PIERCING_ARROW -> item(GOLDEN_AXE).hiddenUnbreakable();
            case PERK_KNOCKBACK_ARROW -> item(ENCHANTED_BOOK).glow(false);
            case PERK_LONGJUMP -> item(RABBIT_FOOT);
            case PERK_ROCKETJUMP -> item(DIAMOND_BOOTS);
            case PERK_ARROW_RAIN -> item(DISPENSER);
            case PERK_ARROW_RAIN_COOLDOWN -> item(DISPENSER);
            case PERK_RONJAS_TOILET_SPLASH -> item(POTION);
            case PERK_RONJAS_TOILET_SPLASH_COOLDOWN -> item(GLASS_BOTTLE);
            case PERK_BLINK -> item(ENDER_EYE);
            case PERK_BLINK_COOLDOWN -> item(ENDER_PEARL);
            case PERK_SAFETY_PLATFORM -> item(BLAZE_ROD);
            case PERK_SAFETY_PLATFORM_COOLDOWN -> item(STICK);
            case PERK_WALL_GENERATOR -> item(RED_STAINED_GLASS_PANE);
            case PERK_WALL_GENERATOR_COOLDOWN -> item(WHITE_STAINED_GLASS_PANE);
            case PERK_GRANDPAS_CLOCK -> item(CLOCK);
            case PERK_GRANDPAS_CLOCK_COOLDOWN -> item(CLOCK);
            case PERK_GHOST -> item(GHAST_TEAR);
            case PERK_GHOST_COOLDOWN -> item(GUNPOWDER);
            case PERK_MINE, PERK_MINE_COOLDOWN -> item(STONE_PRESSURE_PLATE);
            case PERK_MINIGUN, PERK_MINIGUN_COOLDOWN -> item(DIAMOND_HORSE_ARMOR);
            case PERK_POD -> item(FLOWER_POT);
            case PERK_POD_COOLDOWN -> item(FIREWORK_STAR);
            case PERK_GRABBER, PERK_GRABBER_COOLDOWN -> item(STICK);
            case PERK_GRABBER_GRABBED -> item(BLAZE_ROD);
            case PERK_BOOSTER, PERK_BOOSTER_COOLDOWN -> item(FEATHER);
            case PERK_FAST_ARROW -> item(SUGAR);
            case PERK_TNT_ARROW, PERK_TNT_ARROW_COOLDOWN -> item(TNT);
            case PERK_ARROW_BOMB, PERK_ARROW_BOMB_COOLDOWN -> item(FIREWORK_STAR);
            case PERK_REFLECTOR, PERK_REFLECTOR_COOLDOWN -> item(CACTUS);
            case PERK_FREEZER, PERK_FREEZER_COOLDOWN -> item(ICE);
            case PERK_PROTECTIVE_SHIELD, PERK_PROTECTIVE_SHIELD_COOLDOWN -> item(EMERALD);
            case PERK_GRAPPLING_HOOK -> item(FISHING_ROD).hiddenUnbreakable();
            case PERK_GRAPPLING_HOOK_COOLDOWN -> item(STICK);
            case PERK_ELEVATOR, PERK_ELEVATOR_COOLDOWN -> item(PISTON);
            case PERK_ROPE, PERK_ROPE_COOLDOWN -> item(VINE);
            case PERK_STOMPER -> item(DIAMOND_BOOTS);
            case PERK_HOOK_ARROW, PERK_HOOK_ARROW_COOLDOWN -> item(REDSTONE_TORCH);
            case DEFAULT_BOW -> item(BOW).enchant(INFINITY, 1).enchant(PUNCH, 2).enchant(KNOCKBACK, 5)/*.flag(HIDE_UNBREAKABLE)TODO*/.hiddenUnbreakable();
            case DEFAULT_SHEARS -> item(SHEARS).enchant(KNOCKBACK, 5).enchant(EFFICIENCY, 5).hiddenUnbreakable().set(ATTRIBUTE_MODIFIERS, new AttributeList(List.of(), false)).set(TOOL, new Tool(List.of(new Tool.Rule(new BlockTypeFilter.Blocks(woolbattle.api().woolProvider().woolMaterials()), 1000F, true)), 0, 0));
            case DEFAULT_PEARL -> item(ENDER_PEARL).glow(true);
            case DEFAULT_PEARL_COOLDOWN -> item(FIREWORK_STAR);
            case DEFAULT_ARROW -> item(ARROW);
            case ARMOR_LEATHER_BOOTS -> item(LEATHER_BOOTS).hiddenUnbreakable();
            case ARMOR_LEATHER_LEGGINGS -> item(LEATHER_LEGGINGS).hiddenUnbreakable();
            case ARMOR_LEATHER_CHESTPLATE -> item(LEATHER_CHESTPLATE).hiddenUnbreakable();
            case ARMOR_LEATHER_HELMET -> item(LEATHER_HELMET).hiddenUnbreakable();
            case TELEPORT_COMPASS -> item(COMPASS).glow(true);
            case LOBBY_PARTICLES_ON -> item(BLAZE_ROD);
            case LOBBY_PARTICLES_OFF -> item(BLAZE_ROD);
            case HEIGHT_DISPLAY_ON -> item(LIME_TERRACOTTA);
            case HEIGHT_DISPLAY_OFF -> item(RED_TERRACOTTA);
            case NEXT_PAGE, PREV_PAGE -> item(LIME_STAINED_GLASS_PANE);
            case NEXT_PAGE_UNUSABLE, PREV_PAGE_UNUSABLE -> item(RED_STAINED_GLASS_PANE);
            case PERK_SCAMP -> item(GOLD_INGOT);
            case PERK_SPIDER, PERK_SPIDER_COOLDOWN -> item(SPIDER_SPAWN_EGG);
            case PERK_DRAW_ARROW, PERK_DRAW_ARROW_COOLDOWN -> item(TORCH);
            case PERK_FREEZE_ARROW, PERK_FREEZE_ARROW_COOLDOWN -> item(ARROW);
            case PERK_BERSERKER -> item(DIAMOND_SWORD);
            case GRAY_GLASS_PANE -> item(GRAY_STAINED_GLASS_PANE);
            case BLACK_GLASS_PANE -> item(BLACK_STAINED_GLASS_PANE);
            case HEIGHT_DISPLAY_COLOR_ENTRY -> item(PAPER);
            case PERK_DOUBLE_JUMP, PERK_DOUBLE_JUMP_COOLDOWN -> item(LEATHER_BOOTS).set(DYED_COLOR, new DyedItemColor(new Color(124, 31, 171), false)).set(ATTRIBUTE_MODIFIERS, AttributeList.EMPTY.withTooltip(false));
        };
    }
}

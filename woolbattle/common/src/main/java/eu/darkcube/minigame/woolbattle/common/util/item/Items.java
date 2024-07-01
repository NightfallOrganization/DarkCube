/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.item;

import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.server.item.ItemBuilder;

public enum Items implements CommonItem {
    LOBBY_TEAMS,
    LOBBY_PERKS,
    LOBBY_VOTING,
    LOBBY_VOTING_MAPS,
    LOBBY_VOTING_EP_GLITCH,
    LOBBY_VOTING_LIFES,
    LOBBY_VOTING_LIFES_ENTRY,
    GENERAL_VOTING_FOR,
    GENERAL_VOTING_AGAINST,
    SETTINGS,
    SETTINGS_WOOL_DIRECTION,
    SETTINGS_HEIGHT_DISPLAY,
    SETTINGS_HEIGHT_DISPLAY_COLOR,
    SETTINGS_WOOL_DIRECTION_LEFT_TO_RIGHT,
    SETTINGS_WOOL_DIRECTION_RIGHT_TO_LEFT,
    PERKS_MISC,
    PERKS_ACTIVE,
    PERKS_PASSIVE,
    PERK_CAPSULE,
    PERK_CAPSULE_COOLDOWN,
    PERK_SWITCHER,
    PERK_SWITCHER_COOLDOWN,
    PERK_LINE_BUILDER,
    PERK_LINE_BUILDER_COOLDOWN,
    PERK_SLIME_PLATFORM,
    PERK_SLIME_PLATFORM_COOLDOWN,
    PERK_WOOL_BOMB,
    PERK_WOOL_BOMB_COOLDOWN,
    PERK_EXTRA_WOOL,
    PERK_LONGJUMP,
    PERK_ROCKETJUMP,
    PERK_ARROW_RAIN,
    PERK_ARROW_RAIN_COOLDOWN,
    PERK_RONJAS_TOILET_SPLASH,
    PERK_RONJAS_TOILET_SPLASH_COOLDOWN,
    PERK_BLINK,
    PERK_BLINK_COOLDOWN,
    PERK_SAFETY_PLATFORM,
    PERK_SAFETY_PLATFORM_COOLDOWN,
    PERK_WALL_GENERATOR,
    PERK_WALL_GENERATOR_COOLDOWN,
    PERK_GRANDPAS_CLOCK,
    PERK_GRANDPAS_CLOCK_COOLDOWN,
    PERK_GHOST,
    PERK_GHOST_COOLDOWN,
    PERK_MINE,
    PERK_MINE_COOLDOWN,
    PERK_MINIGUN,
    PERK_MINIGUN_COOLDOWN,
    PERK_GRABBER,
    PERK_GRABBER_GRABBED,
    PERK_GRABBER_COOLDOWN,
    PERK_BOOSTER,
    PERK_BOOSTER_COOLDOWN,
    PERK_FAST_ARROW,
    PERK_TNT_ARROW,
    PERK_TNT_ARROW_COOLDOWN,
    PERK_ARROW_BOMB,
    PERK_ARROW_BOMB_COOLDOWN,
    PERK_REFLECTOR,
    PERK_REFLECTOR_COOLDOWN,
    PERK_FREEZER,
    PERK_FREEZER_COOLDOWN,
    PERK_PROTECTIVE_SHIELD,
    PERK_PROTECTIVE_SHIELD_COOLDOWN,
    PERK_GRAPPLING_HOOK,
    PERK_GRAPPLING_HOOK_COOLDOWN,
    PERK_ELEVATOR,
    PERK_ELEVATOR_COOLDOWN,
    PERK_ROPE,
    PERK_ROPE_COOLDOWN,
    PERK_STOMPER,
    PERK_HOOK_ARROW,
    PERK_HOOK_ARROW_COOLDOWN,
    DEFAULT_BOW,
    DEFAULT_SHEARS,
    DEFAULT_PEARL,
    DEFAULT_ARROW,
    DEFAULT_PEARL_COOLDOWN,
    ARMOR_LEATHER_BOOTS,
    ARMOR_LEATHER_LEGGINGS,
    ARMOR_LEATHER_CHESTPLATE,
    ARMOR_LEATHER_HELMET,
    TELEPORT_COMPASS,
    LOBBY_PARTICLES_ON,
    LOBBY_PARTICLES_OFF,
    HEIGHT_DISPLAY_ON,
    HEIGHT_DISPLAY_OFF,
    NEXT_PAGE,
    PREV_PAGE,
    NEXT_PAGE_UNUSABLE,
    PREV_PAGE_UNUSABLE,
    PERK_SCAMP,
    PERK_SPIDER,
    PERK_SPIDER_COOLDOWN,
    PERK_DRAW_ARROW,
    PERK_DRAW_ARROW_COOLDOWN,
    PERK_FREEZE_ARROW,
    PERK_FREEZE_ARROW_COOLDOWN,
    PERK_BERSERKER,
    GRAY_GLASS_PANE,
    BLACK_GLASS_PANE,
    ;

    private final String key;
    private ItemBuilder builder;

    Items() {
        key = this.name();
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public ItemBuilder builder() {
        return builder.clone();
    }

    static {
        for (var item : values()) {
            item.builder = ProviderReference.PROVIDER.builder(item);
        }
    }

    public interface Provider {
        ItemBuilder builder(Items items);
    }

    private interface ProviderReference {
        Provider PROVIDER = WoolBattleProvider.PROVIDER.service(Provider.class);
    }
}

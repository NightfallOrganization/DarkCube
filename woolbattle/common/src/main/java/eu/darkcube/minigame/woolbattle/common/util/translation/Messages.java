/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.translation;

import java.util.function.Function;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.translation.Message;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.util.Language;

public enum Messages implements Message {

    NO_PLAYER,
    COMMAND_LANGUAGE_USAGE,
    UNKNOWN_LANGUAGE,
    CHANGED_LANGUAGE,
    ALREADY_IN_TEAM,
    CHANGED_TEAM,
    TEAM_IS_FULL,
    EP_GLITCH_ON,
    EP_GLITCH_OFF,
    PERK_COOLDOWN_SET,
    PERK_COST_SET,
    MAP_CHANGED,
    STATS_WERE_DISABLED,
    STATS_ARE_ENABLED,
    STATS_ARE_DISABLED,
    VOTED_FOR_EP_GLITCH,
    VOTED_AGAINST_EP_GLITCH,
    VOTED_LIFES,
    ALREADY_VOTED_FOR_THIS,
    VOTED_FOR_MAP,
    PERK_SET_FOR_PLAYER,
    NOT_IMPLEMENTED,
    ALREADY_VOTED_FOR_MAP,
    SELECTED,
    ALREADY_SELECTED_PERK,
    AT_ALL,
    TIMER_CHANGED,
    ENTER_POSITIVE_NUMBER,
    PLAYER_JOINED,
    PLAYER_LEFT,
    CHANGED_LIFES,
    PLAYER_DIED,
    PLAYER_WAS_KILLED_BY_PLAYER,
    INVALID_MAP_SIZE,
    STARTING_GAME,

    INVENTORY_PERKS,
    INVENTORY_VOTING,
    INVENTORY_VOTING_MAPS,
    INVENTORY_VOTING_EP_GLITCH,
    INVENTORY_TEAMS,
    TEAM_WAS_ELIMINATED,
    INVENTORY_COMPASS,
    TEAM_HAS_WON,
    PLAYER_HAS_MOST_KILLS,
    STATS_PLACE_3,
    STATS_PLACE_2,
    STATS_PLACE_1,
    STATS_PLACE_TH,
    INVENTORY_VOTING_LIFES,
    SETTINGS_TITLE,
    HEIGHT_DISPLAY_SETTINGS_TITLE,
    HEIGHT_DISPLAY_COLOR_SETTINGS_TITLE,
    COSTS,
    COSTS_PER_BLOCK,
    COOLDOWN,
    CLICK_TO_SELECT,
    KILLSTREAK,
    NO_MAPS_TO_MIGRATE,
    MIGRATING_MAPS,
    MAP_MIGRATION_COMPLETE,
    LEFT_SETUP_MODE,
    ENTERED_SETUP_MODE,
    WOOL_DIRECTION_SETTINGS_TITLE,
    GAME_NOT_FOUND,
    NOT_IN_A_WORLD,
    SET_LOBBY_SPAWN,
    SET_SETUP_LOBBY_SPAWN,
    TEAM_CONFIGURATION_NOT_FOUND,
    INVALID_NAME_COLOR,
    INVALID_WOOL_COLOR,
    TEAM_INFO;

    public static final String KEY_PREFIX = "WOOLBATTLE_";
    public static final String ITEM_PREFIX = "ITEM_";
    public static final String LORE_PREFIX = "LORE_";
    public static final String TEAM_PREFIX = "TEAM_";
    public static final Function<String, String> KEY_MODIFIER = s -> KEY_PREFIX + s;
    public static final Function<String, String> ITEM_MODIFIER = s -> KEY_MODIFIER.apply(ITEM_PREFIX + s);
    public static final Function<String, String> LORE_MODIFIER = s -> ITEM_MODIFIER.apply(LORE_PREFIX + s);

    private final String key;

    Messages() {
        this.key = this.name();
    }

    public static Component getMessage(String messageKey, Language language, Object... replacements) {
        return language.getMessage(KEY_PREFIX + messageKey, replacements);
    }

    @Override
    public String getPrefixModifier() {
        return KEY_PREFIX;
    }

    @Override
    public String key() {
        return key;
    }

    public Component getServerMessage(Object... args) {
        return getMessage(Language.ENGLISH, args);
    }

    public Component getMessage(WBUser user, Object... args) {
        return getMessage(user.language(), args);
    }
}

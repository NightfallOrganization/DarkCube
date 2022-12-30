/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;

import eu.darkcube.system.BaseMessage;

public enum Message implements BaseMessage {

	XP_POINTS,
	XP_LEVELS,
	XP_ADDED_TO_PLAYER,
	XP_ADDED_TO_PLAYERS,
	XP_REMOVED_FROM_PLAYER,
	XP_REMOVED_FROM_PLAYERS,
	XP_OF_PLAYER,
	XP_OF_PLAYER_SET,
	XP_OF_PLAYERS_SET,
	SPAWNER_GIVEN_TO_SELF,
	SPAWNER_GIVEN_TO_PLAYER,
	SPAWNER_GIVEN_TO_PLAYERS,
	YOU_WERE_HEALED,
	YOU_HEALED_PLAYERS,
	;

	private final String key;

	Message() {
		this.key = name();
	}

	public static String getPrefix() {
		return "DarkEssentials_";
	}

	@Override
	public String getPrefixModifier() {
		return getPrefix();
	}

	@Override
	public String getKey() {
		return key;
	}
}

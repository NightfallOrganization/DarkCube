/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.miners.player;

import eu.darkcube.system.BaseMessage;

import java.util.function.Function;

// copied from woolbattle
public enum Message implements BaseMessage {

	SERVER_STOP,
	TIME_REMAINING,
	TIME_SECONDS,
	TIME_MINUTES,
	PLAYER_JOINED,
	PLAYER_LEFT,
	PLAYER_DIED,
	PLAYER_WAS_KILLED,
	COMMAND_TIMER_CHANGED,
	FAIL_PLACE_BLOCK,
	FAIL_PLACE_BLOCK_AT_SPAWN,
	FAIL_TEAM_FULL,
	FAIL_GAME_RUNNING;

	public static final String KEY_PREFIX = "MINERS_";
	public static final Function<String, String> KEY_MODIFIER = s -> KEY_PREFIX + s;

	private final String key;

	Message() {
		this.key = this.name();
	}

	@Override
	public String getPrefixModifier() {
		return KEY_PREFIX;
	}

	@Override
	public String getKey() {
		return key;
	}
}

/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.player;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.util.Language;
import org.bukkit.entity.Player;

import java.util.function.Function;

// copied from woolbattle
public enum Message {

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
	public static final Function<String, String> KEY_MODFIIER = s -> KEY_PREFIX + s;

	private final String key;

	Message() {
		this.key = this.name();
	}

	public static Component getMessage(String messageKey, Language language,
			Object... replacements) {
		if (replacements.length > 0)
			for (int i = 0; i < replacements.length; i++)
				if (replacements[i] instanceof Message)
					replacements[i] = ((Message) replacements[i]).getMessage(language);
		return language.getMessage(KEY_PREFIX + messageKey, replacements);
	}

	public String getKey() {
		return key;
	}

	public final Component getMessage(Language language, Object... replacements) {
		return getMessage(getKey(), language, replacements);
	}

	public final Component getServerMessage(Object... replacements) {
		return getMessage(key, Language.ENGLISH, replacements);
	}

	public final Component getMessage(MinersPlayer user, Object... replacements) {
		return getMessage(user.getLanguage(), replacements);
	}

	public final Component getMessage(Player user, Object... replacements) {
		return getMessage(Miners.getPlayerManager().getMinersPlayer(user).getLanguage(),
				replacements);
	}

}

/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.userapi.User;

public enum Message implements BaseMessage {
	ITEM_ATTRIBUTE_DAMAGE;

	public static final String PREFIX = "SKYLAND_";
	private final String key;

	Message() {
		key = name();
	}

	public static Component getMessage(String key, User user, Object... replacements) {
		return user.getLanguage().getMessage(PREFIX + key, replacements);
	}

	@Override
	public String getPrefixModifier() {
		return PREFIX;
	}

	@Override
	public String getKey() {
		return key;
	}
}

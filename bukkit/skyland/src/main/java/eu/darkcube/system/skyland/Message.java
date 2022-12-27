/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import eu.darkcube.system.userapi.User;

public enum Message {

	;

	public static final String PREFIX = "SKYLAND_";

	public static String getMessage(String key, User user, Object... replacements) {
		return user.getLanguage().getMessage(PREFIX + key, replacements);
	}

}

/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.data;

import eu.darkcube.system.userapi.User;

public interface UserModifier {

	default void onLoad(User user) {}

	default void onUnload(User user) {}

}

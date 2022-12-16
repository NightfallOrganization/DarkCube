/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.nbt;

import java.util.function.Supplier;

public interface DataStorage {

	void set(String key, Object value);

	<T> T get(String key);

	<T> T getOr(String key, T orElse);

	<T> T getOr(String key, Supplier<T> orElse);

	boolean has(String key);

	<T> T remove(String key);

}

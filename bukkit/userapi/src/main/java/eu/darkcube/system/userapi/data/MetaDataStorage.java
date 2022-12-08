/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.data;

import java.util.function.Supplier;

public interface MetaDataStorage {

	/**
	 * Stores some data
	 * 
	 * @param key
	 * @param value
	 */
	void set(Key key, Object value);

	/**
	 * 
	 * @param <T>
	 * @param key
	 * @return stored data, null if not present
	 */
	<T> T get(Key key);

	/**
	 * @param <T>
	 * @param key
	 * @param orElse
	 * @return stored data, the given fallback data if not present
	 */
	<T> T getOr(Key key, T orElse);

	/**
	 * @param <T>
	 * @param key
	 * @param orElse
	 * @return stored data, the given fallback data if not present
	 */
	<T> T getOr(Key key, Supplier<T> orElse);

	/**
	 * @param key
	 * @return whether or not this storage has data under the given key
	 */
	boolean has(Key key);

	/**
	 * Removes some data
	 * 
	 * @param <T>
	 * @param key
	 * @return the removed data
	 */
	<T> T remove(Key key);

}

/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.data;

import java.util.function.Supplier;

/**
 * @author DasBabyPixel
 */
public interface PersistentDataStorage {

	/**
	 * Saves some data
	 * 
	 * @param <T>
	 * @param key
	 * @param type
	 * @param data
	 */
	<T> void set(Key key, PersistentDataType<T> type, T data);

	/**
	 * Removes some data
	 * 
	 * @param <T>
	 * @param key
	 * @param type
	 * @return the removed data
	 */
	<T> T remove(Key key, PersistentDataType<T> type);

	/**
	 * @param <T>
	 * @param key
	 * @param type
	 * @return saved data, null if not present
	 */
	<T> T get(Key key, PersistentDataType<T> type);

	/**
	 * @param <T>
	 * @param key
	 * @param type
	 * @param defaultValue
	 * @return the saved data, defaultValue if not present
	 */
	<T> T get(Key key, PersistentDataType<T> type, Supplier<T> defaultValue);

	<T> void setIfNotPresent(Key key, PersistentDataType<T> type, T data);

	/**
	 * @param key
	 * @return whether or not data is present for the given key
	 */
	boolean has(Key key);

}

/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import java.util.function.Supplier;

/**
 * @author DasBabyPixel
 */
public interface PersistentDataStorage {

	/**
	 * Saves some data
	 *
	 * @param key  the key
	 * @param type the type
	 * @param data the data
	 */
	<T> void set(Key key, PersistentDataType<T> type, T data);

	/**
	 * Removes some data
	 *
	 * @param key  the key
	 * @param type the type
	 *
	 * @return the removed data
	 */
	<T> T remove(Key key, PersistentDataType<T> type);

	/**
	 * @param key  the key
	 * @param type the type
	 *
	 * @return saved data, null if not present
	 */
	<T> T get(Key key, PersistentDataType<T> type);

	/**
	 * @param key          the key
	 * @param type         the type
	 * @param defaultValue the default value
	 *
	 * @return the saved data, defaultValue if not present
	 */
	<T> T get(Key key, PersistentDataType<T> type, Supplier<T> defaultValue);

	/**
	 * @param key  the key
	 * @param type the type
	 * @param data the data
	 * @param <T>  the data type
	 */
	<T> void setIfNotPresent(Key key, PersistentDataType<T> type, T data);

	/**
	 * @param key the key
	 *
	 * @return whether data is present for the given key
	 */
	boolean has(Key key);

}

/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.util;

public class Pair<T, U> {
	
	private T key;
	private U value;
	
	public Pair(T key, U value) {
		this.key = key;
		this.value = value;
	}

	public T getKey() {
		return key;
	}
	
	public U getValue() {
		return value;
	}
}

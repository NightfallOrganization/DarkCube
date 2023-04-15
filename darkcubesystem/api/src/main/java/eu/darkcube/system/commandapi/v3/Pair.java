/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

public class Pair<T, V> {

	private T first;
	private V second;

	public Pair(T first, V second) {
		this.first = first;
		this.second = second;
	}

	public static <T, V> Pair<T, V> of(T first, V second) {
		return new Pair<>(first, second);
	}

	public T getFirst() {
		return first;
	}

	public V getSecond() {
		return second;
	}

}

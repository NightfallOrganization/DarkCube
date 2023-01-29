/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

public class Triple<T, V, K> {

	private T first;
	private V second;
	private K third;

	public Triple(T first, V second, K third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public T getFirst() {
		return first;
	}

	public V getSecond() {
		return second;
	}

	public K getThird() {
		return third;
	}

}

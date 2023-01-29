/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;
public class WeightedObject {
	Object object;
	int weight;

	public WeightedObject(Object object, int weight) {
		this.object = object;
		this.weight = weight;
	}

	public Object getObject() {
		return object;
	}

	public int getWeight() {
		return weight;
	}
}

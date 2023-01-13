/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import java.util.ArrayList;
import java.util.List;

class Arrays {
	
	@SafeVarargs
	public static <T> T[] insert(T[] array, T[] instance, T... insertion) {
		List<T> list = new ArrayList<>();
		for(T t : array) {
			list.add(t);
		}
		for(T t : insertion) {
			list.add(t);
		}
		return list.toArray(instance);
	}
	
}

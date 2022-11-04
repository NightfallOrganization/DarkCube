package eu.darkcube.system;

import java.util.*;

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

/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.friend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Arrays {

	private static final Collection<ConvertingRule<?>> CONVERTING_RULES = new HashSet<>();

	public static <T> T[] addBefore(T[] array, T t) {
		List<T> list = new ArrayList<T>();
		list.add(t);
		list.addAll(asList(array));
		return list.toArray(array);
	}

	@SafeVarargs
	public static <T> T[] addAfter(T[] array, T... t) {
		List<T> list = new ArrayList<T>(array.length + 1);
		list.addAll(Arrays.asList(array));
		list.addAll(asList(t));
		return list.toArray(array);
	}

	public static <T> T[] addAfter(T[] array, T t) {
		List<T> list = new ArrayList<T>(array.length + 1);
		list.addAll(Arrays.asList(array));
		list.add(t);
		return list.toArray(array);
	}

	@SafeVarargs
	public static <T> List<T> asList(T... array) {
		return new ArrayList<>(java.util.Arrays.asList(array));
	}

	public static <T> List<T> asList(Collection<? extends T> collection) {
		return new ArrayList<>(collection);
	}

	@SafeVarargs
	public static <T> Set<T> asSet(T... array) {
		return new HashSet<>(Arrays.asList(array));
	}

	public static <T> Set<T> asSet(Collection<? extends T> collection) {
		return new HashSet<>(collection);
	}

	public static <T> List<String> toSortedStringList(T[] array, T[] exclusion, String start) {
		return toSortedStringList(asList(array), asList(exclusion), start);
	}

	public static <T> List<String> toSortedStringList(T[] array, String start) {
		return toSortedStringList(asList(array), start);
	}

	public static <T> List<String> toSortedStringList(Collection<? extends T> collection,
			Collection<? extends T> exclusion, String start) {
		collection = new ArrayList<>(collection);
		collection.removeAll(exclusion);
		return toSortedStringList(collection, start);
	}

	public static <T> List<String> toSortedStringList(Collection<? extends T> collection, String start) {
		if (collection.size() == 0)
			return new ArrayList<>();
		if (start == null)
			start = "";
		List<String> list = new ArrayList<>();
		Class<?> clazz = new ArrayList<>(collection).get(0).getClass();
		Method method = null;
		for (Method mt : ConvertingRule.class.getDeclaredMethods()) {
			if (mt.getReturnType().equals(String.class)) {
				method = mt;
				break;
			}
		}
		ConvertingRule<?> theRule = null;
		for (ConvertingRule<?> rule : CONVERTING_RULES) {
			if (rule.getConvertingClass().isAssignableFrom(clazz)) {
				if (theRule == null) {
					theRule = rule;
					continue;
				}
				if (theRule.getConvertingClass().isAssignableFrom(rule.getConvertingClass())) {
					theRule = rule;
				}
			}
		}
		if (theRule != null) {
			for (T t : collection) {
				try {
					String converted = (String) method.invoke(theRule, t);
					if (converted.startsWith(start)) {
						list.add(converted);
					}
				} catch (IllegalAccessException ex) {
					ex.printStackTrace();
				} catch (IllegalArgumentException ex) {
					ex.printStackTrace();
				} catch (InvocationTargetException ex) {
					ex.printStackTrace();
				}
			}
		} else {
			for (T t : collection) {
				String converted = t.toString();
				if (converted.startsWith(start)) {
					list.add(converted);
				}
			}
		}
		return list;
	}

	public static boolean addConvertingRule(ConvertingRule<?> rule) {
		return CONVERTING_RULES.add(rule);
	}

	public static abstract class ConvertingRule<T> {

		public abstract Class<T> getConvertingClass();

		public abstract String convert(T object);

	}
}

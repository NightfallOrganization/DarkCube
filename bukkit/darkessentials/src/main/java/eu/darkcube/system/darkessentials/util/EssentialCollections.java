/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;

public class EssentialCollections {

	private static final Set<SortingRule<?>> RULES = new HashSet<>();

	public static <T> List<T> asList(Collection<T> ts) {
		return new ArrayList<>(ts);
	}

	@SafeVarargs
	public static <T> List<T> asList(T... ts) {
		return Arrays.asList(ts);
	}

	public static <T> Set<T> asSet(Collection<T> ts) {
		return new HashSet<>(ts);
	}

	@SafeVarargs
	public static <T> Set<T> asSet(T... ts) {
		return Sets.newHashSet(ts);
	}

	public static <T> T[] asArray(Collection<T> ts, T[] empty) {
		return ts.toArray(empty);
	}

	public static final void addSortingRule(SortingRule<?> rule) {
		RULES.add(rule);
	}

	public static <T> List<String> toSortedStringList(T[] ts, String start) {
		return toSortedStringList(asList(ts), start);
	}

	public static <T> List<String> toSortedStringList(Collection<T> ts, String start) {
		return toSortedStringList(ts, new HashSet<>(), start);
	}

	private static final Class<?>[] getAllSuperClasses(Class<?> clazz) {
		List<Class<?>> classes = new ArrayList<>();
		if (clazz.getSuperclass() != null) {
			classes.add(clazz.getSuperclass());
			classes.addAll(asList(clazz.getInterfaces()));
		}
		for (Class<?> superClass : new ArrayList<>(classes)) {
			classes.addAll(asList(getAllSuperClasses(superClass)));
		}
		return asArray(classes, new Class<?>[0]);
	}

	@SuppressWarnings({ "unchecked" })
	public static <T> List<String> toSortedStringList(Collection<T> ts, Collection<String> used, String start) {
		if (ts.isEmpty())
			return new ArrayList<>();
		Set<String> set = new HashSet<>();
		Set<SortingRule<T>> rules = new HashSet<>();
		Class<T> collectionClass = (Class<T>) ts.iterator().next().getClass();
		try {
			for (SortingRule<?> rule : RULES) {
				for (Class<?> clazz : getAllSuperClasses(collectionClass)) {
					if (clazz.getName().equals(rule.getRuleClass().getName())) {
						rules.add((SortingRule<T>) rule);
						break;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ArrayList<>(set);
		}
		for (T t : ts) {
			String st = null;
			for (SortingRule<T> rule : rules) {
				if (rule.instanceOf(t)) {
					st = rule.toString(t);
					break;
				}
			}
			if (st == null)
				st = t.toString();
			final String fst = st;
			if (st.toLowerCase().startsWith(start.toLowerCase())) {
				if (used.stream().map(s -> s.toLowerCase(Locale.ENGLISH))
						.filter(s -> s.equals(fst.toLowerCase(Locale.ENGLISH))).count() == 0) {
					set.add(fst);
				}
			}
		}
		return new ArrayList<>(set);
	}

	public static abstract class SortingRule<T> {

		public abstract boolean instanceOf(Object obj);

		public abstract String toString(T t);

		public abstract Class<T> getRuleClass();
	}

	public static abstract class UUIDBasedSortingRule<T> extends SortingRule<T> {

		public abstract String toString(T t, UUID uuid);

		@Override
		public String toString(T t) {
			return toString(t, null);
		}
	}
}

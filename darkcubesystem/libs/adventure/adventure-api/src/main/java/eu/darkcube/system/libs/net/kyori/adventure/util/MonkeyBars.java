/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * {@link Collection} related utilities.
 *
 * @since 4.8.0
 */
public final class MonkeyBars {
  private MonkeyBars() {
  }

  /**
   * Creates a set from an array of enum constants.
   *
   * @param type the enum type
   * @param constants the enum constants
   * @param <E> the enum type
   * @return the set
   * @since 4.0.0
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <E extends Enum<E>> @NotNull Set<E> enumSet(final Class<E> type, final E@NotNull... constants) {
    final Set<E> set = EnumSet.noneOf(type);
    Collections.addAll(set, constants);
    return Collections.unmodifiableSet(set);
  }

  /**
   * Adds an element to the end of the list, or returns a new list.
   *
   * <p>The returned list is unmodifiable.</p>
   *
   * @param oldList the old list
   * @param newElement the element to add
   * @param <T> the element type
   * @return a list
   * @since 4.8.0
   */
  public static <T> @NotNull List<T> addOne(final @NotNull List<T> oldList, final T newElement) {
    if (oldList.isEmpty()) return Collections.singletonList(newElement);
    final List<T> newList = new ArrayList<>(oldList.size() + 1);
    newList.addAll(oldList);
    newList.add(newElement);
    return Collections.unmodifiableList(newList);
  }
}

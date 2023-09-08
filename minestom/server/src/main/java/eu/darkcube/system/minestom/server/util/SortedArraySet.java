/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * Useful for very frequent lookups with small sizes (a few hundred entries)
 */
public class SortedArraySet<T> extends AbstractArraySet<T> {

    private final Comparator<T> comparator;

    public SortedArraySet(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SortedArraySet<?> that = (SortedArraySet<?>) o;
        return Objects.equals(comparator, that.comparator);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), comparator);
    }

    @Override protected int findIndex(T value) {
        return Arrays.binarySearch(array, 0, size, value, comparator);
    }
}

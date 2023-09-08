/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.util;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import it.unimi.dsi.fastutil.objects.ObjectArrays;

import java.util.*;

public abstract class AbstractArraySet<T> extends AbstractSet<T> {
    /**
     * This is a safe value used by {@link ArrayList} (as of Java 7) to avoid
     * throwing {@link OutOfMemoryError} on some JVMs. We adopt the same value.
     */
    public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    protected int size;
    protected T[] array;

    @Override public @NotNull Iterator<T> iterator() {
        return new OurIterator();
    }

    @Override public int size() {
        return size;
    }

    @Override public boolean add(T value) {
        var index = findIndex(value);
        if (index >= 0) return false; // Already in array
        index = insertionPosition(index);
        grow(size + 1);
        // Check if we have to shift the array
        if (index != size) System.arraycopy(array, index, array, index + 1, size - index);
        array[index] = value;
        size++;
        return true;
    }

    @Override public boolean remove(Object value) {
        var index = findIndex((T) value);
        if (index < 0) return false;
        removeElementAt(index);
        return true;
    }

    @Override public boolean contains(Object value) {
        return findIndex((T) value) >= 0;
    }

    public T first() {
        return elementAt(0);
    }

    public T last() {
        return elementAt(size - 1);
    }

    public T remove(int index) {
        if (index < 0 || index >= size) throw new ArrayIndexOutOfBoundsException();
        return removeElementAt(index);
    }

    public T elementAt(int index) {
        if (index < 0 || index >= size) throw new ArrayIndexOutOfBoundsException();
        return array[index];
    }

    private T removeElementAt(int index) {
        size--;
        // Check if we have to shift the array
        if (index != size) System.arraycopy(array, index + 1, array, index, size - index);
        var element = array[size];
        array[size] = null;
        return element;
    }

    private void grow(int requiredSize) {
        if (requiredSize <= array.length) return;
        if (array != ObjectArrays.DEFAULT_EMPTY_ARRAY)
            requiredSize = (int) Math.max(Math.min((long) array.length + (long) (array.length >> 1), MAX_ARRAY_SIZE), requiredSize);
        else if (requiredSize < 10) requiredSize = 10;
        array = Arrays.copyOf(array, requiredSize);
    }

    @Override public void clear() {
        // We remove the memory footprint when clearing. This is safer
        array = (T[]) ObjectArrays.DEFAULT_EMPTY_ARRAY;
        size = 0;
    }

    @Override public <E> E @NotNull [] toArray(E[] intoArray) {
        if (intoArray.length < size) return (E[]) Arrays.copyOf(array, size, intoArray.getClass());
        System.arraycopy(array, 0, intoArray, 0, size);
        if (intoArray.length > size) intoArray[size] = null;
        return intoArray;
    }

    @Override public Object @NotNull [] toArray() {
        return Arrays.copyOf(array, size, Object[].class);
    }

    private int insertionPosition(int index) {
        return -index - 1;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractArraySet<?> that = (AbstractArraySet<?>) o;
        return size == that.size && Arrays.equals(array, that.array);
    }

    @Override public int hashCode() {
        int result = Objects.hash(super.hashCode(), size);
        result = 31 * result + Arrays.hashCode(array);
        return result;
    }

    protected abstract int findIndex(T value);

    private class OurIterator implements Iterator<T> {
        private int index;
        private int last;

        @Override public boolean hasNext() {
            return index <= size;
        }

        @Override public T next() {
            if (index >= size) throw new NoSuchElementException();
            return array[last = index++];
        }

        @Override public void remove() {
            if (last == -1) throw new IllegalStateException();
            removeElementAt(last);
            index--;
            last = -1;
        }
    }
}

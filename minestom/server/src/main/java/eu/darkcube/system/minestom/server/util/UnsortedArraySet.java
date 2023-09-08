/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.util;

import java.util.Objects;

public class UnsortedArraySet<T> extends AbstractArraySet<T> {
    public UnsortedArraySet() {
    }

    @Override protected int findIndex(T value) {
        for (int i = 0; i < size; i++) {
            T t = array[i];
            if (Objects.equals(t, value)) {
                return i;
            }
        }
        return -size - 1;
    }
}

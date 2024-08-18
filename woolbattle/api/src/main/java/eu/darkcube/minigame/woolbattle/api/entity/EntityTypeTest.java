/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public interface EntityTypeTest<B, T extends B> {
    static <B, T extends B> EntityTypeTest<B, T> forClass(Class<T> cls) {
        return new EntityTypeTest<>() {
            @Nullable
            @Override
            public T tryCast(B obj) {
                return (T) (cls.isInstance(obj) ? obj : null);
            }

            @Override
            public Class<? extends B> getBaseClass() {
                return cls;
            }
        };
    }

    static <B, T extends B> EntityTypeTest<B, T> forExactClass(Class<T> cls) {
        return new EntityTypeTest<>() {
            @Nullable
            @Override
            public T tryCast(B obj) {
                return (T) (cls.equals(obj.getClass()) ? obj : null);
            }

            @Override
            public Class<? extends B> getBaseClass() {
                return cls;
            }
        };
    }

    @Nullable
    T tryCast(B obj);

    Class<? extends B> getBaseClass();
}

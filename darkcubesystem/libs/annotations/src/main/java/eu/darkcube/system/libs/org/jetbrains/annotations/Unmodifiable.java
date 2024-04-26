/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.org.jetbrains.annotations;

import java.lang.annotation.*;

/**
 * An annotation which marks a {@link java.util.Collection} or {@link java.util.Map} type
 * as unmodifiable. A collection or a map is unmodifiable if any mutator methods
 * (e.g. {@link java.util.Collection#add(Object)}) throw exception or have no effect,
 * and the object references stored as collection elements, map keys, and map values
 * are never changed. The referenced objects themselves still could be changed if they
 * are mutable.
 *
 * @see UnmodifiableView
 * @since 19.0.0
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE_USE})
public @interface Unmodifiable {
}

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
 * as unmodifiable view. A collection or a map is unmodifiable view if any mutator methods
 * (e.g. {@link java.util.Collection#add(Object)}) throw exception or have no effect.
 * However, the content of the collection or the map may still be updated by other code.
 *
 * @see Unmodifiable
 * @since 19.0.0
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE_USE})
public @interface UnmodifiableView {
}

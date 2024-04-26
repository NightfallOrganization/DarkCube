/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

/**
 * Elements annotated with the {@link PlatformAPI} annotation are intended for platform implementations of the Adventure API
 * only and should not be used by standard developers. They are not public API and may change or be removed without warning at any time.
 *
 * <p>This annotation should always be used in tandem with the {@link ApiStatus.Internal} annotation to more consistently produce
 * warnings</p>
 *
 * @since 4.12.0
 */
@ApiStatus.Internal
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE, ElementType.ANNOTATION_TYPE})
public @interface PlatformAPI {
}

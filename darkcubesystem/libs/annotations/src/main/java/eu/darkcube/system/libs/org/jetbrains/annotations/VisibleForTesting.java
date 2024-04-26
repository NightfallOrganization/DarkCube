/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.org.jetbrains.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A member or type annotated with VisibleForTesting claims that its visibility is higher than necessary,
 * only for testing purposes. In particular:
 * <ul>
 *   <li>If public or protected member/type is annotated with VisibleForTesting,
 *   it's assumed that package-private access is enough for production code.</li>
 *   <li>If package-private member/type is annotated with VisibleForTesting,
 *   it's assumed that private access is enough for production code.</li>
 *   <li>It's illegal to annotate private member/type as VisibleForTesting.</li>
 * </ul>
 * <p>
 * This annotation means that the annotated element exposes internal data and breaks encapsulation
 * of the containing class; the annotation won't prevent its use from production code, developers
 * even won't see warnings if their IDE doesn't support the annotation. It's better to provide
 * proper API which can be used in production as well as in tests.
 *
 * @since 20.0.0
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.TYPE})
public @interface VisibleForTesting { }

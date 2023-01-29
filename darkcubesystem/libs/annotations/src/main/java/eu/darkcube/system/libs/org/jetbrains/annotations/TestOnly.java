/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.org.jetbrains.annotations;

import java.lang.annotation.*;

/**
 * A member or type annotated with TestOnly claims that it should be used from testing code only.
 * <p>
 * Apart from documentation purposes this annotation is intended to be used by static analysis tools
 * to validate against element contract violations.
 * <p>
 * This annotation means that the annotated element exposes internal data and breaks encapsulation
 * of the containing class; the annotation won't prevent its use from production code, developers
 * even won't see warnings if their IDE doesn't support the annotation. It's better to provide
 * proper API which can be used in production as well as in tests.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.TYPE})
public @interface TestOnly { }

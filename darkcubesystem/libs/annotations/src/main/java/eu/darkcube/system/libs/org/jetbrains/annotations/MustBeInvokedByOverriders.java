/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.org.jetbrains.annotations;

import java.lang.annotation.*;

/**
 * The annotation should be applied to overridable non-abstract method
 * and indicates that all the overriders must invoke this method via
 * superclass method invocation expression. The static analysis tools
 * may report a warning if overrider fails to invoke this method.
 *
 * @since 20.0.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface MustBeInvokedByOverriders {}

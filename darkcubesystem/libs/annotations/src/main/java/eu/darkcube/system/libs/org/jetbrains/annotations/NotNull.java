/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.org.jetbrains.annotations;

import java.lang.annotation.*;

/**
 * An element annotated with NotNull claims {@code null} value is <em>forbidden</em>
 * to return (for methods), pass to (parameters) and hold (local variables and fields).
 * Apart from documentation purposes this annotation is intended to be used by static analysis tools
 * to validate against probable runtime errors and element contract violations.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface NotNull {
  /**
   * @return Custom exception message
   */
  String value() default "";

  /**
   * @return Custom exception type that should be thrown when not-nullity contract is violated.
   * The exception class should have a constructor with one String argument (message).
   *
   * By default, {@link IllegalArgumentException} is thrown on null method arguments and
   * {@link IllegalStateException} &mdash; on null return value.
   */
  Class<? extends Exception> exception() default Exception.class;
}

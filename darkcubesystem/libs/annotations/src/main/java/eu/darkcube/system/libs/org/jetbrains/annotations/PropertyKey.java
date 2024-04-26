/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.org.jetbrains.annotations;

import java.lang.annotation.*;

/**
 * Specifies that a method parameter, local variable, field or a method return value
 * must be a valid property key in a specific resource bundle. When a string literal
 * which is not a property key in the specified bundle is passed as a parameter,
 * static analyzers may highlight it as an error. The annotation is also could be used
 * by IDEs to provide completion in string literals passed as parameters.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.FIELD, ElementType.TYPE_USE})
public @interface PropertyKey {
  /**
   * The full-qualified name of the resource bundle in which the property keys must
   * be present. Consists of a full-qualified name of the package corresponding to the
   * directory where the resource bundle is located and the base name of the resource
   * bundle (with no locale specifier or extension), separated with a dot.
   */
  @NonNls String resourceBundle();
}

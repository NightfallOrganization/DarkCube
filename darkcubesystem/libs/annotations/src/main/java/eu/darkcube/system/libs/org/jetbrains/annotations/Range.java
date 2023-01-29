/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.org.jetbrains.annotations;

import java.lang.annotation.*;

/**
 * An annotation which allows to specify for integral type (byte, char, short, int, long) an allowed values range.
 * Applying this annotation to other types is not correct.
 * <p>
 * Example:
 * <pre>{@code public @Range(from = 0, to = Integer.MAX_VALUE) int length() {
 *   return this.length; // returns a non-negative integer
 * }}</pre>
 *
 * @since 17.0.0
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE_USE})
public @interface Range {
  /**
   * @return minimal allowed value (inclusive)
   */
  long from();

  /**
   * @return maximal allowed value (inclusive)
   */
  long to();
}

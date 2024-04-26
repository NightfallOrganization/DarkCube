/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.util.Set;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Various utilities.
 *
 * @since 4.0.0
 */
public final class ShadyPines {
  private ShadyPines() {
  }

  /**
   * Creates a set from an array of enum constants.
   *
   * @param type the enum type
   * @param constants the enum constants
   * @param <E> the enum type
   * @return the set
   * @since 4.0.0
   * @deprecated for removal since 4.8.0, use {@link MonkeyBars#enumSet(Class, Enum[])}
   */
  @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
  @Deprecated
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <E extends Enum<E>> @NotNull Set<E> enumSet(final Class<E> type, final E@NotNull... constants) {
    return MonkeyBars.enumSet(type, constants);
  }

  /**
   * Checks if {@code a} is equal to {@code b}.
   *
   * @param a a double
   * @param b a double
   * @return {@code true} if {@code a} is equal to {@code b}, otherwise {@code false}
   * @since 4.0.0
   */
  public static boolean equals(final double a, final double b) {
    return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
  }

  /**
   * Checks if {@code a} is equal to {@code b}.
   *
   * @param a a float
   * @param b a float
   * @return {@code true} if {@code a} is equal to {@code b}, otherwise {@code false}
   * @since 4.0.0
   */
  public static boolean equals(final float a, final float b) {
    return Float.floatToIntBits(a) == Float.floatToIntBits(b);
  }
}

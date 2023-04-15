/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.time.Duration;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Standard game tick utilities.
 *
 * @since 4.0.0
 */
public interface Ticks {
  /**
   * The number of ticks that occur in one second.
   *
   * @since 4.0.0
   */
  int TICKS_PER_SECOND = 20;

  /**
   * A single tick duration, in milliseconds.
   *
   * @since 4.0.0
   */
  long SINGLE_TICK_DURATION_MS = 1000 / TICKS_PER_SECOND;

  /**
   * Converts ticks into a {@link Duration}.
   *
   * @param ticks the number of ticks
   * @return a duration
   * @since 4.0.0
   */
  static @NotNull Duration duration(final long ticks) {
    return Duration.ofMillis(ticks * SINGLE_TICK_DURATION_MS);
  }
}

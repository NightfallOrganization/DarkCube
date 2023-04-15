/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.util.function.BiFunction;

/**
 * A function that takes two {@code int}s as input and produces a {@code R} result.
 *
 * <p>This is the {@code int}-consuming primitive specialization for {@link BiFunction}.</p>
 *
 * @param <R> the result type
 * @since 4.0.0
 */
@FunctionalInterface
public interface IntFunction2<R> {
  /**
   * Evaluates this predicate on the given arguments.
   *
   * @param first the first input argument
   * @param second the second input argument
   * @return a result
   * @since 4.0.0
   */
  R apply(final int first, final int second);
}

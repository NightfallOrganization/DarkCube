/*
 * Copyright (c) 2018-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.examination;

import java.util.stream.Stream;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * An examiner.
 *
 * @param <R> the result type
 * @since 1.0.0
 */
public interface Examiner<R> {
  /**
   * Examines an examinable.
   *
   * @param examinable the examinable
   * @return the result
   * @since 1.1.0
   */
  default @NotNull R examine(final @NotNull Examinable examinable) {
    return this.examine(examinable.examinableName(), examinable.examinableProperties());
  }

  /**
   * Examines.
   *
   * @param name the examinable name
   * @param properties the examinable properties
   * @return the result
   * @since 1.1.0
   */
  @NotNull R examine(final @NotNull String name, final @NotNull Stream<? extends ExaminableProperty> properties);

  /**
   * Examines.
   *
   * @param value the value to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final @Nullable Object value);

  /**
   * Examines.
   *
   * @param value the value to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final boolean value);

  /**
   * Examines.
   *
   * @param values the values to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final boolean@Nullable[] values);

  /**
   * Examines.
   *
   * @param value the value to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final byte value);

  /**
   * Examines.
   *
   * @param values the values to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final byte@Nullable[] values);

  /**
   * Examines.
   *
   * @param value the value to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final char value);

  /**
   * Examines.
   *
   * @param values the values to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final char@Nullable[] values);

  /**
   * Examines.
   *
   * @param value the value to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final double value);

  /**
   * Examines.
   *
   * @param values the values to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final double@Nullable[] values);

  /**
   * Examines.
   *
   * @param value the value to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final float value);

  /**
   * Examines.
   *
   * @param values the values to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final float@Nullable[] values);

  /**
   * Examines.
   *
   * @param value the value to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final int value);

  /**
   * Examines.
   *
   * @param values the values to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final int@Nullable[] values);

  /**
   * Examines.
   *
   * @param value the value to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final long value);

  /**
   * Examines.
   *
   * @param values the values to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final long@Nullable[] values);

  /**
   * Examines.
   *
   * @param value the value to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final short value);

  /**
   * Examines.
   *
   * @param values the values to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final short@Nullable[] values);

  /**
   * Examines.
   *
   * @param value the value to examine
   * @return the result
   * @since 1.0.0
   */
  @NotNull R examine(final @Nullable String value);
}

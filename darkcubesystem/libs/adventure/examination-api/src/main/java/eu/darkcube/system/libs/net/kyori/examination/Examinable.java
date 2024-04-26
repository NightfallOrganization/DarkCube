/*
 * Copyright (c) 2018-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.examination;

import java.util.stream.Stream;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Something that can be examined.
 *
 * @since 1.0.0
 */
public interface Examinable {
  /**
   * Gets the examinable name.
   *
   * @return the examinable name
   * @since 1.0.0
   */
  default @NotNull String examinableName() {
    return this.getClass().getSimpleName();
  }

  /**
   * Gets a stream of examinable properties.
   *
   * @return a stream of examinable properties
   * @since 1.0.0
   */
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.empty();
  }

  /**
   * Examines.
   *
   * <p>You should not override this method.</p>
   *
   * @param examiner the examiner
   * @param <R> the result type
   * @return the examination result
   * @since 1.0.0
   */
  default /* final */ <R> @NotNull R examine(final @NotNull Examiner<R> examiner) {
    return examiner.examine(this);
  }
}

/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.internal;

import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.net.kyori.examination.string.StringExaminer;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Utilities internal to Adventure.
 */
@ApiStatus.Internal
public final class Internals {
  private Internals() {
  }

  /**
   * Examines an {@link Examinable} using the {@link StringExaminer}.
   *
   * @param examinable the examinable
   * @return the result from examining
   * @since 4.10.0
   */
  public static @NotNull String toString(final @NotNull Examinable examinable) {
    return examinable.examine(StringExaminer.simpleEscaping());
  }
}

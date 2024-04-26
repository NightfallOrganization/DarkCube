/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.flattener;

import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A listener accepting styled information from flattened components.
 *
 * @since 4.7.0
 */
@FunctionalInterface
public interface FlattenerListener {
  /**
   * Begin a region of style in the component.
   *
   * @param style the style to push
   * @since 4.7.0
   */
  default void pushStyle(final @NotNull Style style) {
  }

  /**
   * Accept the plain-text content of a single component.
   *
   * @param text the component text
   * @since 4.7.0
   */
  void component(final @NotNull String text);

  /**
   * Pop a pushed style.
   *
   * <p>The popped style will always be the most recent un-popped style that has been {@link #pushStyle(Style) pushed}.</p>
   *
   * @param style the style popped, as passed to {@link #pushStyle(Style)}
   * @since 4.7.0
   */
  default void popStyle(final @NotNull Style style) {
  }
}

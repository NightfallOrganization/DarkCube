/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Common methods between {@link ListBinaryTag} and {@link ListBinaryTag.Builder}.
 *
 * @param <R> the return type
 * @param <T> the element type
 * @since 4.0.0
 */
public interface ListTagSetter<R, T extends BinaryTag> {
  /**
   * Adds a tag.
   *
   * @param tag the tag
   * @return a list tag
   * @since 4.0.0
   */
  @NotNull R add(final T tag);

  /**
   * Adds multiple tags.
   *
   * @param tags the tags
   * @return a list tag
   * @since 4.4.0
   */
  @NotNull R add(final Iterable<? extends T> tags);
}

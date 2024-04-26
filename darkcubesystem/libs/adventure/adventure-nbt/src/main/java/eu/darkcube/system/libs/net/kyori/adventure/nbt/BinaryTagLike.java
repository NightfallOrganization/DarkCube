/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Something that can be represented as a binary tag.
 *
 * @since 4.4.0
 */
public interface BinaryTagLike {
  /**
   * Gets a {@link BinaryTag} representation.
   *
   * @return a binary tag
   * @since 4.4.0
   */
  @NotNull BinaryTag asBinaryTag();
}

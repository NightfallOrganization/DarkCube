/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A binary tag.
 *
 * @since 4.0.0
 */
public interface BinaryTag extends BinaryTagLike, Examinable {
  /**
   * Gets the tag type.
   *
   * @return the tag type
   * @since 4.0.0
   */
  @NotNull BinaryTagType<? extends BinaryTag> type();

  @Override
  default @NotNull BinaryTag asBinaryTag() {
    return this;
  }
}

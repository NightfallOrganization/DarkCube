/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.key;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Something that has an associated {@link Key}.
 *
 * @since 4.0.0
 */
public interface Keyed {
  /**
   * Gets the key.
   *
   * @return the key
   * @since 4.0.0
   */
  @NotNull Key key();
}

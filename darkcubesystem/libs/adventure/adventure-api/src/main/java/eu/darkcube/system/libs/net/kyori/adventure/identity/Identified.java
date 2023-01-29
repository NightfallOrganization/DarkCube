/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.identity;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Something that can be identified by an {@link Identity}.
 *
 * @since 4.0.0
 */
public interface Identified {
  /**
   * Gets the identity.
   *
   * @return the identity
   * @since 4.0.0
   */
  @NotNull Identity identity();
}

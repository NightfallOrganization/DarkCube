/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.translation;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Something that has a translation key.
 *
 * @since 4.8.0
 */
public interface Translatable {
  /**
   * Gets the translation key.
   *
   * @return the translation key
   * @since 4.8.0
   */
  @NotNull String translationKey();
}

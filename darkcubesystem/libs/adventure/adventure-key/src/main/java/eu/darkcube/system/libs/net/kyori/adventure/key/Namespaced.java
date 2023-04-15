/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.key;

import eu.darkcube.system.libs.org.intellij.lang.annotations.Pattern;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Something that has a namespace.
 *
 * @since 4.4.0
 */
public interface Namespaced {
  /**
   * Gets the namespace.
   *
   * @return the namespace
   * @since 4.4.0
   */
  @NotNull @Pattern(KeyImpl.NAMESPACE_PATTERN) String namespace();
}

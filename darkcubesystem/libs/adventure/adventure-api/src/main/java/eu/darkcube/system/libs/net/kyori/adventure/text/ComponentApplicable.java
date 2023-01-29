/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Something that can be applied to a {@link Component}.
 *
 * @since 4.0.0
 */
@FunctionalInterface
public interface ComponentApplicable {
  /**
   * Applies to {@code component}.
   *
   * @param component the component
   * @return a component with something applied.
   * @since 4.0.0
   */
  @NotNull Component componentApply(final @NotNull Component component);
}

/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import eu.darkcube.system.libs.net.kyori.adventure.text.format.StyleBuilderApplicable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Something that can be applied to a {@link ComponentBuilder}.
 *
 * @see StyleBuilderApplicable
 * @since 4.0.0
 */
@FunctionalInterface
public interface ComponentBuilderApplicable {
  /**
   * Applies to {@code component}.
   *
   * @param component the component builder
   * @since 4.0.0
   */
  @Contract(mutates = "param")
  void componentBuilderApply(final @NotNull ComponentBuilder<?, ?> component);
}

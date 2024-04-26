/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.format;

import eu.darkcube.system.libs.net.kyori.adventure.text.ComponentBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.ComponentBuilderApplicable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Something that can be applied to a {@link Style}.
 *
 * @since 4.0.0
 */
@FunctionalInterface
@SuppressWarnings("FunctionalInterfaceMethodChanged")
public interface StyleBuilderApplicable extends ComponentBuilderApplicable {
  /**
   * Applies to {@code style}.
   *
   * @param style the style builder
   * @since 4.0.0
   */
  @Contract(mutates = "param")
  void styleApply(final Style.@NotNull Builder style);

  @Override
  default void componentBuilderApply(final @NotNull ComponentBuilder<?, ?> component) {
    component.style(this::styleApply);
  }
}

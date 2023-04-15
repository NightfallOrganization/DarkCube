/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.renderer;

import java.util.function.Function;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A component renderer.
 *
 * @param <C> the context type
 * @since 4.0.0
 */
public interface ComponentRenderer<C> {
  /**
   * Renders a component.
   *
   * @param component the component
   * @param context the context
   * @return the rendered component
   * @since 4.0.0
   */
  @NotNull Component render(final @NotNull Component component, final @NotNull C context);

  /**
   * Return a {@link ComponentRenderer} that takes a different context type.
   *
   * @param transformer context type transformer
   * @param <T> transformation function
   * @return mapping renderer
   * @since 4.0.0
   */
  default <T> ComponentRenderer<T> mapContext(final Function<T, C> transformer) {
    return (component, ctx) -> this.render(component, transformer.apply(ctx));
  }
}

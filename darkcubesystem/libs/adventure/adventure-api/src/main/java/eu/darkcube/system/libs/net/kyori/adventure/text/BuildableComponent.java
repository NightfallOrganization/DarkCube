/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import eu.darkcube.system.libs.net.kyori.adventure.util.Buildable;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A component which may be built.
 *
 * @param <C> the component type
 * @param <B> the builder type
 * @since 4.0.0
 */
public interface BuildableComponent<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>> extends Buildable<C, B>, Component {
  /**
   * Create a builder from this component.
   *
   * @return the builder
   */
  @Override
  @NotNull B toBuilder();
}

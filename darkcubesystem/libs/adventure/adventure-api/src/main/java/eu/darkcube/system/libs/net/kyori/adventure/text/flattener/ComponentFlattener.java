/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.flattener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import eu.darkcube.system.libs.net.kyori.adventure.builder.AbstractBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.util.Buildable;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * A 'flattener' to convert a component tree to a linear string for display.
 *
 * @since 4.7.0
 */
public interface ComponentFlattener extends Buildable<ComponentFlattener, ComponentFlattener.Builder> {
  /**
   * Create a new builder for a flattener.
   *
   * @return a new builder
   * @since 4.7.0
   */
  static @NotNull Builder builder() {
    return new ComponentFlattenerImpl.BuilderImpl();
  }

  /**
   * A basic flattener that will print only information directly contained in components.
   *
   * <p>The output of this flattener aims to match what the vanilla <em>Minecraft: Java Edition</em> client
   * will display when unable to resolve any game data.</p>
   *
   * @return a basic flattener
   * @since 4.7.0
   */
  static @NotNull ComponentFlattener basic() {
    return ComponentFlattenerImpl.BASIC;
  }

  /**
   * A component flattener that will only handle text components.
   *
   * <p>All other component types will not be included in the output.</p>
   *
   * @return a text-only flattener
   * @since 4.7.0
   */
  static @NotNull ComponentFlattener textOnly() {
    return ComponentFlattenerImpl.TEXT_ONLY;
  }

  /**
   * Perform a flattening on the component, providing output to the {@code listener}.
   *
   * @param input the component to be flattened
   * @param listener the listener that will receive flattened component state
   * @since 4.7.0
   */
  void flatten(final @NotNull Component input, final @NotNull FlattenerListener listener);

  /**
   * A builder for a component flattener.
   *
   * <p>A new builder will start out empty, providing empty strings for all component types.</p>
   *
   * @since 4.7.0
   */
  interface Builder extends AbstractBuilder<ComponentFlattener>, Buildable.Builder<ComponentFlattener> {
    /**
     * Register a type of component to be handled.
     *
     * @param type the component type
     * @param converter the converter to map that component to a string
     * @param <T> component type
     * @return this builder
     * @see #complexMapper(Class, BiConsumer) for component types that are too complex to be directly rendered to a string
     * @since 4.7.0
     */
    <T extends Component> @NotNull Builder mapper(final @NotNull Class<T> type, final @NotNull Function<T, String> converter);

    /**
     * Register a type of component that needs to be flattened to an intermediate stage.
     *
     * @param type the component type
     * @param converter a provider of contained Components
     * @param <T> component type
     * @return this builder
     * @since 4.7.0
     */
    <T extends Component> @NotNull Builder complexMapper(final @NotNull Class<T> type, final @NotNull BiConsumer<T, Consumer<Component>> converter);

    /**
     * Register a handler for unknown component types.
     *
     * <p>This will be called if no other converter can be found.</p>
     *
     * @param converter the converter, may be null to ignore unknown components
     * @return this builder
     * @since 4.7.0
     */
    @NotNull Builder unknownMapper(final @Nullable Function<Component, String> converter);
  }
}

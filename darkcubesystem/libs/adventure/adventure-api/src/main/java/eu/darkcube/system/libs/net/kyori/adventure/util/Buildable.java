/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.util.function.Consumer;

import eu.darkcube.system.libs.net.kyori.adventure.builder.AbstractBuilder;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * Something that can be built.
 *
 * @param <R> the type that can be built
 * @param <B> the builder type
 * @since 4.0.0
 */
public interface Buildable<R, B extends Buildable.Builder<R>> {
  /**
   * Configures {@code builder} using {@code consumer} and then builds.
   *
   * @param builder the builder
   * @param consumer the builder consume
   * @param <R> the type to be built
   * @param <B> the builder type
   * @return the built thing
   * @since 4.0.0
   * @deprecated since 4.10.0, use {@link AbstractBuilder#configureAndBuild(AbstractBuilder, Consumer)}
   */
  @Contract(mutates = "param1")
  @Deprecated
  static <R extends Buildable<R, B>, B extends Builder<R>> @NotNull R configureAndBuild(final @NotNull B builder, final @Nullable Consumer<? super B> consumer) {
    return AbstractBuilder.configureAndBuild(builder, consumer);
  }

  /**
   * Create a builder from this thing.
   *
   * @return a builder
   * @since 4.0.0
   */
  @Contract(value = "-> new", pure = true)
  @NotNull B toBuilder();

  /**
   * A builder.
   *
   * @param <R> the type to be built
   * @since 4.0.0
   * @deprecated since 4.10.0, use {@link AbstractBuilder}
   */
  @Deprecated
  interface Builder<R> extends AbstractBuilder<R> {
    /**
     * Builds.
     *
     * @return the built thing
     * @since 4.0.0
     */
    @Contract(value = "-> new", pure = true)
    @Override
    @NotNull R build();
  }
}

/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.builder;

import java.util.function.Consumer;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * A builder.
 *
 * @param <R> the type to be built
 * @since 4.10.0
 */
@FunctionalInterface
public interface AbstractBuilder<R> {
  /**
   * Configures {@code builder} using {@code consumer} and then builds.
   *
   * @param builder the builder
   * @param consumer the builder consume
   * @param <R> the type to be built
   * @param <B> the builder type
   * @return the built thing
   * @since 4.10.0
   */
  @Contract(mutates = "param1")
  static <R, B extends AbstractBuilder<R>> @NotNull R configureAndBuild(final @NotNull B builder, final @Nullable Consumer<? super B> consumer) {
    if (consumer != null) {
      consumer.accept(builder);
    }
    return builder.build();
  }

  /**
   * Builds.
   *
   * @return the built thing
   * @since 4.10.0
   */
  @Contract(value = "-> new", pure = true)
  @NotNull R build();
}

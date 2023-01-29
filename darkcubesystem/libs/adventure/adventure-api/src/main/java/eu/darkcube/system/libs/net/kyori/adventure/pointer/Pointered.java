/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.pointer;

import java.util.Optional;
import java.util.function.Supplier;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;

/**
 * Something that can retrieve values based on a given {@link Pointer}.
 *
 * @since 4.8.0
 */
public interface Pointered {
  /**
   * Gets the value of {@code pointer}.
   *
   * @param pointer the pointer
   * @param <T> the type
   * @return the value
   * @since 4.8.0
   */
  default <T> @NotNull Optional<T> get(final @NotNull Pointer<T> pointer) {
    return this.pointers().get(pointer);
  }

  /**
   * Gets the value of {@code pointer}.
   *
   * <p>If this {@code Audience} is unable to provide a value for {@code pointer}, {@code defaultValue} will be returned.</p>
   *
   * @param pointer the pointer
   * @param defaultValue the default value
   * @param <T> the type
   * @return the value
   * @since 4.8.0
   */
  @Contract("_, null -> _; _, !null -> !null")
  @SuppressWarnings("checkstyle:MethodName")
  default <T> @Nullable T getOrDefault(final @NotNull Pointer<T> pointer, final @Nullable T defaultValue) {
    return this.pointers().getOrDefault(pointer, defaultValue);
  }

  /**
   * Gets the value of {@code pointer}.
   *
   * <p>If this {@code Audience} is unable to provide a value for {@code pointer}, the value supplied by {@code defaultValue} will be returned.</p>
   *
   * @param pointer the pointer
   * @param defaultValue the default value supplier
   * @param <T> the type
   * @return the value
   * @since 4.8.0
   */
  @SuppressWarnings("checkstyle:MethodName")
  default <T> @UnknownNullability T getOrDefaultFrom(final @NotNull Pointer<T> pointer, final @NotNull Supplier<? extends T> defaultValue) {
    return this.pointers().getOrDefaultFrom(pointer, defaultValue);
  }

  /**
   * Gets the pointers for this object.
   *
   * @return the pointers
   * @since 4.8.0
   */
  default @NotNull Pointers pointers() {
    return Pointers.empty();
  }
}

/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.pointer;

import java.util.Optional;
import java.util.function.Supplier;
import eu.darkcube.system.libs.net.kyori.adventure.builder.AbstractBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.util.Buildable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;

/**
 * A collection of {@link Pointer pointers}.
 *
 * @since 4.8.0
 */
public interface Pointers extends Buildable<Pointers, Pointers.Builder> {
  /**
   * Gets an empty pointers collection.
   *
   * @return the pointers
   * @since 4.8.0
   */
  @Contract(pure = true)
  static @NotNull Pointers empty() {
    return PointersImpl.EMPTY;
  }

  /**
   * Gets a new pointers builder.
   *
   * @return the builder
   * @see Builder
   * @since 4.8.0
   */
  @Contract(pure = true)
  static @NotNull Builder builder() {
    return new PointersImpl.BuilderImpl();
  }

  /**
   * Gets the value of {@code pointer}.
   *
   * @param pointer the pointer
   * @param <T> the type
   * @return the value
   * @since 4.8.0
   */
  <T> @NotNull Optional<T> get(final @NotNull Pointer<T> pointer);

  /**
   * Gets the value of {@code pointer}.
   *
   * <p>If a value for {@code pointer} is unable to be provided, {@code defaultValue} will be returned.</p>
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
    return this.get(pointer).orElse(defaultValue);
  }

  /**
   * Gets the value of {@code pointer}.
   *
   * <p>If a value for {@code pointer} is unable to be provided, the value supplied by {@code defaultValue} will be returned.</p>
   *
   * @param pointer the pointer
   * @param defaultValue the default value supplier
   * @param <T> the type
   * @return the value
   * @since 4.8.0
   */
  @SuppressWarnings("checkstyle:MethodName")
  default <T> @UnknownNullability T getOrDefaultFrom(final @NotNull Pointer<T> pointer, final @NotNull Supplier<? extends T> defaultValue) {
    return this.get(pointer).orElseGet(defaultValue);
  }

  /**
   * Checks if a given pointer is supported.
   *
   * <p>This will return {@code true} when a mapping for the provided pointer exists, even if the value for the pointer is {@code null}.</p>
   *
   * @param pointer the pointer
   * @param <T> the type
   * @return if the pointer is supported
   * @since 4.8.0
   */
  <T> boolean supports(final @NotNull Pointer<T> pointer);

  /**
   * A builder of pointers.
   *
   * @see Pointers
   * @since 4.8.0
   */
  interface Builder extends AbstractBuilder<Pointers>, Buildable.Builder<Pointers> {
    /**
     * Adds a pointer with a static, optional value.
     *
     * @param pointer the pointer
     * @param value the optional value
     * @param <T> the type
     * @return this builder
     * @since 4.8.0
     */
    @Contract("_, _ -> this")
    default <T> @NotNull Builder withStatic(final @NotNull Pointer<T> pointer, final @Nullable T value) {
      return this.withDynamic(pointer, () -> value);
    }

    /**
     * Adds a pointer with a dynamic value provided by a supplier.
     *
     * @param pointer the pointer
     * @param value the value supplier
     * @param <T> the type
     * @return this builder
     * @since 4.8.0
     */
    @Contract("_, _ -> this")
    <T> @NotNull Builder withDynamic(final @NotNull Pointer<T> pointer, @NotNull Supplier<@Nullable T> value);
  }
}

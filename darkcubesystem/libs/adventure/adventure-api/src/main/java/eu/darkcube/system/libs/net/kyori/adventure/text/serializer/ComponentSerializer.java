/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * A {@link Component} serializer and deserializer.
 *
 * @param <I> the input component type
 * @param <O> the output component type
 * @param <R> the serialized type
 * @since 4.0.0
 */
public interface ComponentSerializer<I extends Component, O extends Component, R> {
  /**
   * Deserialize a component from input of type {@code R}.
   *
   * @param input the input
   * @return the component
   * @since 4.0.0
   */
  @NotNull O deserialize(final @NotNull R input);

  /**
   * Deserialize a component from input of type {@code R}.
   *
   * <p>If {@code input} is {@code null}, then {@code null} will be returned.</p>
   *
   * @param input the input
   * @return the component if {@code input} is non-null, otherwise {@code null}
   * @since 4.7.0
   * @deprecated for removal since 4.8.0, use {@link #deserializeOrNull(Object)} instead.
   */
  @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
  @Contract(value = "!null -> !null; null -> null", pure = true)
  @Deprecated
  default @Nullable O deseializeOrNull(final @Nullable R input) {
    return this.deserializeOrNull(input);
  }

  /**
   * Deserialize a component from input of type {@code R}.
   *
   * <p>If {@code input} is {@code null}, then {@code null} will be returned.</p>
   *
   * @param input the input
   * @return the component if {@code input} is non-null, otherwise {@code null}
   * @since 4.8.0
   */
  @Contract(value = "!null -> !null; null -> null", pure = true)
  default @Nullable O deserializeOrNull(final @Nullable R input) {
    return this.deserializeOr(input, null);
  }

  /**
   * Deserialize a component from input of type {@code R}.
   *
   * <p>If {@code input} is {@code null}, then {@code fallback} will be returned.</p>
   *
   * @param input the input
   * @param fallback the fallback value
   * @return the component if {@code input} is non-null, otherwise {@code fallback}
   * @since 4.7.0
   */
  @Contract(value = "!null, _ -> !null; null, _ -> param2", pure = true)
  default @Nullable O deserializeOr(final @Nullable R input, final @Nullable O fallback) {
    if (input == null) return fallback;

    return this.deserialize(input);
  }

  /**
   * Serializes a component into an output of type {@code R}.
   *
   * @param component the component
   * @return the output
   * @since 4.0.0
   */
  @NotNull R serialize(final @NotNull I component);

  /**
   * Serializes a component into an output of type {@code R}.
   *
   * <p>If {@code component} is {@code null}, then {@code null} will be returned.</p>
   *
   * @param component the component
   * @return the output if {@code component} is non-null, otherwise {@code null}
   * @since 4.7.0
   */
  @Contract(value = "!null -> !null; null -> null", pure = true)
  default @Nullable R serializeOrNull(final @Nullable I component) {
    return this.serializeOr(component, null);
  }

  /**
   * Serializes a component into an output of type {@code R}.
   *
   * <p>If {@code component} is {@code null}, then {@code fallback} will be returned.</p>
   *
   * @param component the component
   * @param fallback the fallback value
   * @return the output if {@code component} is non-null, otherwise {@code fallback}
   * @since 4.7.0
   */
  @Contract(value = "!null, _ -> !null; null, _ -> param2", pure = true)
  default @Nullable R serializeOr(final @Nullable I component, final @Nullable R fallback) {
    if (component == null) return fallback;

    return this.serialize(component);
  }
}

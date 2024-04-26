/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.format;

import java.util.Map;
import java.util.Set;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * Writes style properties to a mutable object. Used to override some default methods from {@link StyleSetter}
 * with faster alternatives that only work for mutable objects.
 *
 * @param <T> The type implementing this interface e.g. {@link Component}
 * @see StyleSetter
 * @since 4.10.0
 */
@ApiStatus.NonExtendable
public interface MutableStyleSetter<T extends MutableStyleSetter<?>> extends StyleSetter<T> {
  /**
   * Sets {@code decorations} to {@link TextDecoration.State#TRUE}.
   *
   * @param decorations the decorations
   * @return a mutable object ({@code T})
   * @since 4.10.0
   */
  @Override
  @Contract("_ -> this")
  @SuppressWarnings("unchecked")
  default @NotNull T decorate(final @NotNull TextDecoration@NotNull... decorations) {
    for (int i = 0, length = decorations.length; i < length; i++) {
      this.decorate(decorations[i]);
    }
    return (T) this;
  }

  /**
   * Sets decorations using the specified {@code decorations} map.
   *
   * <p>If a given decoration does not have a value explicitly set, the value of that particular decoration is not changed.</p>
   *
   * @param decorations a map containing text decorations and their respective state.
   * @return a mutable object ({@code T})
   * @since 4.10.0
   */
  @Override
  @Contract("_ -> this")
  @SuppressWarnings("unchecked")
  default @NotNull T decorations(final @NotNull Map<TextDecoration, TextDecoration.State> decorations) {
    requireNonNull(decorations, "decorations");
    for (final Map.Entry<TextDecoration, TextDecoration.State> entry : decorations.entrySet()) {
      this.decoration(entry.getKey(), entry.getValue());
    }
    return (T) this;
  }

  /**
   * Sets the state of a set of decorations to {@code flag}.
   *
   * @param decorations the decorations
   * @param flag {@code true} if this mutable object should have the decorations, {@code false} if
   *     this mutable object should not have the decorations
   * @return a mutable object ({@code T})
   * @since 4.10.0
   */
  @Override
  @Contract("_, _ -> this")
  @SuppressWarnings("unchecked")
  default @NotNull T decorations(final @NotNull Set<TextDecoration> decorations, final boolean flag) {
    final TextDecoration.State state = TextDecoration.State.byBoolean(flag);
    decorations.forEach(decoration -> this.decoration(decoration, state));
    return (T) this;
  }
}

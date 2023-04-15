/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.format;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEventSource;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * Writes style properties to an object.
 *
 * @param <T> the type implementing this interface, e.g. {@link Component}
 * @see Style
 * @since 4.10.0
 */
@ApiStatus.NonExtendable
public interface StyleSetter<T extends StyleSetter<?>> {
  /**
   * Sets the font.
   *
   * @param font the font
   * @return an object ({@code T})
   * @since 4.10.0
   * @sinceMinecraft 1.16
   */
  @NotNull T font(final @Nullable Key font);

  /**
   * Sets the color.
   *
   * @param color the color
   * @return an object ({@code T})
   * @since 4.10.0
   */
  @NotNull T color(final @Nullable TextColor color);

  /**
   * Sets the color if there isn't one set already.
   *
   * @param color the color
   * @return an object ({@code T})
   * @since 4.10.0
   */
  @NotNull T colorIfAbsent(final @Nullable TextColor color);

  /**
   * Sets the state of {@code decoration} to {@link TextDecoration.State#TRUE}.
   *
   * @param decoration the decoration
   * @return an object ({@code T})
   * @since 4.10.0
   */
  default @NotNull T decorate(final @NotNull TextDecoration decoration) {
    return this.decoration(decoration, TextDecoration.State.TRUE);
  }

  /**
   * Sets {@code decorations} to {@link TextDecoration.State#TRUE}.
   *
   * @param decorations the decorations
   * @return an object ({@code T})
   * @since 4.10.0
   */
  default @NotNull T decorate(final @NotNull TextDecoration@NotNull... decorations) {
    final Map<TextDecoration, TextDecoration.State> map = new EnumMap<>(TextDecoration.class);
    for (int i = 0, length = decorations.length; i < length; i++) {
      map.put(decorations[i], TextDecoration.State.TRUE);
    }
    return this.decorations(map);
  }

  /**
   * Sets the state of a decoration.
   *
   * @param decoration the decoration
   * @param flag {@code true} if this object should have the decoration, {@code false} if
   *     this object should not have the decoration
   * @return an object ({@code T})
   * @since 4.10.0
   */
  default @NotNull T decoration(final @NotNull TextDecoration decoration, final boolean flag) {
    return this.decoration(decoration, TextDecoration.State.byBoolean(flag));
  }

  /**
   * Sets the value of a decoration.
   *
   * @param decoration the decoration
   * @param state {@link TextDecoration.State#TRUE} if this object should have the
   *     decoration, {@link TextDecoration.State#FALSE} if this object should not
   *     have the decoration, and {@link TextDecoration.State#NOT_SET} if the decoration
   *     should not have a set value
   * @return an object ({@code T})
   * @since 4.10.0
   */
  @NotNull T decoration(final @NotNull TextDecoration decoration, final TextDecoration.@NotNull State state);

  /**
   * Sets the state of a decoration to {@code state} if the current state of the decoration is {@link TextDecoration.State#NOT_SET}.
   *
   * @param decoration the decoration
   * @param state the state
   * @return an object ({@code T})
   * @since 4.12.0
   */
  @NotNull T decorationIfAbsent(final @NotNull TextDecoration decoration, final TextDecoration.@NotNull State state);

  /**
   * Sets decorations using the specified {@code decorations} map.
   *
   * <p>If a given decoration does not have a value explicitly set, the value of that particular decoration is not changed.</p>
   *
   * @param decorations the decorations
   * @return an object ({@code T})
   * @since 4.10.0
   */
  @NotNull T decorations(final @NotNull Map<TextDecoration, TextDecoration.State> decorations);

  /**
   * Sets the state of a set of decorations to {@code flag}.
   *
   * @param decorations the decorations
   * @param flag {@code true} if this builder should have the decorations, {@code false} if
   *     this builder should not have the decorations
   * @return an object ({@code T})
   * @since 4.10.0
   */
  default @NotNull T decorations(final @NotNull Set<TextDecoration> decorations, final boolean flag) {
    return this.decorations(decorations.stream().collect(Collectors.toMap(Function.identity(), decoration -> TextDecoration.State.byBoolean(flag))));
  }

  /**
   * Sets the click event.
   *
   * @param event the click event
   * @return an object ({@code T})
   * @since 4.10.0
   */
  @NotNull T clickEvent(final @Nullable ClickEvent event);

  /**
   * Sets the hover event.
   *
   * @param source the hover event source
   * @return an object ({@code T})
   * @since 4.10.0
   */
  @NotNull T hoverEvent(final @Nullable HoverEventSource<?> source);

  /**
   * Sets the string to be inserted when this object ({@code T}) is shift-clicked.
   *
   * @param insertion the insertion string
   * @return an object ({@code T})
   * @since 4.10.0
   */
  @NotNull T insertion(final @Nullable String insertion);
}

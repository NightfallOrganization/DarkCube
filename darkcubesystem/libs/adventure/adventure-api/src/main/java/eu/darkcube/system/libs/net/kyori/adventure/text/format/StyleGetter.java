/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.format;

import java.util.EnumMap;
import java.util.Map;

import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

/**
 * Reads style properties from an object.
 *
 * @see Style
 * @since 4.10.0
 */
@ApiStatus.NonExtendable
public interface StyleGetter {
  /**
   * Gets the font.
   *
   * @return the font
   * @since 4.10.0
   * @sinceMinecraft 1.16
   */
  @Nullable Key font();

  /**
   * Gets the color.
   *
   * @return the color
   * @since 4.10.0
   */
  @Nullable TextColor color();

  /**
   * Tests if this stylable has a decoration.
   *
   * @param decoration the decoration
   * @return {@code true} if this stylable has the decoration, {@code false} if this
   *     stylable does not have the decoration
   * @since 4.10.0
   */
  default boolean hasDecoration(final @NotNull TextDecoration decoration) {
    return this.decoration(decoration) == TextDecoration.State.TRUE;
  }

  /**
   * Gets the state of a decoration on this stylable.
   *
   * @param decoration the decoration
   * @return {@link TextDecoration.State#TRUE} if this stylable has the decoration,
   *     {@link TextDecoration.State#FALSE} if this stylable does not have the decoration,
   *     and {@link TextDecoration.State#NOT_SET} if not set
   * @since 4.10.0
   */
  TextDecoration.@NotNull State decoration(final @NotNull TextDecoration decoration);

  /**
   * Gets a map of decorations this stylable has.
   *
   * @return a set of decorations this stylable has
   * @since 4.10.0
   */
  @SuppressWarnings("Duplicates")
  default @Unmodifiable @NotNull Map<TextDecoration, TextDecoration.State> decorations() {
    final Map<TextDecoration, TextDecoration.State> decorations = new EnumMap<>(TextDecoration.class);
    for (int i = 0, length = DecorationMap.DECORATIONS.length; i < length; i++) {
      final TextDecoration decoration = DecorationMap.DECORATIONS[i];
      final TextDecoration.State value = this.decoration(decoration);
      decorations.put(decoration, value);
    }
    return decorations;
  }

  /**
   * Gets the click event.
   *
   * @return the click event
   * @since 4.10.0
   */
  @Nullable ClickEvent clickEvent();

  /**
   * Gets the hover event.
   *
   * @return the hover event
   * @since 4.10.0
   */
  @Nullable HoverEvent<?> hoverEvent();

  /**
   * Gets the string to be inserted when this stylable is shift-clicked.
   *
   * @return the insertion string
   * @since 4.10.0
   */
  @Nullable String insertion();
}

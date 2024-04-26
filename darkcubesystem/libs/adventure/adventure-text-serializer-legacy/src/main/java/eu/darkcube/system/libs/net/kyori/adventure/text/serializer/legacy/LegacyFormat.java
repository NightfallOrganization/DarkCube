/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy;

import java.util.Objects;
import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/*
 * This is a hack.
 */

/**
 * A <b>legacy</b> format.
 *
 * @since 4.0.0
 */
public final class LegacyFormat implements Examinable {
  static final LegacyFormat RESET = new LegacyFormat(true);
  private final @Nullable NamedTextColor color;
  private final @Nullable TextDecoration decoration;
  private final boolean reset;

  /*
   * Separate constructors to ensure a format can never be more than one thing.
   */

  LegacyFormat(final @Nullable NamedTextColor color) {
    this.color = color;
    this.decoration = null;
    this.reset = false;
  }

  LegacyFormat(final @Nullable TextDecoration decoration) {
    this.color = null;
    this.decoration = decoration;
    this.reset = false;
  }

  private LegacyFormat(final boolean reset) {
    this.color = null;
    this.decoration = null;
    this.reset = reset;
  }

  /**
   * Gets the color.
   *
   * @return the color
   * @since 4.0.0
   */
  public @Nullable TextColor color() {
    return this.color;
  }

  /**
   * Gets the decoration.
   *
   * @return the decoration
   * @since 4.0.0
   */
  public @Nullable TextDecoration decoration() {
    return this.decoration;
  }

  /**
   * Gets if this format is a reset.
   *
   * @return {@code true} if a reset, {@code false} otherwise
   * @since 4.0.0
   */
  public boolean reset() {
    return this.reset;
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || this.getClass() != other.getClass()) return false;
    final LegacyFormat that = (LegacyFormat) other;
    return this.color == that.color && this.decoration == that.decoration && this.reset == that.reset;
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(this.color);
    result = (31 * result) + Objects.hashCode(this.decoration);
    result = (31 * result) + Boolean.hashCode(this.reset);
    return result;
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("color", this.color),
      ExaminableProperty.of("decoration", this.decoration),
      ExaminableProperty.of("reset", this.reset)
    );
  }
}

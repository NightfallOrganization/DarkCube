/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Range;

/**
 * Something that can provide hue, saturation, and value color components.
 *
 * <p>Provided values should be in the range [0, 1].</p>
 *
 * @since 4.6.0
 */
public interface HSVLike extends Examinable {
  /**
   * Creates a new HSVLike.
   *
   * @param h hue color component
   * @param s saturation color component
   * @param v value color component
   * @return a new HSVLike
   * @since 4.10.0
   */
  static @NotNull HSVLike hsvLike(final float h, final float s, final float v) {
    return new HSVLikeImpl(h, s, v);
  }

  /**
   * Creates a new HSVLike.
   *
   * @param h hue color component
   * @param s saturation color component
   * @param v value color component
   * @return a new HSVLike
   * @since 4.6.0
   * @deprecated for removal since 4.10.0, use {@link #hsvLike(float, float, float)} instead.
   */
  @Deprecated
  @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
  static @NotNull HSVLike of(final float h, final float s, final float v) {
    return new HSVLikeImpl(h, s, v);
  }

  /**
   * Creates a new HSVLike from the given red, green, and blue color components.
   *
   * @param red red color component
   * @param green green color component
   * @param blue blue color component
   * @return a new HSVLike
   * @since 4.6.0
   */
  static @NotNull HSVLike fromRGB(@Range(from = 0x0, to = 0xff) final int red, @Range(from = 0x0, to = 0xff) final int green, @Range(from = 0x0, to = 0xff) final int blue) {
    final float r = red / 255.0f;
    final float g = green / 255.0f;
    final float b = blue / 255.0f;

    final float min = Math.min(r, Math.min(g, b));
    final float max = Math.max(r, Math.max(g, b)); // v
    final float delta = max - min;

    final float s;
    if (max != 0) {
      s = delta / max; // s
    } else {
      // r = g = b = 0
      s = 0;
    }
    if (s == 0) { // s = 0, h is undefined
      return new HSVLikeImpl(0, s, max);
    }

    float h;
    if (r == max) {
      h = (g - b) / delta; // between yellow & magenta
    } else if (g == max) {
      h = 2 + (b - r) / delta; // between cyan & yellow
    } else {
      h = 4 + (r - g) / delta; // between magenta & cyan
    }
    h *= 60; // degrees
    if (h < 0) {
      h += 360;
    }

    return new HSVLikeImpl(h / 360.0f, s, max);
  }

  /**
   * Gets the hue component.
   *
   * @return the hue component
   * @since 4.6.0
   */
  float h();

  /**
   * Gets the saturation component.
   *
   * @return the saturation component
   * @since 4.6.0
   */
  float s();

  /**
   * Gets the value component.
   *
   * @return the value component
   * @since 4.6.0
   */
  float v();

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("h", this.h()),
      ExaminableProperty.of("s", this.s()),
      ExaminableProperty.of("v", this.v())
    );
  }
}

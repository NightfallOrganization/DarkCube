/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Range;

/**
 * Something that can provide red, green, and blue colour components.
 *
 * @since 4.0.0
 */
public interface RGBLike {
  /**
   * Gets the red component.
   *
   * @return the red component
   * @since 4.0.0
   */
  @Range(from = 0x0, to = 0xff) int red();

  /**
   * Gets the green component.
   *
   * @return the green component
   * @since 4.0.0
   */
  @Range(from = 0x0, to = 0xff) int green();

  /**
   * Gets the blue component.
   *
   * @return the blue component
   * @since 4.0.0
   */
  @Range(from = 0x0, to = 0xff) int blue();

  /**
   * Converts the color represented by this RGBLike to the HSV color space.
   *
   * @return an HSVLike representing this RGBLike in the HSV color space
   * @since 4.6.0
   */
  default @NotNull HSVLike asHSV() {
    return HSVLike.fromRGB(this.red(), this.green(), this.blue());
  }
}

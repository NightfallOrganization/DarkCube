/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.util.Objects;

import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class HSVLikeImpl implements HSVLike {
  private final float h;
  private final float s;
  private final float v;

  HSVLikeImpl(final float h, final float s, final float v) {
    requireInsideRange(h, "h");
    requireInsideRange(s, "s");
    requireInsideRange(v, "v");
    this.h = h;
    this.s = s;
    this.v = v;
  }

  @Override
  public float h() {
    return this.h;
  }

  @Override
  public float s() {
    return this.s;
  }

  @Override
  public float v() {
    return this.v;
  }

  private static void requireInsideRange(final float number, final String name) throws IllegalArgumentException {
    if (number < 0 || 1 < number) {
      throw new IllegalArgumentException(
        name + " (" + number + ")" + " is not inside the required range: [0,1]");
    }
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof HSVLikeImpl)) return false;
    final HSVLikeImpl that = (HSVLikeImpl) other;
    return ShadyPines.equals(that.h, this.h) && ShadyPines.equals(that.s, this.s) && ShadyPines.equals(that.v, this.v);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.h, this.s, this.v);
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }
}

/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.format;

import eu.darkcube.system.libs.org.jetbrains.annotations.Debug;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

@Debug.Renderer(text = "asHexString()")
final class TextColorImpl implements TextColor {
  private final int value;

  TextColorImpl(final int value) {
    this.value = value;
  }

  @Override
  public int value() {
    return this.value;
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof TextColorImpl)) return false;
    final TextColorImpl that = (TextColorImpl) other;
    return this.value == that.value;
  }

  @Override
  public int hashCode() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.asHexString();
  }
}

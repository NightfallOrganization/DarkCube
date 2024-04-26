/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

final class EndBinaryTagImpl extends AbstractBinaryTag implements EndBinaryTag {
  static final EndBinaryTagImpl INSTANCE = new EndBinaryTagImpl();

  @Override
  public boolean equals(final Object that) {
    return this == that;
  }

  @Override
  public int hashCode() {
    return 0;
  }
}

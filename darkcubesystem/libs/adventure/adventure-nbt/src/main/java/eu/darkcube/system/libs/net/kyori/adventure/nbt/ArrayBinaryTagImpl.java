/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

abstract class ArrayBinaryTagImpl extends AbstractBinaryTag implements ArrayBinaryTag {
  static void checkIndex(final int index, final int length) {
    if (index < 0 || index >= length) {
      throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }
  }
}

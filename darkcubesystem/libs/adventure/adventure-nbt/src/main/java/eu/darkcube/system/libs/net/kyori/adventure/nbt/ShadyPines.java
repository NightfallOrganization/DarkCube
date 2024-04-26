/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

final class ShadyPines {
  private ShadyPines() {
  }

  static int floor(final double dv) {
    final int iv = (int) dv;
    return dv < (double) iv ? iv - 1 : iv;
  }

  static int floor(final float fv) {
    final int iv = (int) fv;
    return fv < (float) iv ? iv - 1 : iv;
  }
}

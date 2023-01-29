/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.util.ServiceLoader;

final class Services0 {
  private Services0() {
  }

  static <S> ServiceLoader<S> loader(final Class<S> type) {
    return ServiceLoader.load(type, type.getClassLoader());
  }
}

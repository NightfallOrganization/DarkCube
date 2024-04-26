/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.io.IOException;

interface BinaryTagScope extends AutoCloseable {
  @Override
  void close() throws IOException;

  final class NoOp implements BinaryTagScope {
    static final NoOp INSTANCE = new NoOp();

    private NoOp() {
    }

    @Override
    public void close() {

    }
  }
}

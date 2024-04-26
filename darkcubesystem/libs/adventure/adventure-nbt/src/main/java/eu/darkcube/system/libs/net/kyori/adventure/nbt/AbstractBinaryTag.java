/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import eu.darkcube.system.libs.net.kyori.examination.string.StringExaminer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

abstract class AbstractBinaryTag implements BinaryTag {
  @Override
  public final @NotNull String examinableName() {
    return this.type().toString();
  }

  @Override
  public final String toString() {
    return this.examine(StringExaminer.simpleEscaping());
  }
}

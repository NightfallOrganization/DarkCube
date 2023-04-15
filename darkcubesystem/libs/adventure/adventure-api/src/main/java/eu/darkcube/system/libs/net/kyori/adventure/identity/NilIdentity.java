/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.identity;

import java.util.UUID;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class NilIdentity implements Identity {
  static final UUID NIL_UUID = new UUID(0, 0);
  static final Identity INSTANCE = new NilIdentity();

  @Override
  public @NotNull UUID uuid() {
    return NIL_UUID;
  }

  @Override
  public String toString() {
    return "Identity.nil()";
  }

  @Override
  public boolean equals(final @Nullable Object that) {
    return this == that;
  }

  @Override
  public int hashCode() {
    return 0;
  }
}

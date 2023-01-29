/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.pointer;

import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class PointerImpl<T> implements Pointer<T> {
  private final Class<T> type;
  private final Key key;

  PointerImpl(final Class<T> type, final Key key) {
    this.type = type;
    this.key = key;
  }

  @Override
  public @NotNull Class<T> type() {
    return this.type;
  }

  @Override
  public @NotNull Key key() {
    return this.key;
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || this.getClass() != other.getClass()) return false;
    final PointerImpl<?> that = (PointerImpl<?>) other;
    return this.type.equals(that.type) && this.key.equals(that.key);
  }

  @Override
  public int hashCode() {
    int result = this.type.hashCode();
    result = (31 * result) + this.key.hashCode();
    return result;
  }
}

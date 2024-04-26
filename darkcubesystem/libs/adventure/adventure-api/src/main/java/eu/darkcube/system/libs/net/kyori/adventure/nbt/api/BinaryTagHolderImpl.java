/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt.api;

import eu.darkcube.system.libs.net.kyori.adventure.util.Codec;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

final class BinaryTagHolderImpl implements BinaryTagHolder {
  private final String string;

  BinaryTagHolderImpl(final String string) {
    this.string = requireNonNull(string, "string");
  }

  @Override
  public @NotNull String string() {
    return this.string;
  }

  @Override
  public <T, DX extends Exception> @NotNull T get(final @NotNull Codec<T, String, DX, ?> codec) throws DX {
    return codec.decode(this.string);
  }

  @Override
  public int hashCode() {
    return 31 * this.string.hashCode();
  }

  @Override
  public boolean equals(final Object that) {
    if (!(that instanceof BinaryTagHolderImpl)) {
      return false;
    }

    return this.string.equals(((BinaryTagHolderImpl) that).string);
  }

  @Override
  public String toString() {
    return this.string;
  }
}

/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.identity;

import java.util.UUID;

import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class IdentityImpl implements Examinable, Identity {
  private final UUID uuid;

  IdentityImpl(final UUID uuid) {
    this.uuid = uuid;
  }

  @Override
  public @NotNull UUID uuid() {
    return this.uuid;
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof Identity)) return false;
    final Identity that = (Identity) other;
    return this.uuid.equals(that.uuid());
  }

  @Override
  public int hashCode() {
    return this.uuid.hashCode();
  }
}

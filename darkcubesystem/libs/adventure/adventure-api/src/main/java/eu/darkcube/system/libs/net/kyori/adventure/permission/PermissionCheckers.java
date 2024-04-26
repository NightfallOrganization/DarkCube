/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.permission;

import eu.darkcube.system.libs.net.kyori.adventure.util.TriState;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class PermissionCheckers {
  static final PermissionChecker NOT_SET = new Always(TriState.NOT_SET);
  static final PermissionChecker FALSE = new Always(TriState.FALSE);
  static final PermissionChecker TRUE = new Always(TriState.TRUE);

  private PermissionCheckers() {
  }

  private static final class Always implements PermissionChecker {
    private final TriState value;

    private Always(final TriState value) {
      this.value = value;
    }

    @Override
    public @NotNull TriState value(final String permission) {
      return this.value;
    }

    @Override
    public String toString() {
      return PermissionChecker.class.getSimpleName() + ".always(" + this.value + ")";
    }

    @Override
    public boolean equals(final @Nullable Object other) {
      if (this == other) return true;
      if (other == null || this.getClass() != other.getClass()) return false;
      final Always always = (Always) other;
      return this.value == always.value;
    }

    @Override
    public int hashCode() {
      return this.value.hashCode();
    }
  }
}

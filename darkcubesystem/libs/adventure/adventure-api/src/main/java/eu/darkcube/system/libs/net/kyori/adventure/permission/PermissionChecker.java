/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.permission;

import java.util.function.Predicate;

import eu.darkcube.system.libs.net.kyori.adventure.Adventure;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.pointer.Pointer;
import eu.darkcube.system.libs.net.kyori.adventure.util.TriState;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Something that has permissions.
 *
 * @since 4.8.0
 */
public interface PermissionChecker extends Predicate<String> {
  /**
   * A pointer to a permission predicate.
   *
   * @since 4.8.0
   */
  Pointer<PermissionChecker> POINTER = Pointer.pointer(PermissionChecker.class, Key.key(Adventure.NAMESPACE, "permission"));

  /**
   * Creates a {@link PermissionChecker} that always returns {@code state}.
   *
   * @param state the state
   * @return a {@link PermissionChecker}
   * @since 4.8.0
   */
  static @NotNull PermissionChecker always(final TriState state) {
    if (state == TriState.TRUE) return PermissionCheckers.TRUE;
    if (state == TriState.FALSE) return PermissionCheckers.FALSE;
    return PermissionCheckers.NOT_SET;
  }

  /**
   * Checks if something has a permission.
   *
   * @param permission the permission
   * @return a tri-state result
   * @since 4.8.0
   */
  @NotNull TriState value(final String permission);

  @Override
  default boolean test(final String permission) {
    return this.value(permission) == TriState.TRUE;
  }
}

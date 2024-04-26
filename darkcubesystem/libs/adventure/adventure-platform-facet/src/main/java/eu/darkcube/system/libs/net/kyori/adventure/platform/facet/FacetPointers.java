/*
 * Copyright (c) 2018-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.platform.facet;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.pointer.Pointer;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

/**
 * Pointers for facet-specific data.
 *
 * @since 4.0.0
 */
@ApiStatus.Internal
public final class FacetPointers {
  private FacetPointers() {
  }

  private static final String NAMESPACE = "adventure_platform";
  public static final Pointer<String> SERVER = Pointer.pointer(String.class, Key.key(NAMESPACE, "server"));
  public static final Pointer<Key> WORLD = Pointer.pointer(Key.class, Key.key(NAMESPACE, "world"));
  public static final Pointer<Type> TYPE = Pointer.pointer(Type.class, Key.key(NAMESPACE, "type"));

  /**
   * Types of audience that may receive special handling.
   *
   * @since 4.0.0
   */
  public enum Type {
    PLAYER,
    CONSOLE,
    OTHER
  }
}

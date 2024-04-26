/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.key;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * This exception is thrown when an invalid namespace and/or value has been detected while creating a {@link Key}.
 *
 * @since 4.0.0
 */
public final class InvalidKeyException extends RuntimeException {
  private static final long serialVersionUID = -5413304087321449434L;
  private final String keyNamespace;
  private final String keyValue;

  InvalidKeyException(final @NotNull String keyNamespace, final @NotNull String keyValue, final @Nullable String message) {
    super(message);
    this.keyNamespace = keyNamespace;
    this.keyValue = keyValue;
  }

  /**
   * Gets the invalid key, as a string.
   *
   * @return a key
   * @since 4.0.0
   */
  public final @NotNull String keyNamespace() {
    return this.keyNamespace;
  }

  /**
   * Gets the invalid key, as a string.
   *
   * @return a key
   * @since 4.0.0
   */
  public final @NotNull String keyValue() {
    return this.keyValue;
  }
}

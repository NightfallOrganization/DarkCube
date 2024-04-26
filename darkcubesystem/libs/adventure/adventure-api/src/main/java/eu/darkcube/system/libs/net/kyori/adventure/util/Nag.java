/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A nag.
 *
 * @since 4.7.0
 */
public abstract class Nag extends RuntimeException {
  private static final long serialVersionUID = -695562541413409498L;

  /**
   * Prints a nag.
   *
   * @param nag the nag
   * @since 4.7.0
   */
  public static void print(final @NotNull Nag nag) {
    nag.printStackTrace();
  }

  /**
   * Constructs with a message.
   *
   * @param message the message
   * @since 4.7.0
   */
  protected Nag(final String message) {
    super(message);
  }
}

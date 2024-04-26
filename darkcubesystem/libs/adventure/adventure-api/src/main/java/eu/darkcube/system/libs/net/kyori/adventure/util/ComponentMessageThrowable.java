/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * An extension interface for {@link Throwable}s to provide a {@link Component}-based message.
 *
 * @since 4.0.0
 */
public interface ComponentMessageThrowable {
  /**
   * Gets the {@link Component}-based message from a {@link Throwable}, if available.
   *
   * @param throwable the throwable
   * @return the message
   * @since 4.0.0
   */
  @SuppressWarnings("checkstyle:MethodName")
  static @Nullable Component getMessage(final @Nullable Throwable throwable) {
    if (throwable instanceof ComponentMessageThrowable) {
      return ((ComponentMessageThrowable) throwable).componentMessage();
    }
    return null;
  }

  /**
   * Gets the {@link Component}-based message from a {@link Throwable}, or converts {@link Throwable#getMessage()}.
   *
   * @param throwable the throwable
   * @return the message
   * @since 4.0.0
   */
  @SuppressWarnings("checkstyle:MethodName")
  static @Nullable Component getOrConvertMessage(final @Nullable Throwable throwable) {
    if (throwable instanceof ComponentMessageThrowable) {
      return ((ComponentMessageThrowable) throwable).componentMessage();
    } else if (throwable != null) {
      final String message = throwable.getMessage();
      if (message != null) {
        return Component.text(message);
      }
    }
    return null;
  }

  /**
   * Gets the message.
   *
   * @return the message
   * @since 4.0.0
   */
  @Nullable Component componentMessage();
}

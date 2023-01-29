/*
 * Copyright (c) 2018-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.platform.facet;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * Facet utilities and logging pipeline.
 *
 * <p>This is not supported API. Subject to change at any time.</p>
 *
 * @since 4.0.0
 */
public final class Knob {
  private Knob() {
  }

  private static final String NAMESPACE =
    "net.kyo".concat("ri.adventure"); // Concat is used to trick package relocations
  public static final boolean DEBUG = isEnabled("debug", false);
  private static final Set<Object> UNSUPPORTED = new CopyOnWriteArraySet<>();

  public static volatile Consumer<String> OUT = System.out::println;
  public static volatile BiConsumer<String, Throwable> ERR =
    (message, err) -> {
      System.err.println(message);
      if (err != null) {
        err.printStackTrace(System.err);
      }
    };

  /**
   * Gets whether a facet should be enabled.
   *
   * <p>Use the JVM flag, {@code -Dnet.kyori.adventure.<key>=true}, to enable the facet.</p>
   *
   * @param key a key
   * @param defaultValue the default value
   * @return if the feature is enabled
   * @since 4.0.0
   */
  public static boolean isEnabled(final @NotNull String key, final boolean defaultValue) {
    return System.getProperty(NAMESPACE + "." + key, Boolean.toString(defaultValue))
      .equalsIgnoreCase("true");
  }

  /**
   * Logs an error.
   *
   * @param error an error
   * @param format a string format
   * @param arguments an array of arguments
   * @since 4.0.0
   */
  public static void logError(final @Nullable Throwable error, final @NotNull String format, final @NotNull Object... arguments) {
    if (DEBUG) {
      ERR.accept(String.format(format, arguments), error);
    }
  }

  /**
   * Logs a message.
   *
   * @param format a string format
   * @param arguments an array of arguments
   * @since 4.0.0
   */
  public static void logMessage(final @NotNull String format, final @NotNull Object... arguments) {
    if (DEBUG) {
      OUT.accept(String.format(format, arguments));
    }
  }

  /**
   * Logs an unsupported value.
   *
   * @param facet a facet
   * @param value a value
   * @since 4.0.0
   */
  public static void logUnsupported(final @NotNull Object facet, final @NotNull Object value) {
    if (DEBUG && UNSUPPORTED.add(value)) {
      OUT.accept(String.format("Unsupported value '%s' for facet: %s", value, facet));
    }
  }
}

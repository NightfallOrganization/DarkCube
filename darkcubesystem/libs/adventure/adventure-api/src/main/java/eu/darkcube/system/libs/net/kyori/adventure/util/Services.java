/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;

import eu.darkcube.system.libs.net.kyori.adventure.internal.properties.AdventureProperties;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Tools for working with {@link ServiceLoader}s.
 *
 * @since 4.8.0
 */
public final class Services {
  private static final boolean SERVICE_LOAD_FAILURES_ARE_FATAL = Boolean.TRUE.equals(
		  AdventureProperties.SERVICE_LOAD_FAILURES_ARE_FATAL.value());

  private Services() {
  }

  /**
   * Locates a service.
   *
   * @param type the service type
   * @param <P> the service type
   * @return a service, or {@link Optional#empty()}
   * @since 4.8.0
   */
  public static <P> @NotNull Optional<P> service(final @NotNull Class<P> type) {
    final ServiceLoader<P> loader = Services0.loader(type);
    final Iterator<P> it = loader.iterator();
    while (it.hasNext()) {
      final P instance;
      try {
        instance = it.next();
      } catch (final Throwable t) {
        if (SERVICE_LOAD_FAILURES_ARE_FATAL) {
          throw new IllegalStateException("Encountered an exception loading service " + type, t);
        } else {
          continue;
        }
      }
      if (it.hasNext()) {
        throw new IllegalStateException("Expected to find one service " + type + ", found multiple");
      }
      return Optional.of(instance);
    }
    return Optional.empty();
  }
}

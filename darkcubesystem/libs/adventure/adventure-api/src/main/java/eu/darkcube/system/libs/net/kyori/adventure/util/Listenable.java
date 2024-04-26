/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Something that has listeners.
 *
 * @param <L> the listener type
 * @since 4.0.0
 */
public abstract class Listenable<L> {
  private final List<L> listeners = new CopyOnWriteArrayList<>();

  /**
   * Process an action for each listener.
   *
   * @param consumer the consumer
   * @since 4.0.0
   */
  protected final void forEachListener(final @NotNull Consumer<L> consumer) {
    for (final L listener : this.listeners) {
      consumer.accept(listener);
    }
  }

  /**
   * Adds a listener.
   *
   * @param listener the listener
   * @since 4.0.0
   */
  protected final void addListener0(final @NotNull L listener) {
    this.listeners.add(listener);
  }

  /**
   * Removes a listener.
   *
   * @param listener the listener
   * @since 4.0.0
   */
  protected final void removeListener0(final @NotNull L listener) {
    this.listeners.remove(listener);
  }
}

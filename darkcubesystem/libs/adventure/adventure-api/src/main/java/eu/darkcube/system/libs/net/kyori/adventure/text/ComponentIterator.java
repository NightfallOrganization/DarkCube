/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

final class ComponentIterator implements Iterator<Component> {
  private Component component;
  private final ComponentIteratorType type;
  private final Set<ComponentIteratorFlag> flags;
  private final Deque<Component> deque;

  ComponentIterator(final @NotNull Component component, final @NotNull ComponentIteratorType type, final @NotNull Set<ComponentIteratorFlag> flags) {
    this.component = component;
    this.type = type;
    this.flags = flags;
    this.deque = new ArrayDeque<>();
  }

  @Override
  public boolean hasNext() {
    return this.component != null || !this.deque.isEmpty();
  }

  @Override
  public Component next() {
    if (this.component != null) {
      final Component next = this.component;
      this.component = null;
      this.type.populate(next, this.deque, this.flags);
      return next;
    } else {
      if (this.deque.isEmpty()) throw new NoSuchElementException();
      this.component = this.deque.poll();
      return this.next();
    }
  }
}

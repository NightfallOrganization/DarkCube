/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.Deque;
import java.util.List;
import java.util.Set;

import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * The iterator types.
 *
 * @see Component#iterator(ComponentIteratorType, Set)
 * @see Component#iterable(ComponentIteratorType, Set)
 * @see Component#spliterator(ComponentIteratorType, Set)
 * @since 4.9.0
 */
@ApiStatus.NonExtendable
@FunctionalInterface
public interface ComponentIteratorType {
  /**
   * A depth-first iteration.
   *
   * @since 4.9.0
   */
  ComponentIteratorType DEPTH_FIRST = (component, deque, flags) -> {
    if (flags.contains(ComponentIteratorFlag.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS) && component instanceof TranslatableComponent) {
      final TranslatableComponent translatable = (TranslatableComponent) component;
      final List<Component> args = translatable.args();

      for (int i = args.size() - 1; i >= 0; i--) {
        deque.addFirst(args.get(i));
      }
    }

    final HoverEvent<?> hoverEvent = component.hoverEvent();
    if (hoverEvent != null) {
      final HoverEvent.Action<?> action = hoverEvent.action();

      if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_ENTITY_NAME) && action == HoverEvent.Action.SHOW_ENTITY) {
        deque.addFirst(((HoverEvent.ShowEntity) hoverEvent.value()).name());
      } else if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_TEXT_COMPONENT) && action == HoverEvent.Action.SHOW_TEXT) {
        deque.addFirst((Component) hoverEvent.value());
      }
    }

    final List<Component> children = component.children();
    for (int i = children.size() - 1; i >= 0; i--) {
      deque.addFirst(children.get(i));
    }
  };
  /**
   * A breadth-first iteration.
   *
   * @since 4.9.0
   */
  ComponentIteratorType BREADTH_FIRST = (component, deque, flags) -> {
    if (flags.contains(ComponentIteratorFlag.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS) && component instanceof TranslatableComponent) {
      deque.addAll(((TranslatableComponent) component).args());
    }

    final HoverEvent<?> hoverEvent = component.hoverEvent();
    if (hoverEvent != null) {
      final HoverEvent.Action<?> action = hoverEvent.action();

      if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_ENTITY_NAME) && action == HoverEvent.Action.SHOW_ENTITY) {
        deque.addLast(((HoverEvent.ShowEntity) hoverEvent.value()).name());
      } else if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_TEXT_COMPONENT) && action == HoverEvent.Action.SHOW_TEXT) {
        deque.addLast((Component) hoverEvent.value());
      }
    }

    deque.addAll(component.children());
  };

  /**
   * Populates a deque with the children of the provided component, based on the iterator type and flags.
   *
   * @param component the component
   * @param deque the deque
   * @param flags the flags
   * @since 4.9.0
   */
  void populate(final @NotNull Component component, final @NotNull Deque<Component> deque, final @NotNull Set<ComponentIteratorFlag> flags);
}

/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent.Action;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

/**
 * Flags to modify the behaviour of a component iterator.
 *
 * @see Component#iterator(ComponentIteratorType, java.util.Set)
 * @see Component#iterable(ComponentIteratorType, java.util.Set)
 * @see Component#spliterator(ComponentIteratorType, java.util.Set)
 * @since 4.9.0
 */
@ApiStatus.NonExtendable
public enum ComponentIteratorFlag {
  /**
   * Includes the name of entities inside {@link Action#SHOW_ENTITY entity} hover events.
   *
   * @since 4.9.0
   */
  INCLUDE_HOVER_SHOW_ENTITY_NAME,
  /**
   * Includes the components inside {@link Action#SHOW_TEXT text} hover events.
   *
   * @since 4.9.0
   */
  INCLUDE_HOVER_SHOW_TEXT_COMPONENT,
  /**
   * Includes the arguments of {@link TranslatableComponent translatable components}.
   *
   * @since 4.9.0
   */
  INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS;
}

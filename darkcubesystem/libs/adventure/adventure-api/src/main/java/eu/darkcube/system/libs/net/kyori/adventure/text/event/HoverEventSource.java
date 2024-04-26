/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.event;

import java.util.function.UnaryOperator;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * Something that can provide a {@link HoverEvent}.
 *
 * @param <V> the value type
 * @since 4.0.0
 */
public interface HoverEventSource<V> {
  /**
   * Fetches a {@link HoverEvent} from a {@code HoverEventSource}.
   *
   * @param source the hover event source
   * @param <V> the value type
   * @return a hover event, or {@code null}
   * @since 4.0.0
   */
  static <V> @Nullable HoverEvent<V> unbox(final @Nullable HoverEventSource<V> source) {
    return source != null ? source.asHoverEvent() : null;
  }

  /**
   * Represent this object as a hover event.
   *
   * @return a hover event
   * @since 4.0.0
   */
  default @NotNull HoverEvent<V> asHoverEvent() {
    return this.asHoverEvent(UnaryOperator.identity());
  }

  /**
   * Creates a hover event with value derived from this object.
   *
   * <p>The event value will be passed through the provided callback to allow
   * transforming the original value of the event.</p>
   *
   * @param op transformation on value
   * @return a hover event
   * @since 4.0.0
   */
  @NotNull HoverEvent<V> asHoverEvent(final @NotNull UnaryOperator<V> op);
}

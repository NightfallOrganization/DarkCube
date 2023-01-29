/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Something that can be represented as a {@link Component}.
 *
 * @since 4.0.0
 */
@FunctionalInterface
public interface ComponentLike {
  /**
   * Converts a list of {@link ComponentLike}s to a list of {@link Component}s.
   *
   * @param likes the component-likes
   * @return the components
   * @since 4.8.0
   */
  static @NotNull List<Component> asComponents(final @NotNull List<? extends ComponentLike> likes) {
    return asComponents(likes, null);
  }

  /**
   * Converts a list of {@link ComponentLike}s to a list of {@link Component}s.
   *
   * <p>Only components that match {@code filter} will be returned.</p>
   *
   * @param likes the component-likes
   * @param filter the component filter
   * @return the components
   * @since 4.8.0
   */
  static @NotNull List<Component> asComponents(final @NotNull List<? extends ComponentLike> likes, final @Nullable Predicate<? super Component> filter) {
    requireNonNull(likes, "likes");
    final int size = likes.size();
    if (size == 0) {
      // We do not need to create a new list if the one we are copying is empty - we can
      // simply just return our known-empty list instead.
      return Collections.emptyList();
    }
    @Nullable ArrayList<Component> components = null;
    for (int i = 0; i < size; i++) {
      final @Nullable ComponentLike like = likes.get(i);
      if (like == null) {
        throw new NullPointerException("likes[" + i + "]");
      }
      final Component component = like.asComponent();
      if (filter == null || filter.test(component)) {
        if (components == null) {
          components = new ArrayList<>(size);
        }
        components.add(component);
      }
    }
    // if we filtered all elements out, just use an empty list instead
    if (components == null) return Collections.emptyList();
    // https://github.com/KyoriPowered/adventure/pull/327#discussion_r631420264
    // we pre-size the list, but filtering might make the actual size much smaller
    components.trimToSize();
    return Collections.unmodifiableList(components);
  }

  /**
   * Fetches a {@link Component} from a {@code ComponentLike}.
   *
   * @param like the component-like
   * @return a component, or {@code null}
   * @since 4.8.0
   */
  static @Nullable Component unbox(final @Nullable ComponentLike like) {
    return like != null ? like.asComponent() : null;
  }

  /**
   * Gets a {@link Component} representation.
   *
   * @return a component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull Component asComponent();
}

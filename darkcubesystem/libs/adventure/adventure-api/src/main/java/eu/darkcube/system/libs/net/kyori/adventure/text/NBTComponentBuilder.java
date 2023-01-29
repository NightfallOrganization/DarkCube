/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/*
 * This can't be a child of NBTComponent.
 */

/**
 * An NBT component builder.
 *
 * @param <C> component type
 * @param <B> builder type
 * @since 4.0.0
 */
public interface NBTComponentBuilder<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> extends ComponentBuilder<C, B> {
  /**
   * Sets the NBT path content.
   *
   * @param nbtPath the NBT path
   * @return this builder
   * @since 4.0.0
   */
  @Contract("_ -> this")
  @NotNull B nbtPath(final @NotNull String nbtPath);

  /**
   * Sets whether to interpret.
   *
   * @param interpret if we should be interpreting
   * @return this builder
   * @since 4.0.0
   */
  @Contract("_ -> this")
  @NotNull B interpret(final boolean interpret);

  /**
   * Sets the separator.
   *
   * @param separator the separator
   * @return this builder
   * @since 4.8.0
   */
  @Contract("_ -> this")
  @NotNull B separator(final @Nullable ComponentLike separator);
}

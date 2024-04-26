/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Given a Minecraft selector, this component reads the NBT of the associated entity and displays that information.
 *
 * <p>This component consists of:</p>
 * <dl>
 *   <dt>selector</dt>
 *   <dd>a Minecraft selector.(e.g {@code @p}, {@code @r})</dd>
 *   <dt>everything in</dt>
 *   <dd>{@link NBTComponent}</dd>
 * </dl>
 *
 * @see NBTComponent
 * @since 4.0.0
 * @sinceMinecraft 1.14
 */
public interface EntityNBTComponent extends NBTComponent<EntityNBTComponent, EntityNBTComponent.Builder>, ScopedComponent<EntityNBTComponent> {
  /**
   * Gets the entity selector.
   *
   * @return the entity selector
   * @since 4.0.0
   */
  @NotNull String selector();

  /**
   * Sets the entity selector.
   *
   * @param selector the entity selector
   * @return an entity NBT component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull EntityNBTComponent selector(final @NotNull String selector);

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
      Stream.of(
        ExaminableProperty.of("selector", this.selector())
      ),
      NBTComponent.super.examinableProperties()
    );
  }

  /**
   * An entity NBT component builder.
   *
   * @since 4.0.0
   */
  interface Builder extends NBTComponentBuilder<EntityNBTComponent, Builder> {
    /**
     * Sets the entity selector.
     *
     * @param selector the entity selector
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder selector(final @NotNull String selector);
  }
}

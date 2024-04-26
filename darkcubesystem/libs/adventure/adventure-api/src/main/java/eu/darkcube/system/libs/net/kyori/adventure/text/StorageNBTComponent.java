/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Given a {@link Key}, this component reads the NBT of the associated command storage and displays that information.
 *
 * <p>This component consists of:</p>
 * <dl>
 *   <dt>storage</dt>
 *   <dd>a key that represents the resource location of a command storage (eg. my_plugin:actions.punches_entity)</dd>
 *   <dt>everything in</dt>
 *   <dd>{@link NBTComponent}</dd>
 * </dl>
 *
 * @see NBTComponent
 * @since 4.0.0
 * @sinceMinecraft 1.15
 */
public interface StorageNBTComponent extends NBTComponent<StorageNBTComponent, StorageNBTComponent.Builder>, ScopedComponent<StorageNBTComponent> {
  /**
   * Gets the NBT storage's ID.
   *
   * @return the NBT storage
   * @since 4.0.0
   */
  @NotNull Key storage();

  /**
   * Sets the NBT storage.
   *
   * @param storage the identifier of the NBT storage
   * @return a storage NBT component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull StorageNBTComponent storage(final @NotNull Key storage);

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
      Stream.of(
        ExaminableProperty.of("storage", this.storage())
      ),
      NBTComponent.super.examinableProperties()
    );
  }

  /**
   * A command storage NBT component builder.
   *
   * @since 4.0.0
   */
  interface Builder extends NBTComponentBuilder<StorageNBTComponent, Builder> {
    /**
     * Sets the NBT storage.
     *
     * @param storage the id of the NBT storage
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder storage(final @NotNull Key storage);
  }
}

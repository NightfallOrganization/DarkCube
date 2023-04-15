/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * A component that can display NBT fetched from different locations, optionally trying to interpret the NBT as JSON
 * using the {@code net.kyori.adventure.text.serializer.gson.GsonComponentSerializer} to convert the JSON to a {@link Component}.
 * Sending interpreted NBT to the chat would be similar to using {@code /tellraw}.
 *
 * <p>This component consists of:</p>
 * <dl>
 *   <dt>nbtPath</dt>
 *   <dd>a path to specify which parts of the nbt you want displayed(<a href="https://minecraft.gamepedia.com/NBT_path_format#Examples">examples</a>).</dd>
 *   <dt>interpret</dt>
 *   <dd>a boolean telling adventure if the fetched NBT value should be parsed as JSON</dd>
 * </dl>
 *
 * <p>This component is rendered serverside and can therefore receive platform-defined
 * context. See the documentation for your respective
 * platform for more info</p>
 *
 * @param <C> component type
 * @param <B> builder type
 * @since 4.0.0
 * @sinceMinecraft 1.14
 */
public interface NBTComponent<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> extends BuildableComponent<C, B> {
  /**
   * Gets the NBT path.
   *
   * @return the NBT path
   * @since 4.0.0
   */
  @NotNull String nbtPath();

  /**
   * Sets the NBT path.
   *
   * @param nbtPath the NBT path
   * @return an NBT component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull C nbtPath(final @NotNull String nbtPath);

  /**
   * Gets if we should be interpreting.
   *
   * @return if we should be interpreting
   * @since 4.0.0
   */
  boolean interpret();

  /**
   * Sets if we should be interpreting.
   *
   * @param interpret if we should be interpreting.
   * @return an NBT component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull C interpret(final boolean interpret);

  /**
   * Gets the separator.
   *
   * @return the separator
   * @since 4.8.0
   */
  @Nullable Component separator();

  /**
   * Sets the separator.
   *
   * @param separator the separator
   * @return the separator
   * @since 4.8.0
   */
  @NotNull C separator(final @Nullable ComponentLike separator);

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
      Stream.of(
        ExaminableProperty.of("nbtPath", this.nbtPath()),
        ExaminableProperty.of("interpret", this.interpret()),
        ExaminableProperty.of("separator", this.separator())
      ),
      BuildableComponent.super.examinableProperties()
    );
  }
}

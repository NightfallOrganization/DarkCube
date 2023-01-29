/*
 * Copyright (c) 2018-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.craftbukkit;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.ComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A component serializer for {@code net.minecraft.server.<version>.IChatBaseComponent}.
 *
 * <p>Due to Bukkit version namespaces, the return type does not reflect the actual type.</p>
 *
 * <p>Color downsampling will be performed as necessary for the running server version.</p>
 *
 * <p>If not {@link #isSupported()}, an {@link UnsupportedOperationException} will be thrown on any serialize or deserialize operations.</p>
 *
 * @see #get()
 * @since 4.0.0
 * @deprecated for removal, use {@link eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer} instead.
 */
@Deprecated
public final class MinecraftComponentSerializer implements ComponentSerializer<Component, Component, Object> {
  private static final MinecraftComponentSerializer INSTANCE = new MinecraftComponentSerializer();

  /**
   * Gets whether this serializer is supported.
   *
   * @return if the serializer is supported.
   * @since 4.0.0
   */
  public static boolean isSupported() {
    return eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer.isSupported();
  }

  /**
   * Gets the component serializer.
   *
   * @return a component serializer
   * @since 4.0.0
   */
  public static @NotNull MinecraftComponentSerializer get() {
    return INSTANCE;
  }

  private final eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
          realSerial = eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer.get();

  @Override
  public @NotNull Component deserialize(final @NotNull Object input) {
    return this.realSerial.deserialize(input);
  }

  @Override
  public @NotNull Object serialize(final @NotNull Component component) {
    return this.realSerial.serialize(component);
  }
}

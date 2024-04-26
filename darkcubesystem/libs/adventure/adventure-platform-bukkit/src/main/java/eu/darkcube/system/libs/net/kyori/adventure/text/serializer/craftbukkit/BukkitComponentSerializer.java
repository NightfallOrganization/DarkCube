/*
 * Copyright (c) 2018-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.craftbukkit;

import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A pair of component serializers for {@link org.bukkit.Bukkit}.
 *
 * @since 4.0.0
 * @deprecated for removal, use {@link eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer} instead
 */
@Deprecated
public final class BukkitComponentSerializer {
  private BukkitComponentSerializer() {
  }

  /**
   * Gets the legacy component serializer.
   *
   * @return a legacy component serializer
   * @since 4.0.0
   */
  public static @NotNull LegacyComponentSerializer legacy() {
    return eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer.legacy();
  }

  /**
   * Gets the gson component serializer.
   *
   * <p>Not available on servers before 1.8, will be {@code null}.</p>
   *
   * @return a gson component serializer
   * @since 4.0.0
   */
  public static @NotNull GsonComponentSerializer gson() {
    return eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer.gson();
  }
}

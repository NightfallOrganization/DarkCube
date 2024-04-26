/*
 * Copyright (c) 2018-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit;

import eu.darkcube.system.libs.net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Entity;

final class BukkitEmitter implements Sound.Emitter {
  final Entity entity;

  BukkitEmitter(final Entity entity) {
    this.entity = entity;
  }
}

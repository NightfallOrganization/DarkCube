/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.audience;

import eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;

/**
 * Message types.
 *
 * @since 4.0.0
 * @deprecated for removal since 4.12.0, use separate methods on {@link Audience} for sending player or system messages
 */
@ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
@Deprecated
public enum MessageType {
  /**
   * Chat message type.
   *
   * @since 4.0.0
   * @deprecated for removal since 4.12.0, use {@link ChatType#CHAT} instead
   */
  @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
  @Deprecated
  CHAT,
  /**
   * System message type.
   *
   * @since 4.0.0
   * @deprecated for removal since 4.12.0
   */
  @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
  @Deprecated
  SYSTEM;
}

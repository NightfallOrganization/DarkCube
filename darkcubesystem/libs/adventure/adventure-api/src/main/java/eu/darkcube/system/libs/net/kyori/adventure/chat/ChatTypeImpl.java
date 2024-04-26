/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.chat;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class ChatTypeImpl implements ChatType {
  private final Key key;

  ChatTypeImpl(final @NotNull Key key) {
    this.key = key;
  }

  @Override
  public @NotNull Key key() {
    return this.key;
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }

  static final class BoundImpl implements ChatType.Bound {
    private final ChatType chatType;
    private final Component name;
    private final @Nullable Component target;

    BoundImpl(final ChatType chatType, final Component name, final @Nullable Component target) {
      this.chatType = chatType;
      this.name = name;
      this.target = target;
    }

    @Override
    public @NotNull ChatType type() {
      return this.chatType;
    }

    @Override
    public @NotNull Component name() {
      return this.name;
    }

    @Override
    public @Nullable Component target() {
      return this.target;
    }

    @Override
    public String toString() {
      return Internals.toString(this);
    }
  }
}

/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.chat;

import java.util.stream.Stream;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.ComponentLike;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.key.Keyed;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * A type of chat.
 *
 * @since 4.12.0
 * @sinceMinecraft 1.19
 */
public interface ChatType extends Examinable, Keyed {
  /**
   * A chat message from a player.
   *
   * @since 4.12.0
   * @sinceMinecraft 1.19
   */
  ChatType CHAT = new ChatTypeImpl(Key.key("chat"));

  /**
   * A message send as a result of using the {@code /say} command.
   *
   * @since 4.12.0
   * @sinceMinecraft 1.19
   */
  ChatType SAY_COMMAND = new ChatTypeImpl(Key.key("say_command"));

  /**
   * A message received as a result of using the {@code /msg} command.
   *
   * @since 4.12.0
   * @sinceMinecraft 1.19
   */
  ChatType MSG_COMMAND_INCOMING = new ChatTypeImpl(Key.key("msg_command_incoming"));

  /**
   * A message sent as a result of using the {@code /msg} command.
   *
   * @since 4.12.0
   * @sinceMinecraft 1.19
   */
  ChatType MSG_COMMAND_OUTGOING = new ChatTypeImpl(Key.key("msg_command_outgoing"));

  /**
   * A message received as a result of using the {@code /teammsg} command.
   *
   * @since 4.12.0
   * @sinceMinecraft 1.19
   */
  ChatType TEAM_MSG_COMMAND_INCOMING = new ChatTypeImpl(Key.key("team_msg_command_incoming"));

  /**
   * A message sent as a result of using the {@code /teammsg} command.
   *
   * @since 4.12.0
   * @sinceMinecraft 1.19
   */
  ChatType TEAM_MSG_COMMAND_OUTGOING = new ChatTypeImpl(Key.key("team_msg_command_outgoing"));

  /**
   * A message sent as a result of using the {@code /me} command.
   *
   * @since 4.12.0
   * @sinceMinecraft 1.19
   */
  ChatType EMOTE_COMMAND = new ChatTypeImpl(Key.key("emote_command"));

  /**
   * Creates a new chat type with a given key.
   *
   * @param key the key
   * @return the chat type
   * @since 4.12.0
   */
  static @NotNull ChatType chatType(final @NotNull Keyed key) {
    return key instanceof ChatType ? (ChatType) key : new ChatTypeImpl(requireNonNull(key, "key").key());
  }

  /**
   * Creates a bound chat type with a name {@link Component}.
   *
   * @param name the name component
   * @return a new bound chat type
   * @since 4.12.0
   * @sinceMinecraft 1.19
   */
  @Contract(value = "_ -> new", pure = true)
  default ChatType.@NotNull Bound bind(final @NotNull ComponentLike name) {
    return this.bind(name, null);
  }

  /**
   * Creates a bound chat type with a name and target {@link Component}.
   *
   * @param name the name component
   * @param target the optional target component
   * @return a new bound chat type
   * @since 4.12.0
   * @sinceMinecraft 1.19
   */
  @Contract(value = "_, _ -> new", pure = true)
  default ChatType.@NotNull Bound bind(final @NotNull ComponentLike name, final @Nullable ComponentLike target) {
    return new ChatTypeImpl.BoundImpl(this, requireNonNull(name.asComponent(), "name"), ComponentLike.unbox(target));
  }

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("key", this.key()));
  }

  /**
   * A bound {@link ChatType}.
   *
   * @since 4.12.0
   * @sinceMinecraft 1.19
   */
  interface Bound extends Examinable {

    /**
     * Gets the chat type.
     *
     * @return the chat type
     * @since 4.12.0
     * @sinceMinecraft 1.19
     */
    @Contract(pure = true)
    @NotNull ChatType type();

    /**
     * Get the name component.
     *
     * @return the name component
     * @since 4.12.0
     * @sinceMinecraft 1.19
     */
    @Contract(pure = true)
    @NotNull Component name();

    /**
     * Get the target component.
     *
     * @return the target component or null
     * @since 4.12.0
     * @sinceMinecraft 1.19
     */
    @Contract(pure = true)
    @Nullable Component target();

    @Override
    default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(
        ExaminableProperty.of("type", this.type()),
        ExaminableProperty.of("name", this.name()),
        ExaminableProperty.of("target", this.target())
      );
    }
  }
}

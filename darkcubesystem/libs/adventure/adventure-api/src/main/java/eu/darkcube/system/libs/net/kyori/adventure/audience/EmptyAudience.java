/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.audience;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import eu.darkcube.system.libs.net.kyori.adventure.chat.SignedMessage;
import eu.darkcube.system.libs.net.kyori.adventure.identity.Identified;
import eu.darkcube.system.libs.net.kyori.adventure.identity.Identity;
import eu.darkcube.system.libs.net.kyori.adventure.pointer.Pointer;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.ComponentLike;
import eu.darkcube.system.libs.net.kyori.adventure.chat.ChatType;
import eu.darkcube.system.libs.net.kyori.adventure.inventory.Book;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;

final class EmptyAudience implements Audience {
  static final EmptyAudience INSTANCE = new EmptyAudience();

  @Override
  public @NotNull <T> Optional<T> get(final @NotNull Pointer<T> pointer) {
    return Optional.empty();
  }

  @Contract("_, null -> null; _, !null -> !null")
  @Override
  public <T> @Nullable T getOrDefault(final @NotNull Pointer<T> pointer, final @Nullable T defaultValue) {
    return defaultValue;
  }

  @Override
  public <T> @UnknownNullability T getOrDefaultFrom(final @NotNull Pointer<T> pointer, final @NotNull Supplier<? extends T> defaultValue) {
    return defaultValue.get();
  }

  @Override
  public @NotNull Audience filterAudience(final @NotNull Predicate<? super Audience> filter) {
    return this;
  }

  @Override
  public void forEachAudience(final @NotNull Consumer<? super Audience> action) {
  }

  @Override
  public void sendMessage(final @NotNull ComponentLike message) {
  }

  @Override
  public void sendMessage(final @NotNull Component message) {
  }

  @Override
  @Deprecated
  public void sendMessage(final @NotNull Identified source, final @NotNull Component message, final @NotNull MessageType type) {
  }

  @Override
  @Deprecated
  public void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
  }

  @Override
  public void sendMessage(final @NotNull Component message, final @NotNull ChatType.Bound boundChatType) {
  }

  @Override
  public void sendMessage(final @NotNull SignedMessage signedMessage, final @NotNull ChatType.Bound boundChatType) {
  }

  @Override
  public void deleteMessage(final SignedMessage.@NotNull Signature signature) {
  }

  @Override
  public void sendActionBar(final @NotNull ComponentLike message) {
  }

  @Override
  public void sendPlayerListHeader(final @NotNull ComponentLike header) {
  }

  @Override
  public void sendPlayerListFooter(final @NotNull ComponentLike footer) {
  }

  @Override
  public void sendPlayerListHeaderAndFooter(final @NotNull ComponentLike header, final @NotNull ComponentLike footer) {
  }

  @Override
  public void openBook(final @NotNull Book.Builder book) {
  }

  @Override
  public boolean equals(final Object that) {
    return this == that;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public String toString() {
    return "EmptyAudience";
  }
}

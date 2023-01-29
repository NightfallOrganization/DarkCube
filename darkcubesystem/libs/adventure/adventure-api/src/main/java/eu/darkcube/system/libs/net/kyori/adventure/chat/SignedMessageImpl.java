/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.chat;

import java.security.SecureRandom;
import java.time.Instant;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.identity.Identity;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

// Used for system messages ONLY
final class SignedMessageImpl implements SignedMessage {
  static final SecureRandom RANDOM = new SecureRandom();

  private final Instant instant;
  private final long salt;
  private final String message;
  private final Component unsignedContent;

  SignedMessageImpl(final String message, final Component unsignedContent) {
    this.instant = Instant.now();
    this.salt = RANDOM.nextLong();
    this.message = message;
    this.unsignedContent = unsignedContent;
  }

  @Override
  public @NotNull Instant timestamp() {
    return this.instant;
  }

  @Override
  public long salt() {
    return this.salt;
  }

  @Override
  public Signature signature() {
    return null;
  }

  @Override
  public @Nullable Component unsignedContent() {
    return this.unsignedContent;
  }

  @Override
  public @NotNull String message() {
    return this.message;
  }

  @Override
  public @NotNull Identity identity() {
    return Identity.nil();
  }

  static final class SignatureImpl implements Signature {

    final byte[] signature;

    SignatureImpl(final byte[] signature) {
      this.signature = signature;
    }

    @Override
    public byte[] bytes() {
      return this.signature;
    }
  }
}

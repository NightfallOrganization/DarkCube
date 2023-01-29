/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.identity;

import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

import eu.darkcube.system.libs.net.kyori.adventure.Adventure;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.pointer.Pointer;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * An identity used to track the sender of messages for the social interaction features
 * introduced in <em>Minecraft: Java Edition</em> 1.16.4.
 *
 * @since 4.0.0
 * @sinceMinecraft 1.16
 */
public interface Identity extends Examinable {
  /**
   * A pointer to a name.
   *
   * @since 4.8.0
   */
  Pointer<String> NAME = Pointer.pointer(String.class, Key.key(Adventure.NAMESPACE, "name"));
  /**
   * A pointer to a {@link UUID}.
   *
   * @since 4.8.0
   */
  Pointer<UUID> UUID = Pointer.pointer(UUID.class, Key.key(Adventure.NAMESPACE, "uuid"));
  /**
   * A pointer to a display name.
   *
   * @since 4.8.0
   */
  Pointer<Component> DISPLAY_NAME = Pointer.pointer(Component.class, Key.key(Adventure.NAMESPACE, "display_name"));
  /**
   * A pointer to a {@link Locale}.
   *
   * @since 4.9.0
   */
  Pointer<Locale> LOCALE = Pointer.pointer(Locale.class, Key.key(Adventure.NAMESPACE, "locale"));

  /**
   * Gets the {@code null} identity.
   *
   * <p>This should only be used when no players can be linked to a message.</p>
   *
   * @return the {@code null} identity
   * @since 4.0.0
   */
  static @NotNull Identity nil() {
    return NilIdentity.INSTANCE;
  }

  /**
   * Creates an identity.
   *
   * @param uuid the uuid
   * @return an identity
   * @since 4.0.0
   */
  static @NotNull Identity identity(final @NotNull UUID uuid) {
    if (uuid.equals(NilIdentity.NIL_UUID)) return NilIdentity.INSTANCE;
    return new IdentityImpl(uuid);
  }

  /**
   * Gets the uuid.
   *
   * @return the uuid
   * @since 4.0.0
   */
  @NotNull UUID uuid();

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("uuid", this.uuid()));
  }
}

/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.plain;

import java.util.function.Consumer;
import eu.darkcube.system.libs.net.kyori.adventure.builder.AbstractBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.TextComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.flattener.ComponentFlattener;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.ComponentSerializer;
import eu.darkcube.system.libs.net.kyori.adventure.util.Buildable;
import eu.darkcube.system.libs.net.kyori.adventure.util.PlatformAPI;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A plain-text component serializer.
 *
 * <p>Plain does <b>not</b> support more complex features such as, but not limited
 * to, colours, decorations, {@link ClickEvent}, and {@link HoverEvent}.</p>
 *
 * @since 4.8.0
 */
public interface PlainTextComponentSerializer extends ComponentSerializer<Component, TextComponent, String>, Buildable<PlainTextComponentSerializer, PlainTextComponentSerializer.Builder> {
  /**
   * A component serializer for plain-based serialization and deserialization.
   *
   * @return serializer instance
   * @since 4.8.0
   */
  static @NotNull PlainTextComponentSerializer plainText() {
    return PlainTextComponentSerializerImpl.Instances.INSTANCE;
  }

  /**
   * Create a new builder.
   *
   * @return a new plain serializer builder
   * @since 4.8.0
   */
  static PlainTextComponentSerializer.@NotNull Builder builder() {
    return new PlainTextComponentSerializerImpl.BuilderImpl();
  }

  @Override
  default @NotNull TextComponent deserialize(final @NotNull String input) {
    return Component.text(input);
  }

  @Override
  default @NotNull String serialize(final @NotNull Component component) {
    final StringBuilder sb = new StringBuilder();
    this.serialize(sb, component);
    return sb.toString();
  }

  /**
   * Serializes.
   *
   * @param sb the string builder
   * @param component the component
   * @since 4.8.0
   */
  void serialize(final @NotNull StringBuilder sb, final @NotNull Component component);

  /**
   * A builder for the plain-text component serializer.
   *
   * @since 4.8.0
   */
  interface Builder extends AbstractBuilder<PlainTextComponentSerializer>, Buildable.Builder<PlainTextComponentSerializer> {
    /**
     * Set the component flattener to use.
     *
     * <p>The default flattener is {@link ComponentFlattener#basic()} modified to throw exceptions on unknown component types.</p>
     *
     * @param flattener the new flattener
     * @return this builder
     * @since 4.8.0
     */
    @NotNull Builder flattener(final @NotNull ComponentFlattener flattener);
  }

  /**
   * A {@link PlainTextComponentSerializer} service provider.
   *
   * @since 4.8.0
   */
  @ApiStatus.Internal
  @PlatformAPI
  interface Provider {
    /**
     * Provides a {@link PlainTextComponentSerializer}.
     *
     * @return a {@link PlainTextComponentSerializer}
     * @since 4.8.0
     */
    @ApiStatus.Internal
    @PlatformAPI
    @NotNull PlainTextComponentSerializer plainTextSimple();

    /**
     * Completes the building process of {@link Builder}.
     *
     * @return a {@link Consumer}
     * @since 4.8.0
     */
    @ApiStatus.Internal
    @PlatformAPI
    @NotNull Consumer<Builder> plainText();
  }
}

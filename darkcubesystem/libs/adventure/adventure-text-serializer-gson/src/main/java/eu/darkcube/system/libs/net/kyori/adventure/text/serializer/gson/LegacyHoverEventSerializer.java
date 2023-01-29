/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.util.Codec;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * Adapter to convert between modern and legacy hover event formats.
 *
 * @since 4.0.0
 */
public interface LegacyHoverEventSerializer {
  /**
   * Convert a legacy hover event {@code show_item} value to its modern format.
   *
   * @param input component whose plain-text value is a SNBT string
   * @return the deserialized event
   * @throws IOException if the input is improperly formatted
   * @since 4.0.0
   */
  HoverEvent.@NotNull ShowItem deserializeShowItem(final @NotNull Component input) throws IOException;

  /**
   * Convert a legacy hover event {@code show_entity} value to its modern format.
   *
   * @param input component whose plain-text value is a SNBT string
   * @param componentDecoder A decoder that can take a JSON string and return a deserialized component
   * @return the deserialized event
   * @throws IOException if the input is improperly formatted
   * @since 4.0.0
   */
  HoverEvent.@NotNull ShowEntity deserializeShowEntity(final @NotNull Component input, final Codec.Decoder<Component, String, ? extends RuntimeException> componentDecoder) throws IOException;

  /**
   * Convert a modern hover event {@code show_item} value to its legacy format.
   *
   * @param input modern hover event
   * @return component with the legacy value as a SNBT string
   * @throws IOException if the input is improperly formatted
   * @since 4.0.0
   */
  @NotNull Component serializeShowItem(final HoverEvent.@NotNull ShowItem input) throws IOException;

  /**
   * Convert a modern hover event {@code show_entity} value to its legacy format.
   *
   * @param input modern hover event
   * @param componentEncoder An encoder that can take a {@link Component} and return a JSON string
   * @return component with the legacy value as a SNBT string
   * @throws IOException if the input is improperly formatted
   * @since 4.0.0
   */
  @NotNull Component serializeShowEntity(final HoverEvent.@NotNull ShowEntity input, final Codec.Encoder<Component, String, ? extends RuntimeException> componentEncoder) throws IOException;
}

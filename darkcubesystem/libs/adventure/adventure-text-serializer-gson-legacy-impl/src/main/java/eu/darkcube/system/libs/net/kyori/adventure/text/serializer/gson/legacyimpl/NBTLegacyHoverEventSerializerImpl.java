/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.legacyimpl;

import java.io.IOException;
import java.util.UUID;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.TagStringIO;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.api.BinaryTagHolder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.TextComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.LegacyHoverEventSerializer;
import eu.darkcube.system.libs.net.kyori.adventure.util.Codec;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class NBTLegacyHoverEventSerializerImpl implements LegacyHoverEventSerializer {
  static final NBTLegacyHoverEventSerializerImpl INSTANCE = new NBTLegacyHoverEventSerializerImpl();
  private static final TagStringIO SNBT_IO = TagStringIO.get();
  private static final Codec<CompoundBinaryTag, String, IOException, IOException> SNBT_CODEC = Codec.codec(SNBT_IO::asCompound, SNBT_IO::asString);

  static final String ITEM_TYPE = "id";
  static final String ITEM_COUNT = "Count";
  static final String ITEM_TAG = "tag";

  static final String ENTITY_NAME = "name";
  static final String ENTITY_TYPE = "type";
  static final String ENTITY_ID = "id";

  private NBTLegacyHoverEventSerializerImpl() {
  }

  @Override
  public HoverEvent.@NotNull ShowItem deserializeShowItem(final @NotNull Component input) throws IOException {
    assertTextComponent(input);
    final CompoundBinaryTag contents = SNBT_CODEC.decode(((TextComponent) input).content());
    final CompoundBinaryTag tag = contents.getCompound(ITEM_TAG);
    return HoverEvent.ShowItem.of(
      Key.key(contents.getString(ITEM_TYPE)),
      contents.getByte(ITEM_COUNT, (byte) 1),
      tag == CompoundBinaryTag.empty() ? null : BinaryTagHolder.encode(tag, SNBT_CODEC)
    );
  }

  @Override
  public HoverEvent.@NotNull ShowEntity deserializeShowEntity(final @NotNull Component input, final Codec.Decoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
    assertTextComponent(input);
    final CompoundBinaryTag contents = SNBT_CODEC.decode(((TextComponent) input).content());
    return HoverEvent.ShowEntity.of(
      Key.key(contents.getString(ENTITY_TYPE)),
      UUID.fromString(contents.getString(ENTITY_ID)),
      componentCodec.decode(contents.getString(ENTITY_NAME))
    );
  }

  private static void assertTextComponent(final Component component) {
    if (!(component instanceof TextComponent) || !component.children().isEmpty()) {
      throw new IllegalArgumentException("Legacy events must be single Component instances");
    }
  }

  @Override
  public @NotNull Component serializeShowItem(final HoverEvent.@NotNull ShowItem input) throws IOException {
    final CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder()
      .putString(ITEM_TYPE, input.item().asString())
      .putByte(ITEM_COUNT, (byte) input.count());
    final @Nullable BinaryTagHolder nbt = input.nbt();
    if (nbt != null) {
      builder.put(ITEM_TAG, nbt.get(SNBT_CODEC));
    }
    return Component.text(SNBT_CODEC.encode(builder.build()));
  }

  @Override
  public @NotNull Component serializeShowEntity(final HoverEvent.@NotNull ShowEntity input, final Codec.Encoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
    final CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder()
      .putString(ENTITY_ID, input.id().toString())
      .putString(ENTITY_TYPE, input.type().asString());
    final @Nullable Component name = input.name();
    if (name != null) {
      builder.putString(ENTITY_NAME, componentCodec.encode(name));
    }
    return Component.text(SNBT_CODEC.encode(builder.build()));
  }
}

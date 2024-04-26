/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.BlockNBTComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class SerializerFactory implements TypeAdapterFactory {
  static final Class<Key> KEY_TYPE = Key.class;
  static final Class<Component> COMPONENT_TYPE = Component.class;
  static final Class<Style> STYLE_TYPE = Style.class;
  static final Class<ClickEvent.Action> CLICK_ACTION_TYPE = ClickEvent.Action.class;
  static final Class<HoverEvent.Action> HOVER_ACTION_TYPE = HoverEvent.Action.class;
  static final Class<HoverEvent.ShowItem> SHOW_ITEM_TYPE = HoverEvent.ShowItem.class;
  static final Class<HoverEvent.ShowEntity> SHOW_ENTITY_TYPE = HoverEvent.ShowEntity.class;
  static final Class<TextColorWrapper> COLOR_WRAPPER_TYPE = TextColorWrapper.class;
  static final Class<TextColor> COLOR_TYPE = TextColor.class;
  static final Class<TextDecoration> TEXT_DECORATION_TYPE = TextDecoration.class;
  static final Class<BlockNBTComponent.Pos> BLOCK_NBT_POS_TYPE = BlockNBTComponent.Pos.class;

  private final boolean downsampleColors;
  private final LegacyHoverEventSerializer legacyHoverSerializer;
  private final boolean emitLegacyHover;

  SerializerFactory(final boolean downsampleColors, final @Nullable LegacyHoverEventSerializer legacyHoverSerializer, final boolean emitLegacyHover) {
    this.downsampleColors = downsampleColors;
    this.legacyHoverSerializer = legacyHoverSerializer;
    this.emitLegacyHover = emitLegacyHover;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
    final Class<? super T> rawType = type.getRawType();
    if (COMPONENT_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) ComponentSerializerImpl.create(gson);
    } else if (KEY_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) KeySerializer.INSTANCE;
    } else if (STYLE_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) StyleSerializer.create(this.legacyHoverSerializer, this.emitLegacyHover, gson);
    } else if (CLICK_ACTION_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) ClickEventActionSerializer.INSTANCE;
    } else if (HOVER_ACTION_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) HoverEventActionSerializer.INSTANCE;
    } else if (SHOW_ITEM_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) ShowItemSerializer.create(gson);
    } else if (SHOW_ENTITY_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) ShowEntitySerializer.create(gson);
    } else if (COLOR_WRAPPER_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) TextColorWrapper.Serializer.INSTANCE;
    } else if (COLOR_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) (this.downsampleColors ? TextColorSerializer.DOWNSAMPLE_COLOR : TextColorSerializer.INSTANCE);
    } else if (TEXT_DECORATION_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) TextDecorationSerializer.INSTANCE;
    } else if (BLOCK_NBT_POS_TYPE.isAssignableFrom(rawType)) {
      return (TypeAdapter<T>) BlockNBTComponentPosSerializer.INSTANCE;
    } else {
      return null;
    }
  }
}

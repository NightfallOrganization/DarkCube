/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.api.BinaryTagHolder;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent.ShowItem;

final class ShowItemSerializer extends TypeAdapter<ShowItem> {
  static final String ID = "id";
  static final String COUNT = "count";
  static final String TAG = "tag";

  static TypeAdapter<HoverEvent.ShowItem> create(final Gson gson) {
    return new ShowItemSerializer(gson).nullSafe();
  }

  private final Gson gson;

  private ShowItemSerializer(final Gson gson) {
    this.gson = gson;
  }

  @Override
  public HoverEvent.ShowItem read(final JsonReader in) throws IOException {
    in.beginObject();

    Key key = null;
    int count = 1;
    @Nullable BinaryTagHolder nbt = null;

    while (in.hasNext()) {
      final String fieldName = in.nextName();
      if (fieldName.equals(ID)) {
        key = this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
      } else if (fieldName.equals(COUNT)) {
        count = in.nextInt();
      } else if (fieldName.equals(TAG)) {
        final JsonToken token = in.peek();
        if (token == JsonToken.STRING || token == JsonToken.NUMBER) {
          nbt = BinaryTagHolder.binaryTagHolder(in.nextString());
        } else if (token == JsonToken.BOOLEAN) {
          nbt = BinaryTagHolder.binaryTagHolder(String.valueOf(in.nextBoolean()));
        } else if (token == JsonToken.NULL) {
          in.nextNull();
        } else {
          throw new JsonParseException("Expected " + TAG + " to be a string");
        }
      } else {
        in.skipValue();
      }
    }

    if (key == null) {
      throw new JsonParseException("Not sure how to deserialize show_item hover event");
    }
    in.endObject();

    return HoverEvent.ShowItem.of(key, count, nbt);
  }

  @Override
  public void write(final JsonWriter out, final HoverEvent.ShowItem value) throws IOException {
    out.beginObject();

    out.name(ID);
    this.gson.toJson(value.item(), SerializerFactory.KEY_TYPE, out);

    final int count = value.count();
    if (count != 1) {
      out.name(COUNT);
      out.value(count);
    }

    final @Nullable BinaryTagHolder nbt = value.nbt();
    if (nbt != null) {
      out.name(TAG);
      out.value(nbt.string());
    }

    out.endObject();
  }
}

/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson;

import java.io.IOException;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent.ShowEntity;

final class ShowEntitySerializer extends TypeAdapter<ShowEntity> {
  static final String TYPE = "type";
  static final String ID = "id";
  static final String NAME = "name";

  static TypeAdapter<HoverEvent.ShowEntity> create(final Gson gson) {
    return new ShowEntitySerializer(gson).nullSafe();
  }

  private final Gson gson;

  private ShowEntitySerializer(final Gson gson) {
    this.gson = gson;
  }

  @Override
  public HoverEvent.ShowEntity read(final JsonReader in) throws IOException {
    in.beginObject();

    Key type = null;
    UUID id = null;
    @Nullable Component name = null;

    while (in.hasNext()) {
      final String fieldName = in.nextName();
      if (fieldName.equals(TYPE)) {
        type = this.gson.fromJson(in, SerializerFactory.KEY_TYPE);
      } else if (fieldName.equals(ID)) {
        id = UUID.fromString(in.nextString());
      } else if (fieldName.equals(NAME)) {
        name = this.gson.fromJson(in, SerializerFactory.COMPONENT_TYPE);
      } else {
        in.skipValue();
      }
    }

    if (type == null || id == null) {
      throw new JsonParseException("A show entity hover event needs type and id fields to be deserialized");
    }
    in.endObject();

    return HoverEvent.ShowEntity.of(type, id, name);
  }

  @Override
  public void write(final JsonWriter out, final HoverEvent.ShowEntity value) throws IOException {
    out.beginObject();

    out.name(TYPE);
    this.gson.toJson(value.type(), SerializerFactory.KEY_TYPE, out);

    out.name(ID);
    out.value(value.id().toString());

    final @Nullable Component name = value.name();
    if (name != null) {
      out.name(NAME);
      this.gson.toJson(name, SerializerFactory.COMPONENT_TYPE, out);
    }

    out.endObject();
  }
}

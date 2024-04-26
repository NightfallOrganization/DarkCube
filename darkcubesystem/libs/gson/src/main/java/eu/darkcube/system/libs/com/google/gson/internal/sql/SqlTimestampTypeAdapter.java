/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson.internal.sql;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.com.google.gson.TypeAdapterFactory;
import eu.darkcube.system.libs.com.google.gson.reflect.TypeToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

class SqlTimestampTypeAdapter extends TypeAdapter<Timestamp> {
  static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
    @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
    @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      if (typeToken.getRawType() == Timestamp.class) {
        final TypeAdapter<Date> dateTypeAdapter = gson.getAdapter(Date.class);
        return (TypeAdapter<T>) new SqlTimestampTypeAdapter(dateTypeAdapter);
      } else {
        return null;
      }
    }
  };

  private final TypeAdapter<Date> dateTypeAdapter;

  private SqlTimestampTypeAdapter(TypeAdapter<Date> dateTypeAdapter) {
    this.dateTypeAdapter = dateTypeAdapter;
  }

  @Override
  public Timestamp read(JsonReader in) throws IOException {
    Date date = dateTypeAdapter.read(in);
    return date != null ? new Timestamp(date.getTime()) : null;
  }

  @Override
  public void write(JsonWriter out, Timestamp value) throws IOException {
    dateTypeAdapter.write(out, value);
  }
}

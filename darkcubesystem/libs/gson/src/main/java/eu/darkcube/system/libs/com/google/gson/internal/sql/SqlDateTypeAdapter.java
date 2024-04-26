/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson.internal.sql;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonSyntaxException;
import eu.darkcube.system.libs.com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.com.google.gson.TypeAdapterFactory;
import eu.darkcube.system.libs.com.google.gson.reflect.TypeToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.JsonToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Adapter for java.sql.Date. Although this class appears stateless, it is not.
 * DateFormat captures its time zone and locale when it is created, which gives
 * this class state. DateFormat isn't thread safe either, so this class has
 * to synchronize its read and write methods.
 */
final class SqlDateTypeAdapter extends TypeAdapter<java.sql.Date> {
  static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
    @SuppressWarnings("unchecked") // we use a runtime check to make sure the 'T's equal
    @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      return typeToken.getRawType() == java.sql.Date.class
          ? (TypeAdapter<T>) new SqlDateTypeAdapter() : null;
    }
  };

  private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");

  private SqlDateTypeAdapter() {
  }

  @Override
  public java.sql.Date read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    String s = in.nextString();
    try {
      Date utilDate;
      synchronized (this) {
        utilDate = format.parse(s);
      }
      return new java.sql.Date(utilDate.getTime());
    } catch (ParseException e) {
      throw new JsonSyntaxException("Failed parsing '" + s + "' as SQL Date; at path " + in.getPreviousPath(), e);
    }
  }

  @Override
  public void write(JsonWriter out, java.sql.Date value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }
    String dateString;
    synchronized (this) {
      dateString = format.format(value);
    }
    out.value(dateString);
  }
}

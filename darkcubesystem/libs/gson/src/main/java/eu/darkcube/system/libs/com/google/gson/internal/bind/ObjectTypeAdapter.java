/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson.internal.bind;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.ToNumberPolicy;
import eu.darkcube.system.libs.com.google.gson.ToNumberStrategy;
import eu.darkcube.system.libs.com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.com.google.gson.TypeAdapterFactory;
import eu.darkcube.system.libs.com.google.gson.internal.LinkedTreeMap;
import eu.darkcube.system.libs.com.google.gson.reflect.TypeToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.JsonToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Adapts types whose static type is only 'Object'. Uses getClass() on
 * serialization and a primitive/Map/List on deserialization.
 */
public final class ObjectTypeAdapter extends TypeAdapter<Object> {
  /**
   * Gson default factory using {@link ToNumberPolicy#DOUBLE}.
   */
  private static final TypeAdapterFactory DOUBLE_FACTORY = newFactory(ToNumberPolicy.DOUBLE);

  private final Gson gson;
  private final ToNumberStrategy toNumberStrategy;

  private ObjectTypeAdapter(Gson gson, ToNumberStrategy toNumberStrategy) {
    this.gson = gson;
    this.toNumberStrategy = toNumberStrategy;
  }

  private static TypeAdapterFactory newFactory(final ToNumberStrategy toNumberStrategy) {
    return new TypeAdapterFactory() {
      @SuppressWarnings("unchecked")
      @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (type.getRawType() == Object.class) {
          return (TypeAdapter<T>) new ObjectTypeAdapter(gson, toNumberStrategy);
        }
        return null;
      }
    };
  }

  public static TypeAdapterFactory getFactory(ToNumberStrategy toNumberStrategy) {
    if (toNumberStrategy == ToNumberPolicy.DOUBLE) {
      return DOUBLE_FACTORY;
    } else {
      return newFactory(toNumberStrategy);
    }
  }

  /**
   * Tries to begin reading a JSON array or JSON object, returning {@code null} if
   * the next element is neither of those.
   */
  private Object tryBeginNesting(JsonReader in, JsonToken peeked) throws IOException {
    switch (peeked) {
      case BEGIN_ARRAY:
        in.beginArray();
        return new ArrayList<>();
      case BEGIN_OBJECT:
        in.beginObject();
        return new LinkedTreeMap<>();
      default:
        return null;
    }
  }

  /** Reads an {@code Object} which cannot have any nested elements */
  private Object readTerminal(JsonReader in, JsonToken peeked) throws IOException {
    switch (peeked) {
      case STRING:
        return in.nextString();
      case NUMBER:
        return toNumberStrategy.readNumber(in);
      case BOOLEAN:
        return in.nextBoolean();
      case NULL:
        in.nextNull();
        return null;
      default:
        // When read(JsonReader) is called with JsonReader in invalid state
        throw new IllegalStateException("Unexpected token: " + peeked);
    }
  }

  @Override public Object read(JsonReader in) throws IOException {
    // Either List or Map
    Object current;
    JsonToken peeked = in.peek();

    current = tryBeginNesting(in, peeked);
    if (current == null) {
      return readTerminal(in, peeked);
    }

    Deque<Object> stack = new ArrayDeque<>();

    while (true) {
      while (in.hasNext()) {
        String name = null;
        // Name is only used for JSON object members
        if (current instanceof Map) {
          name = in.nextName();
        }

        peeked = in.peek();
        Object value = tryBeginNesting(in, peeked);
        boolean isNesting = value != null;

        if (value == null) {
          value = readTerminal(in, peeked);
        }

        if (current instanceof List) {
          @SuppressWarnings("unchecked")
          List<Object> list = (List<Object>) current;
          list.add(value);
        } else {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = (Map<String, Object>) current;
          map.put(name, value);
        }

        if (isNesting) {
          stack.addLast(current);
          current = value;
        }
      }

      // End current element
      if (current instanceof List) {
        in.endArray();
      } else {
        in.endObject();
      }

      if (stack.isEmpty()) {
        return current;
      } else {
        // Continue with enclosing element
        current = stack.removeLast();
      }
    }
  }

  @Override public void write(JsonWriter out, Object value) throws IOException {
    if (value == null) {
      out.nullValue();
      return;
    }

    @SuppressWarnings("unchecked")
    TypeAdapter<Object> typeAdapter = (TypeAdapter<Object>) gson.getAdapter(value.getClass());
    if (typeAdapter instanceof ObjectTypeAdapter) {
      out.beginObject();
      out.endObject();
      return;
    }

    typeAdapter.write(out, value);
  }
}

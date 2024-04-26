/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson.internal.bind;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.TypeAdapter;
import eu.darkcube.system.libs.com.google.gson.TypeAdapterFactory;
import eu.darkcube.system.libs.com.google.gson.internal.$Gson$Types;
import eu.darkcube.system.libs.com.google.gson.internal.ConstructorConstructor;
import eu.darkcube.system.libs.com.google.gson.internal.ObjectConstructor;
import eu.darkcube.system.libs.com.google.gson.reflect.TypeToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.JsonToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Adapt a homogeneous collection of objects.
 */
public final class CollectionTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;

  public CollectionTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
    this.constructorConstructor = constructorConstructor;
  }

  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
    Type type = typeToken.getType();

    Class<? super T> rawType = typeToken.getRawType();
    if (!Collection.class.isAssignableFrom(rawType)) {
      return null;
    }

    Type elementType = $Gson$Types.getCollectionElementType(type, rawType);
    TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
    ObjectConstructor<T> constructor = constructorConstructor.get(typeToken);

    @SuppressWarnings({"unchecked", "rawtypes"}) // create() doesn't define a type parameter
    TypeAdapter<T> result = new Adapter(gson, elementType, elementTypeAdapter, constructor);
    return result;
  }

  private static final class Adapter<E> extends TypeAdapter<Collection<E>> {
    private final TypeAdapter<E> elementTypeAdapter;
    private final ObjectConstructor<? extends Collection<E>> constructor;

    public Adapter(Gson context, Type elementType,
        TypeAdapter<E> elementTypeAdapter,
        ObjectConstructor<? extends Collection<E>> constructor) {
      this.elementTypeAdapter =
          new TypeAdapterRuntimeTypeWrapper<>(context, elementTypeAdapter, elementType);
      this.constructor = constructor;
    }

    @Override public Collection<E> read(JsonReader in) throws IOException {
      if (in.peek() == JsonToken.NULL) {
        in.nextNull();
        return null;
      }

      Collection<E> collection = constructor.construct();
      in.beginArray();
      while (in.hasNext()) {
        E instance = elementTypeAdapter.read(in);
        collection.add(instance);
      }
      in.endArray();
      return collection;
    }

    @Override public void write(JsonWriter out, Collection<E> collection) throws IOException {
      if (collection == null) {
        out.nullValue();
        return;
      }

      out.beginArray();
      for (E element : collection) {
        elementTypeAdapter.write(out, element);
      }
      out.endArray();
    }
  }
}

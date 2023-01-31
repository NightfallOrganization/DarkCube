/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson;

import java.lang.reflect.Type;

/**
 * Interface representing a custom serializer for JSON. You should write a custom serializer, if
 * you are not happy with the default serialization done by Gson. You will also need to register
 * this serializer through {@link GsonBuilder#registerTypeAdapter(Type, Object)}.
 *
 * <p>Let us look at example where defining a serializer will be useful. The {@code Id} class
 * defined below has two fields: {@code clazz} and {@code value}.</p>
 *
 * <pre>
 * public class Id&lt;T&gt; {
 *   private final Class&lt;T&gt; clazz;
 *   private final long value;
 *
 *   public Id(Class&lt;T&gt; clazz, long value) {
 *     this.clazz = clazz;
 *     this.value = value;
 *   }
 *
 *   public long getValue() {
 *     return value;
 *   }
 * }
 * </pre>
 *
 * <p>The default serialization of {@code Id(com.foo.MyObject.class, 20L)} will be
 * <code>{"clazz":"com.foo.MyObject","value":20}</code>. Suppose, you just want the output to be
 * the value instead, which is {@code 20} in this case. You can achieve that by writing a custom
 * serializer:</p>
 *
 * <pre>
 * class IdSerializer implements JsonSerializer&lt;Id&gt; {
 *   public JsonElement serialize(Id id, Type typeOfId, JsonSerializationContext context) {
 *     return new JsonPrimitive(id.getValue());
 *   }
 * }
 * </pre>
 *
 * <p>You will also need to register {@code IdSerializer} with Gson as follows:</p>
 * <pre>
 * Gson gson = new GsonBuilder().registerTypeAdapter(Id.class, new IdSerializer()).create();
 * </pre>
 *
 * <p>Serializers should be stateless and thread-safe, otherwise the thread-safety
 * guarantees of {@link Gson} might not apply.
 *
 * <p>New applications should prefer {@link TypeAdapter}, whose streaming API
 * is more efficient than this interface's tree API.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 *
 * @param <T> type for which the serializer is being registered. It is possible that a serializer
 *        may be asked to serialize a specific generic type of the T.
 */
public interface JsonSerializer<T> {

  /**
   * Gson invokes this call-back method during serialization when it encounters a field of the
   * specified type.
   *
   * <p>In the implementation of this call-back method, you should consider invoking
   * {@link JsonSerializationContext#serialize(Object, Type)} method to create JsonElements for any
   * non-trivial field of the {@code src} object. However, you should never invoke it on the
   * {@code src} object itself since that will cause an infinite loop (Gson will call your
   * call-back method again).</p>
   *
   * @param src the object that needs to be converted to Json.
   * @param typeOfSrc the actual type (fully genericized version) of the source object.
   * @return a JsonElement corresponding to the specified object.
   */
  public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context);
}
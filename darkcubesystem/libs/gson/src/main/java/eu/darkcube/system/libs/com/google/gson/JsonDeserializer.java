/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson;

import java.lang.reflect.Type;

/**
 * <p>Interface representing a custom deserializer for JSON. You should write a custom
 * deserializer, if you are not happy with the default deserialization done by Gson. You will
 * also need to register this deserializer through
 * {@link GsonBuilder#registerTypeAdapter(Type, Object)}.</p>
 *
 * <p>Let us look at example where defining a deserializer will be useful. The {@code Id} class
 * defined below has two fields: {@code clazz} and {@code value}.</p>
 *
 * <pre>
 * public class Id&lt;T&gt; {
 *   private final Class&lt;T&gt; clazz;
 *   private final long value;
 *   public Id(Class&lt;T&gt; clazz, long value) {
 *     this.clazz = clazz;
 *     this.value = value;
 *   }
 *   public long getValue() {
 *     return value;
 *   }
 * }
 * </pre>
 *
 * <p>The default deserialization of {@code Id(com.foo.MyObject.class, 20L)} will require the
 * JSON string to be <code>{"clazz":"com.foo.MyObject","value":20}</code>. Suppose, you already know
 * the type of the field that the {@code Id} will be deserialized into, and hence just want to
 * deserialize it from a JSON string {@code 20}. You can achieve that by writing a custom
 * deserializer:</p>
 *
 * <pre>
 * class IdDeserializer implements JsonDeserializer&lt;Id&gt; {
 *   public Id deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
 *       throws JsonParseException {
 *     long idValue = json.getAsJsonPrimitive().getAsLong();
 *     return new Id((Class) typeOfT, idValue);
 *   }
 * }
 * </pre>
 *
 * <p>You will also need to register {@code IdDeserializer} with Gson as follows:</p>
 *
 * <pre>
 * Gson gson = new GsonBuilder().registerTypeAdapter(Id.class, new IdDeserializer()).create();
 * </pre>
 *
 * <p>Deserializers should be stateless and thread-safe, otherwise the thread-safety
 * guarantees of {@link Gson} might not apply.
 *
 * <p>New applications should prefer {@link TypeAdapter}, whose streaming API
 * is more efficient than this interface's tree API.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 *
 * @param <T> type for which the deserializer is being registered. It is possible that a
 * deserializer may be asked to deserialize a specific generic type of the T.
 */
public interface JsonDeserializer<T> {

  /**
   * Gson invokes this call-back method during deserialization when it encounters a field of the
   * specified type.
   * <p>In the implementation of this call-back method, you should consider invoking
   * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method to create objects
   * for any non-trivial field of the returned object. However, you should never invoke it on the
   * same type passing {@code json} since that will cause an infinite loop (Gson will call your
   * call-back method again).
   *
   * @param json The Json data being deserialized
   * @param typeOfT The type of the Object to deserialize to
   * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
   * @throws JsonParseException if json is not in the expected format of {@code typeofT}
   */
  public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException;
}

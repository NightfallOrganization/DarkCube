/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.com.google.gson;

import eu.darkcube.system.libs.com.google.gson.internal.Streams;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.JsonToken;
import eu.darkcube.system.libs.com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * A parser to parse JSON into a parse tree of {@link JsonElement}s.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 * @since 1.3
 */
public final class JsonParser {
  /** @deprecated No need to instantiate this class, use the static methods instead. */
  @Deprecated
  public JsonParser() {}

  /**
   * Parses the specified JSON string into a parse tree.
   * An exception is thrown if the JSON string has multiple top-level JSON elements,
   * or if there is trailing data.
   *
   * <p>The JSON string is parsed in {@linkplain JsonReader#setLenient(boolean) lenient mode}.
   *
   * @param json JSON text
   * @return a parse tree of {@link JsonElement}s corresponding to the specified JSON
   * @throws JsonParseException if the specified text is not valid JSON
   * @since 2.8.6
   */
  public static JsonElement parseString(String json) throws JsonSyntaxException {
    return parseReader(new StringReader(json));
  }

  /**
   * Parses the complete JSON string provided by the reader into a parse tree.
   * An exception is thrown if the JSON string has multiple top-level JSON elements,
   * or if there is trailing data.
   *
   * <p>The JSON data is parsed in {@linkplain JsonReader#setLenient(boolean) lenient mode}.
   *
   * @param reader JSON text
   * @return a parse tree of {@link JsonElement}s corresponding to the specified JSON
   * @throws JsonParseException if there is an IOException or if the specified
   *     text is not valid JSON
   * @since 2.8.6
   */
  public static JsonElement parseReader(Reader reader) throws JsonIOException, JsonSyntaxException {
    try {
      JsonReader jsonReader = new JsonReader(reader);
      JsonElement element = parseReader(jsonReader);
      if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
        throw new JsonSyntaxException("Did not consume the entire document.");
      }
      return element;
    } catch (MalformedJsonException e) {
      throw new JsonSyntaxException(e);
    } catch (IOException e) {
      throw new JsonIOException(e);
    } catch (NumberFormatException e) {
      throw new JsonSyntaxException(e);
    }
  }

  /**
   * Returns the next value from the JSON stream as a parse tree.
   * Unlike the other {@code parse} methods, no exception is thrown if the JSON data has
   * multiple top-level JSON elements, or if there is trailing data.
   *
   * <p>The JSON data is parsed in {@linkplain JsonReader#setLenient(boolean) lenient mode},
   * regardless of the lenient mode setting of the provided reader. The lenient mode setting
   * of the reader is restored once this method returns.
   *
   * @throws JsonParseException if there is an IOException or if the specified
   *     text is not valid JSON
   * @since 2.8.6
   */
  public static JsonElement parseReader(JsonReader reader)
      throws JsonIOException, JsonSyntaxException {
    boolean lenient = reader.isLenient();
    reader.setLenient(true);
    try {
      return Streams.parse(reader);
    } catch (StackOverflowError e) {
      throw new JsonParseException("Failed parsing JSON source: " + reader + " to Json", e);
    } catch (OutOfMemoryError e) {
      throw new JsonParseException("Failed parsing JSON source: " + reader + " to Json", e);
    } finally {
      reader.setLenient(lenient);
    }
  }

  /** @deprecated Use {@link JsonParser#parseString} */
  @Deprecated
  public JsonElement parse(String json) throws JsonSyntaxException {
    return parseString(json);
  }

  /** @deprecated Use {@link JsonParser#parseReader(Reader)} */
  @Deprecated
  public JsonElement parse(Reader json) throws JsonIOException, JsonSyntaxException {
    return parseReader(json);
  }

  /** @deprecated Use {@link JsonParser#parseReader(JsonReader)} */
  @Deprecated
  public JsonElement parse(JsonReader json) throws JsonIOException, JsonSyntaxException {
    return parseReader(json);
  }
}

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson.internal;

import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import java.io.IOException;

/**
 * Internal-only APIs of JsonReader available only to other classes in Gson.
 */
public abstract class JsonReaderInternalAccess {
  public static JsonReaderInternalAccess INSTANCE;

  /**
   * Changes the type of the current property name token to a string value.
   */
  public abstract void promoteNameToValue(JsonReader reader) throws IOException;
}

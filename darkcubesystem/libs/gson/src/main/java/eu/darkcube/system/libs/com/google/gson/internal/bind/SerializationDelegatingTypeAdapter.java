/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson.internal.bind;

import eu.darkcube.system.libs.com.google.gson.TypeAdapter;

/**
 * Type adapter which might delegate serialization to another adapter.
 */
public abstract class SerializationDelegatingTypeAdapter<T> extends TypeAdapter<T> {
  /**
   * Returns the adapter used for serialization, might be {@code this} or another adapter.
   * That other adapter might itself also be a {@code SerializationDelegatingTypeAdapter}.
   */
  public abstract TypeAdapter<T> getSerializationDelegate();
}

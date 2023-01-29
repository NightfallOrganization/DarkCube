/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.pointer;

import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A pointer to a resource.
 *
 * @param <V> the value type
 * @since 4.8.0
 */
public interface Pointer<V> extends Examinable {
  /**
   * Creates a pointer.
   *
   * @param type the value type
   * @param key the key
   * @param <V> the value type
   * @return the pointer
   * @since 4.8.0
   */
  static <V> @NotNull Pointer<V> pointer(final @NotNull Class<V> type, final @NotNull Key key) {
    return new PointerImpl<>(type, key);
  }

  /**
   * Gets the value type.
   *
   * @return the value type
   * @since 4.8.0
   */
  @NotNull Class<V> type();

  /**
   * Gets the key.
   *
   * @return the key
   * @since 4.8.0
   */
  @NotNull Key key();

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("type", this.type()),
      ExaminableProperty.of("key", this.key())
    );
  }
}

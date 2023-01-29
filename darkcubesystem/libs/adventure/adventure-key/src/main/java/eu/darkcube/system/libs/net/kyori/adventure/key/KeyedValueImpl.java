/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.key;

import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.net.kyori.examination.string.StringExaminer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class KeyedValueImpl<T> implements Examinable, KeyedValue<T> {
  private final Key key;
  private final T value;

  KeyedValueImpl(final Key key, final T value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public @NotNull Key key() {
    return this.key;
  }

  @Override
  public @NotNull T value() {
    return this.value;
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || this.getClass() != other.getClass()) return false;
    final KeyedValueImpl<?> that = (KeyedValueImpl<?>) other;
    return this.key.equals(that.key) && this.value.equals(that.value);
  }

  @Override
  public int hashCode() {
    int result = this.key.hashCode();
    result = (31 * result) + this.value.hashCode();
    return result;
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("key", this.key),
      ExaminableProperty.of("value", this.value)
    );
  }

  @Override
  public String toString() {
    return this.examine(StringExaminer.simpleEscaping());
  }
}

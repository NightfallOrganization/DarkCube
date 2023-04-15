/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class CompoundTagBuilder implements CompoundBinaryTag.Builder {
  private @Nullable Map<String, BinaryTag> tags;

  private Map<String, BinaryTag> tags() {
    if (this.tags == null) {
      this.tags = new HashMap<>();
    }
    return this.tags;
  }

  @Override
  public CompoundBinaryTag.@NotNull Builder put(final @NotNull String key, final @NotNull BinaryTag tag) {
    this.tags().put(key, tag);
    return this;
  }

  @Override
  public CompoundBinaryTag.@NotNull Builder put(final @NotNull CompoundBinaryTag tag) {
    final Map<String, BinaryTag> tags = this.tags();
    for (final String key : tag.keySet()) {
      tags.put(key, tag.get(key));
    }
    return this;
  }

  @Override
  public CompoundBinaryTag.@NotNull Builder put(final @NotNull Map<String, ? extends BinaryTag> tags) {
    this.tags().putAll(tags);
    return this;
  }

  @Override
  public CompoundBinaryTag.@NotNull Builder remove(final @NotNull String key, final @Nullable Consumer<? super BinaryTag> removed) {
    if (this.tags != null) {
      final BinaryTag tag = this.tags.remove(key);
      if (removed != null) {
        removed.accept(tag);
      }
    }
    return this;
  }

  @Override
  public @NotNull CompoundBinaryTag build() {
    if (this.tags == null) return CompoundBinaryTag.empty();
    return new CompoundBinaryTagImpl(new HashMap<>(this.tags));
  }
}

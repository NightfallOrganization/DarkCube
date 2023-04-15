/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.util.ArrayList;
import java.util.List;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class ListTagBuilder<T extends BinaryTag> implements ListBinaryTag.Builder<T> {
  private @Nullable List<BinaryTag> tags;
  private BinaryTagType<? extends BinaryTag> elementType;

  ListTagBuilder() {
    this(BinaryTagTypes.END);
  }

  ListTagBuilder(final BinaryTagType<? extends BinaryTag> type) {
    this.elementType = type;
  }

  @Override
  public ListBinaryTag.@NotNull Builder<T> add(final BinaryTag tag) {
    ListBinaryTagImpl.noAddEnd(tag);
    // set the type if it has not yet been set
    if (this.elementType == BinaryTagTypes.END) {
      this.elementType = tag.type();
    }
    // check after changing from an empty tag
    ListBinaryTagImpl.mustBeSameType(tag, this.elementType);
    if (this.tags == null) {
      this.tags = new ArrayList<>();
    }
    this.tags.add(tag);
    return this;
  }

  @Override
  public ListBinaryTag.@NotNull Builder<T> add(final Iterable<? extends T> tagsToAdd) {
    for (final T tag : tagsToAdd) {
      this.add(tag);
    }
    return this;
  }

  @Override
  public @NotNull ListBinaryTag build() {
    if (this.tags == null) return ListBinaryTag.empty();
    return new ListBinaryTagImpl(this.elementType, new ArrayList<>(this.tags));
  }
}

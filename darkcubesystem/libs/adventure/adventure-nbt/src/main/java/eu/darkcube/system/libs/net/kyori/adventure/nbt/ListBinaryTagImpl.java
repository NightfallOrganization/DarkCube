/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Debug;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Range;

@Debug.Renderer(text = "\"ListBinaryTag[type=\" + this.type.toString() + \"]\"", childrenArray = "this.tags.toArray()", hasChildren = "!this.tags.isEmpty()")
final class ListBinaryTagImpl extends AbstractBinaryTag implements ListBinaryTag {
  static final ListBinaryTag EMPTY = new ListBinaryTagImpl(BinaryTagTypes.END, Collections.emptyList());
  private final List<BinaryTag> tags;
  private final BinaryTagType<? extends BinaryTag> elementType;
  private final int hashCode;

  ListBinaryTagImpl(final BinaryTagType<? extends BinaryTag> elementType, final List<BinaryTag> tags) {
    this.tags = Collections.unmodifiableList(tags);
    this.elementType = elementType;
    this.hashCode = tags.hashCode();
  }

  @Override
  public @NotNull BinaryTagType<? extends BinaryTag> elementType() {
    return this.elementType;
  }

  @Override
  public int size() {
    return this.tags.size();
  }

  @Override
  public @NotNull BinaryTag get(@Range(from = 0, to = Integer.MAX_VALUE) final int index) {
    return this.tags.get(index);
  }

  @Override
  public @NotNull ListBinaryTag set(final int index, final @NotNull BinaryTag newTag, final @Nullable Consumer<? super BinaryTag> removed) {
    return this.edit(tags -> {
      final BinaryTag oldTag = tags.set(index, newTag);
      if (removed != null) {
        removed.accept(oldTag);
      }
    }, newTag.type());
  }

  @Override
  public @NotNull ListBinaryTag remove(final int index, final @Nullable Consumer<? super BinaryTag> removed) {
    return this.edit(tags -> {
      final BinaryTag oldTag = tags.remove(index);
      if (removed != null) {
        removed.accept(oldTag);
      }
    }, null);
  }

  @Override
  public @NotNull ListBinaryTag add(final BinaryTag tag) {
    noAddEnd(tag);
    if (this.elementType != BinaryTagTypes.END) {
      mustBeSameType(tag, this.elementType);
    }
    return this.edit(tags -> tags.add(tag), tag.type());
  }

  @Override
  public @NotNull ListBinaryTag add(final Iterable<? extends BinaryTag> tagsToAdd) {
    if (tagsToAdd instanceof Collection<?> && ((Collection<?>) tagsToAdd).isEmpty()) {
      return this;
    }
    final BinaryTagType<?> type = ListBinaryTagImpl.mustBeSameType(tagsToAdd);
    return this.edit(tags -> {
      for (final BinaryTag tag : tagsToAdd) {
        tags.add(tag);
      }
    }, type);
  }

  // An end tag cannot be an element in a list tag
  static void noAddEnd(final BinaryTag tag) {
    if (tag.type() == BinaryTagTypes.END) {
      throw new IllegalArgumentException(String.format("Cannot add a %s to a %s", BinaryTagTypes.END, BinaryTagTypes.LIST));
    }
  }

  // Cannot have different element types in a list tag
  static BinaryTagType<?> mustBeSameType(final Iterable<? extends BinaryTag> tags) {
    BinaryTagType<?> type = null;
    for (final BinaryTag tag : tags) {
      if (type == null) {
        type = tag.type();
      } else {
        mustBeSameType(tag, type);
      }
    }
    return type;
  }

  // Cannot have different element types in a list tag
  static void mustBeSameType(final BinaryTag tag, final BinaryTagType<? extends BinaryTag> type) {
    if (tag.type() != type) {
      throw new IllegalArgumentException(String.format("Trying to add tag of type %s to list of %s", tag.type(), type));
    }
  }

  private ListBinaryTag edit(final Consumer<List<BinaryTag>> consumer, final @Nullable BinaryTagType<? extends BinaryTag> maybeElementType) {
    final List<BinaryTag> tags = new ArrayList<>(this.tags);
    consumer.accept(tags);
    BinaryTagType<? extends BinaryTag> elementType = this.elementType;
    // set the type if it has not yet been set
    if (maybeElementType != null && elementType == BinaryTagTypes.END) {
      elementType = maybeElementType;
    }
    return new ListBinaryTagImpl(elementType, tags);
  }

  @Override
  public @NotNull Stream<BinaryTag> stream() {
    return this.tags.stream();
  }

  @Override
  public Iterator<BinaryTag> iterator() {
    final Iterator<BinaryTag> iterator = this.tags.iterator();
    return new Iterator<BinaryTag>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public BinaryTag next() {
        return iterator.next();
      }

      @Override
      public void forEachRemaining(final Consumer<? super BinaryTag> action) {
        iterator.forEachRemaining(action);
      }
    };
  }

  @Override
  public void forEach(final Consumer<? super BinaryTag> action) {
    this.tags.forEach(action);
  }

  @Override
  public Spliterator<BinaryTag> spliterator() {
    return Spliterators.spliterator(this.tags, Spliterator.ORDERED | Spliterator.IMMUTABLE);
  }

  @Override
  public boolean equals(final Object that) {
    return this == that || (that instanceof ListBinaryTagImpl && this.tags.equals(((ListBinaryTagImpl) that).tags));
  }

  @Override
  public int hashCode() {
    return this.hashCode;
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("tags", this.tags),
      ExaminableProperty.of("type", this.elementType)
    );
  }
}

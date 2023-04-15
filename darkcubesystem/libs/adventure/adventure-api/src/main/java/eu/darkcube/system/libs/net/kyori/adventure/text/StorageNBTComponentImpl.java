/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.List;
import java.util.Objects;

import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

final class StorageNBTComponentImpl extends NBTComponentImpl<StorageNBTComponent, StorageNBTComponent.Builder> implements StorageNBTComponent {
  private final Key storage;

  static @NotNull StorageNBTComponent create(final @NotNull List<? extends ComponentLike> children, final @NotNull Style style, final String nbtPath, final boolean interpret, final @Nullable ComponentLike separator, final @NotNull Key storage) {
    return new StorageNBTComponentImpl(
      ComponentLike.asComponents(children, IS_NOT_EMPTY),
      requireNonNull(style, "style"),
      requireNonNull(nbtPath, "nbtPath"),
      interpret,
      ComponentLike.unbox(separator),
      requireNonNull(storage, "storage")
    );
  }

  StorageNBTComponentImpl(final @NotNull List<Component> children, final @NotNull Style style, final String nbtPath, final boolean interpret, final @Nullable Component separator, final Key storage) {
    super(children, style, nbtPath, interpret, separator);
    this.storage = storage;
  }

  @Override
  public @NotNull StorageNBTComponent nbtPath(final @NotNull String nbtPath) {
    if (Objects.equals(this.nbtPath, nbtPath)) return this;
    return create(this.children, this.style, nbtPath, this.interpret, this.separator, this.storage);
  }

  @Override
  public @NotNull StorageNBTComponent interpret(final boolean interpret) {
    if (this.interpret == interpret) return this;
    return create(this.children, this.style, this.nbtPath, interpret, this.separator, this.storage);
  }

  @Override
  public @Nullable Component separator() {
    return this.separator;
  }

  @Override
  public @NotNull StorageNBTComponent separator(final @Nullable ComponentLike separator) {
    return create(this.children, this.style, this.nbtPath, this.interpret, separator, this.storage);
  }

  @Override
  public @NotNull Key storage() {
    return this.storage;
  }

  @Override
  public @NotNull StorageNBTComponent storage(final @NotNull Key storage) {
    if (Objects.equals(this.storage, storage)) return this;
    return create(this.children, this.style, this.nbtPath, this.interpret, this.separator, storage);
  }

  @Override
  public @NotNull StorageNBTComponent children(final @NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.nbtPath, this.interpret, this.separator, this.storage);
  }

  @Override
  public @NotNull StorageNBTComponent style(final @NotNull Style style) {
    return create(this.children, style, this.nbtPath, this.interpret, this.separator, this.storage);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof StorageNBTComponent)) return false;
    if (!super.equals(other)) return false;
    final StorageNBTComponentImpl that = (StorageNBTComponentImpl) other;
    return Objects.equals(this.storage, that.storage());
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (31 * result) + this.storage.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }

  @Override
  public StorageNBTComponent.@NotNull Builder toBuilder() {
    return new BuilderImpl(this);
  }

  static class BuilderImpl extends AbstractNBTComponentBuilder<StorageNBTComponent, StorageNBTComponent.Builder> implements StorageNBTComponent.Builder {
    private @Nullable Key storage;

    BuilderImpl() {
    }

    BuilderImpl(final @NotNull StorageNBTComponent component) {
      super(component);
      this.storage = component.storage();
    }

    @Override
    public StorageNBTComponent.@NotNull Builder storage(final @NotNull Key storage) {
      this.storage = requireNonNull(storage, "storage");
      return this;
    }

    @Override
    public @NotNull StorageNBTComponent build() {
      if (this.nbtPath == null) throw new IllegalStateException("nbt path must be set");
      if (this.storage == null) throw new IllegalStateException("storage must be set");
      return create(this.children, this.buildStyle(), this.nbtPath, this.interpret, this.separator, this.storage);
    }
  }
}

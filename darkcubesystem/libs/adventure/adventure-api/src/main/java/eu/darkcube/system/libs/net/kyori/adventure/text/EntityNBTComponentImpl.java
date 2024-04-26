/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.List;
import java.util.Objects;

import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

final class EntityNBTComponentImpl extends NBTComponentImpl<EntityNBTComponent, EntityNBTComponent.Builder> implements EntityNBTComponent {
  private final String selector;

  static EntityNBTComponent create(final @NotNull List<? extends ComponentLike> children, final @NotNull Style style, final String nbtPath, final boolean interpret, final @Nullable ComponentLike separator, final String selector) {
    return new EntityNBTComponentImpl(
      ComponentLike.asComponents(children, IS_NOT_EMPTY),
      requireNonNull(style, "style"),
      requireNonNull(nbtPath, "nbtPath"),
      interpret,
      ComponentLike.unbox(separator),
      requireNonNull(selector, "selector")
    );
  }

  EntityNBTComponentImpl(final @NotNull List<Component> children, final @NotNull Style style, final String nbtPath, final boolean interpret, final @Nullable Component separator, final String selector) {
    super(children, style, nbtPath, interpret, separator);
    this.selector = selector;
  }

  @Override
  public @NotNull EntityNBTComponent nbtPath(final @NotNull String nbtPath) {
    if (Objects.equals(this.nbtPath, nbtPath)) return this;
    return create(this.children, this.style, nbtPath, this.interpret, this.separator, this.selector);
  }

  @Override
  public @NotNull EntityNBTComponent interpret(final boolean interpret) {
    if (this.interpret == interpret) return this;
    return create(this.children, this.style, this.nbtPath, interpret, this.separator, this.selector);
  }

  @Override
  public @Nullable Component separator() {
    return this.separator;
  }

  @Override
  public @NotNull EntityNBTComponent separator(final @Nullable ComponentLike separator) {
    return create(this.children, this.style, this.nbtPath, this.interpret, separator, this.selector);
  }

  @Override
  public @NotNull String selector() {
    return this.selector;
  }

  @Override
  public @NotNull EntityNBTComponent selector(final @NotNull String selector) {
    if (Objects.equals(this.selector, selector)) return this;
    return create(this.children, this.style, this.nbtPath, this.interpret, this.separator, selector);
  }

  @Override
  public @NotNull EntityNBTComponent children(final @NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.nbtPath, this.interpret, this.separator, this.selector);
  }

  @Override
  public @NotNull EntityNBTComponent style(final @NotNull Style style) {
    return create(this.children, style, this.nbtPath, this.interpret, this.separator, this.selector);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof EntityNBTComponent)) return false;
    if (!super.equals(other)) return false;
    final EntityNBTComponentImpl that = (EntityNBTComponentImpl) other;
    return Objects.equals(this.selector, that.selector());
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (31 * result) + this.selector.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }

  @Override
  public EntityNBTComponent.@NotNull Builder toBuilder() {
    return new BuilderImpl(this);
  }

  static final class BuilderImpl extends AbstractNBTComponentBuilder<EntityNBTComponent, EntityNBTComponent.Builder> implements EntityNBTComponent.Builder {
    private @Nullable String selector;

    BuilderImpl() {
    }

    BuilderImpl(final @NotNull EntityNBTComponent component) {
      super(component);
      this.selector = component.selector();
    }

    @Override
    public EntityNBTComponent.@NotNull Builder selector(final @NotNull String selector) {
      this.selector = requireNonNull(selector, "selector");
      return this;
    }

    @Override
    public @NotNull EntityNBTComponent build() {
      if (this.nbtPath == null) throw new IllegalStateException("nbt path must be set");
      if (this.selector == null) throw new IllegalStateException("selector must be set");
      return create(this.children, this.buildStyle(), this.nbtPath, this.interpret, this.separator, this.selector);
    }
  }
}

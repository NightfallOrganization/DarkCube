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

final class SelectorComponentImpl extends AbstractComponent implements SelectorComponent {
  private final String pattern;
  private final @Nullable Component separator;

  static SelectorComponent create(final @NotNull List<? extends ComponentLike> children, final @NotNull Style style, final @NotNull String pattern, final @Nullable ComponentLike separator) {
    return new SelectorComponentImpl(
      ComponentLike.asComponents(children, IS_NOT_EMPTY),
      requireNonNull(style, "style"),
      requireNonNull(pattern, "pattern"),
      ComponentLike.unbox(separator)
    );
  }

  SelectorComponentImpl(final @NotNull List<Component> children, final @NotNull Style style, final @NotNull String pattern, final @Nullable Component separator) {
    super(children, style);
    this.pattern = pattern;
    this.separator = separator;
  }

  @Override
  public @NotNull String pattern() {
    return this.pattern;
  }

  @Override
  public @NotNull SelectorComponent pattern(final @NotNull String pattern) {
    if (Objects.equals(this.pattern, pattern)) return this;
    return create(this.children, this.style, pattern, this.separator);
  }

  @Override
  public @Nullable Component separator() {
    return this.separator;
  }

  @Override
  public @NotNull SelectorComponent separator(final @Nullable ComponentLike separator) {
    return create(this.children, this.style, this.pattern, separator);
  }

  @Override
  public @NotNull SelectorComponent children(final @NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.pattern, this.separator);
  }

  @Override
  public @NotNull SelectorComponent style(final @NotNull Style style) {
    return create(this.children, style, this.pattern, this.separator);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof SelectorComponent)) return false;
    if (!super.equals(other)) return false;
    final SelectorComponent that = (SelectorComponent) other;
    return Objects.equals(this.pattern, that.pattern()) && Objects.equals(this.separator, that.separator());
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (31 * result) + this.pattern.hashCode();
    result = (31 * result) + Objects.hashCode(this.separator);
    return result;
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }

  @Override
  public @NotNull Builder toBuilder() {
    return new BuilderImpl(this);
  }

  static final class BuilderImpl extends AbstractComponentBuilder<SelectorComponent, Builder> implements SelectorComponent.Builder {
    private @Nullable String pattern;
    private @Nullable Component separator;

    BuilderImpl() {
    }

    BuilderImpl(final @NotNull SelectorComponent component) {
      super(component);
      this.pattern = component.pattern();
      this.separator = component.separator();
    }

    @Override
    public @NotNull Builder pattern(final @NotNull String pattern) {
      this.pattern = requireNonNull(pattern, "pattern");
      return this;
    }

    @Override
    public @NotNull Builder separator(final @Nullable ComponentLike separator) {
      this.separator = ComponentLike.unbox(separator);
      return this;
    }

    @Override
    public @NotNull SelectorComponent build() {
      if (this.pattern == null) throw new IllegalStateException("pattern must be set");
      return create(this.children, this.buildStyle(), this.pattern, this.separator);
    }
  }
}

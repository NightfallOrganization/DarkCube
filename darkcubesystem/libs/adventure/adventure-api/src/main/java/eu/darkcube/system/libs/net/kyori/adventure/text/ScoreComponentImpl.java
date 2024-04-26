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

final class ScoreComponentImpl extends AbstractComponent implements ScoreComponent {
  private final String name;
  private final String objective;
  @Deprecated
  private final @Nullable String value;

  static ScoreComponent create(final @NotNull List<? extends ComponentLike> children, final @NotNull Style style, final @NotNull String name, final @NotNull String objective, final @Nullable String value) {
    return new ScoreComponentImpl(
      ComponentLike.asComponents(children, IS_NOT_EMPTY),
      requireNonNull(style, "style"),
      requireNonNull(name, "name"),
      requireNonNull(objective, "objective"),
      value
    );
  }

  ScoreComponentImpl(final @NotNull List<Component> children, final @NotNull Style style, final @NotNull String name, final @NotNull String objective, final @Nullable String value) {
    super(children, style);
    this.name = name;
    this.objective = objective;
    this.value = value;
  }

  @Override
  public @NotNull String name() {
    return this.name;
  }

  @Override
  public @NotNull ScoreComponent name(final @NotNull String name) {
    if (Objects.equals(this.name, name)) return this;
    return create(this.children, this.style, name, this.objective, this.value);
  }

  @Override
  public @NotNull String objective() {
    return this.objective;
  }

  @Override
  public @NotNull ScoreComponent objective(final @NotNull String objective) {
    if (Objects.equals(this.objective, objective)) return this;
    return create(this.children, this.style, this.name, objective, this.value);
  }

  @Override
  @Deprecated
  public @Nullable String value() {
    return this.value;
  }

  @Override
  @Deprecated
  public @NotNull ScoreComponent value(final @Nullable String value) {
    if (Objects.equals(this.value, value)) return this;
    return create(this.children, this.style, this.name, this.objective, value);
  }

  @Override
  public @NotNull ScoreComponent children(final @NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.name, this.objective, this.value);
  }

  @Override
  public @NotNull ScoreComponent style(final @NotNull Style style) {
    return create(this.children, style, this.name, this.objective, this.value);
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof ScoreComponent)) return false;
    if (!super.equals(other)) return false;
    final ScoreComponent that = (ScoreComponent) other;
    return Objects.equals(this.name, that.name())
      && Objects.equals(this.objective, that.objective())
      && Objects.equals(this.value, that.value());
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (31 * result) + this.name.hashCode();
    result = (31 * result) + this.objective.hashCode();
    result = (31 * result) + Objects.hashCode(this.value);
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

  static final class BuilderImpl extends AbstractComponentBuilder<ScoreComponent, Builder> implements ScoreComponent.Builder {
    private @Nullable String name;
    private @Nullable String objective;
    private @Nullable String value;

    BuilderImpl() {
    }

    @SuppressWarnings("deprecation")
    BuilderImpl(final @NotNull ScoreComponent component) {
      super(component);
      this.name = component.name();
      this.objective = component.objective();
      this.value = component.value();
    }

    @Override
    public @NotNull Builder name(final @NotNull String name) {
      this.name = requireNonNull(name, "name");
      return this;
    }

    @Override
    public @NotNull Builder objective(final @NotNull String objective) {
      this.objective = requireNonNull(objective, "objective");
      return this;
    }

    @Override
    @Deprecated
    public @NotNull Builder value(final @Nullable String value) {
      this.value = value;
      return this;
    }

    @Override
    public @NotNull ScoreComponent build() {
      if (this.name == null) throw new IllegalStateException("name must be set");
      if (this.objective == null) throw new IllegalStateException("objective must be set");
      return create(this.children, this.buildStyle(), this.name, this.objective, this.value);
    }
  }
}

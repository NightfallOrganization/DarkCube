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

final class KeybindComponentImpl extends AbstractComponent implements KeybindComponent {
  private final String keybind;

  static KeybindComponent create(final @NotNull List<? extends ComponentLike> children, final @NotNull Style style, final @NotNull String keybind) {
    return new KeybindComponentImpl(
      ComponentLike.asComponents(children, IS_NOT_EMPTY),
      requireNonNull(style, "style"),
      requireNonNull(keybind, "keybind")
    );
  }

  KeybindComponentImpl(final @NotNull List<Component> children, final @NotNull Style style, final @NotNull String keybind) {
    super(children, style);
    this.keybind = keybind;
  }

  @Override
  public @NotNull String keybind() {
    return this.keybind;
  }

  @Override
  public @NotNull KeybindComponent keybind(final @NotNull String keybind) {
    if (Objects.equals(this.keybind, keybind)) return this;
    return create(this.children, this.style, keybind);
  }

  @Override
  public @NotNull KeybindComponent children(final @NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.keybind);
  }

  @Override
  public @NotNull KeybindComponent style(final @NotNull Style style) {
    return create(this.children, style, this.keybind);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof KeybindComponent)) return false;
    if (!super.equals(other)) return false;
    final KeybindComponent that = (KeybindComponent) other;
    return Objects.equals(this.keybind, that.keybind());
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (31 * result) + this.keybind.hashCode();
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

  static final class BuilderImpl extends AbstractComponentBuilder<KeybindComponent, Builder> implements KeybindComponent.Builder {
    private @Nullable String keybind;

    BuilderImpl() {
    }

    BuilderImpl(final @NotNull KeybindComponent component) {
      super(component);
      this.keybind = component.keybind();
    }

    @Override
    public @NotNull Builder keybind(final @NotNull String keybind) {
      this.keybind = requireNonNull(keybind, "keybind");
      return this;
    }

    @Override
    public @NotNull KeybindComponent build() {
      if (this.keybind == null) throw new IllegalStateException("keybind must be set");
      return create(this.children, this.buildStyle(), this.keybind);
    }
  }
}

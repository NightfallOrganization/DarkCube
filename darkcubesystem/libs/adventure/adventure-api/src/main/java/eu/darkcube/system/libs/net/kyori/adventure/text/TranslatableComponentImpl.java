/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

final class TranslatableComponentImpl extends AbstractComponent implements TranslatableComponent {
  static TranslatableComponent create(final @NotNull List<Component> children, final @NotNull Style style, final @NotNull String key, final @NotNull ComponentLike@NotNull[] args) {
    requireNonNull(args, "args");
    return create(children, style, key, Arrays.asList(args));
  }

  static TranslatableComponent create(final @NotNull List<? extends ComponentLike> children, final @NotNull Style style, final @NotNull String key, final @NotNull List<? extends ComponentLike> args) {
    return new TranslatableComponentImpl(
      ComponentLike.asComponents(children, IS_NOT_EMPTY),
      requireNonNull(style, "style"),
      requireNonNull(key, "key"),
      ComponentLike.asComponents(args) // Since translation arguments can be indexed, empty components are also included.
    );
  }

  private final String key;
  private final List<Component> args;

  TranslatableComponentImpl(final @NotNull List<Component> children, final @NotNull Style style, final @NotNull String key, final @NotNull List<Component> args) {
    super(children, style);
    this.key = key;
    this.args = args;
  }

  @Override
  public @NotNull String key() {
    return this.key;
  }

  @Override
  public @NotNull TranslatableComponent key(final @NotNull String key) {
    if (Objects.equals(this.key, key)) return this;
    return create(this.children, this.style, key, this.args);
  }

  @Override
  public @NotNull List<Component> args() {
    return this.args;
  }

  @Override
  public @NotNull TranslatableComponent args(final @NotNull ComponentLike@NotNull... args) {
    return create(this.children, this.style, this.key, args);
  }

  @Override
  public @NotNull TranslatableComponent args(final @NotNull List<? extends ComponentLike> args) {
    return create(this.children, this.style, this.key, args);
  }

  @Override
  public @NotNull TranslatableComponent children(final @NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.key, this.args);
  }

  @Override
  public @NotNull TranslatableComponent style(final @NotNull Style style) {
    return create(this.children, style, this.key, this.args);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof TranslatableComponent)) return false;
    if (!super.equals(other)) return false;
    final TranslatableComponent that = (TranslatableComponent) other;
    return Objects.equals(this.key, that.key()) && Objects.equals(this.args, that.args());
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (31 * result) + this.key.hashCode();
    result = (31 * result) + this.args.hashCode();
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

  static final class BuilderImpl extends AbstractComponentBuilder<TranslatableComponent, Builder> implements TranslatableComponent.Builder {
    private @Nullable String key;
    private List<? extends Component> args = Collections.emptyList();

    BuilderImpl() {
    }

    BuilderImpl(final @NotNull TranslatableComponent component) {
      super(component);
      this.key = component.key();
      this.args = component.args();
    }

    @Override
    public @NotNull Builder key(final @NotNull String key) {
      this.key = key;
      return this;
    }

    @Override
    public @NotNull Builder args(final @NotNull ComponentBuilder<?, ?> arg) {
      return this.args(Collections.singletonList(requireNonNull(arg, "arg").build()));
    }

    @Override
    @SuppressWarnings("checkstyle:GenericWhitespace")
    public @NotNull Builder args(final @NotNull ComponentBuilder<?, ?>@NotNull... args) {
      requireNonNull(args, "args");
      if (args.length == 0) return this.args(Collections.emptyList());
      return this.args(Stream.of(args).map(ComponentBuilder::build).collect(Collectors.toList()));
    }

    @Override
    public @NotNull Builder args(final @NotNull Component arg) {
      return this.args(Collections.singletonList(requireNonNull(arg, "arg")));
    }

    @Override
    public @NotNull Builder args(final @NotNull ComponentLike@NotNull... args) {
      requireNonNull(args, "args");
      if (args.length == 0) return this.args(Collections.emptyList());
      return this.args(Arrays.asList(args));
    }

    @Override
    public @NotNull Builder args(final @NotNull List<? extends ComponentLike> args) {
      this.args = ComponentLike.asComponents(requireNonNull(args, "args"));
      return this;
    }

    @Override
    public @NotNull TranslatableComponent build() {
      if (this.key == null) throw new IllegalStateException("key must be set");
      return create(this.children, this.buildStyle(), this.key, this.args);
    }
  }
}

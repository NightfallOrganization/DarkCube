/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.net.kyori.adventure.internal.properties.AdventureProperties;
import eu.darkcube.system.libs.net.kyori.adventure.util.Nag;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.VisibleForTesting;

import static java.util.Objects.requireNonNull;

final class TextComponentImpl extends AbstractComponent implements TextComponent {
  private static final boolean WARN_WHEN_LEGACY_FORMATTING_DETECTED = Boolean.TRUE.equals(AdventureProperties.TEXT_WARN_WHEN_LEGACY_FORMATTING_DETECTED.value());
  @VisibleForTesting
  static final char SECTION_CHAR = '§';

  static final TextComponent EMPTY = createDirect("");
  static final TextComponent NEWLINE = createDirect("\n");
  static final TextComponent SPACE = createDirect(" ");

  static TextComponent create(final @NotNull List<? extends ComponentLike> children, final @NotNull Style style, final @NotNull String content) {
    final List<Component> filteredChildren = ComponentLike.asComponents(children, IS_NOT_EMPTY);
    if (filteredChildren.isEmpty() && style.isEmpty() && content.isEmpty()) return Component.empty();

    return new TextComponentImpl(
      filteredChildren,
      requireNonNull(style, "style"),
      requireNonNull(content, "content")
    );
  }

  private static @NotNull TextComponent createDirect(final @NotNull String content) {
    return new TextComponentImpl(Collections.emptyList(), Style.empty(), content);
  }

  private final String content;

  TextComponentImpl(final @NotNull List<Component> children, final @NotNull Style style, final @NotNull String content) {
    super(children, style);
    this.content = content;

    if (WARN_WHEN_LEGACY_FORMATTING_DETECTED) {
      final LegacyFormattingDetected nag = this.warnWhenLegacyFormattingDetected();
      if (nag != null) {
        Nag.print(nag);
      }
    }
  }

  @VisibleForTesting
  final @Nullable LegacyFormattingDetected warnWhenLegacyFormattingDetected() {
    if (this.content.indexOf(SECTION_CHAR) != -1) {
      return new LegacyFormattingDetected(this);
    }
    return null;
  }

  @Override
  public @NotNull String content() {
    return this.content;
  }

  @Override
  public @NotNull TextComponent content(final @NotNull String content) {
    if (Objects.equals(this.content, content)) return this;
    return create(this.children, this.style, content);
  }

  @Override
  public @NotNull TextComponent children(final @NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.content);
  }

  @Override
  public @NotNull TextComponent style(final @NotNull Style style) {
    return create(this.children, style, this.content);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof TextComponentImpl)) return false;
    if (!super.equals(other)) return false;
    final TextComponentImpl that = (TextComponentImpl) other;
    return Objects.equals(this.content, that.content);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (31 * result) + this.content.hashCode();
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

  static final class BuilderImpl extends AbstractComponentBuilder<TextComponent, Builder> implements TextComponent.Builder {
    /*
     * We default to an empty string to avoid needing to manually set the
     * content of a newly-created builder when we only want to append other
     * components to the one being built.
     */
    private String content = "";

    BuilderImpl() {
    }

    BuilderImpl(final @NotNull TextComponent component) {
      super(component);
      this.content = component.content();
    }

    @Override
    public @NotNull Builder content(final @NotNull String content) {
      this.content = requireNonNull(content, "content");
      return this;
    }

    @Override
    public @NotNull String content() {
      return this.content;
    }

    @Override
    public @NotNull TextComponent build() {
      if (this.isEmpty()) {
        return Component.empty();
      }
      return create(this.children, this.buildStyle(), this.content);
    }

    private boolean isEmpty() {
      return this.content.isEmpty() && this.children.isEmpty() && !this.hasStyle();
    }
  }
}

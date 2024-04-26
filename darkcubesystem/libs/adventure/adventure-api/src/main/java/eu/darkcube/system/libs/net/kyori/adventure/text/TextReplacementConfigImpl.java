/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.function.BiFunction;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

final class TextReplacementConfigImpl implements TextReplacementConfig {
  private final Pattern matchPattern;
  private final BiFunction<MatchResult, TextComponent.Builder, @Nullable ComponentLike> replacement;
  private final Condition continuer;

  TextReplacementConfigImpl(final Builder builder) {
    this.matchPattern = builder.matchPattern;
    this.replacement = builder.replacement;
    this.continuer = builder.continuer;
  }

  @Override
  public @NotNull Pattern matchPattern() {
    return this.matchPattern;
  }

  TextReplacementRenderer.State createState() {
    return new TextReplacementRenderer.State(this.matchPattern, this.replacement, this.continuer);
  }

  @Override
  public TextReplacementConfig.@NotNull Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("matchPattern", this.matchPattern),
      ExaminableProperty.of("replacement", this.replacement),
      ExaminableProperty.of("continuer", this.continuer)
    );
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }

  static final class Builder implements TextReplacementConfig.Builder {
    @Nullable Pattern matchPattern;
    @Nullable BiFunction<MatchResult, TextComponent.Builder, @Nullable ComponentLike> replacement;
    TextReplacementConfig.Condition continuer = (matchResult, index, replacement) -> PatternReplacementResult.REPLACE;

    Builder() {
    }

    Builder(final TextReplacementConfigImpl instance) {
      this.matchPattern = instance.matchPattern;
      this.replacement = instance.replacement;
      this.continuer = instance.continuer;
    }

    @Override
    public @NotNull Builder match(final @NotNull Pattern pattern) {
      this.matchPattern = requireNonNull(pattern, "pattern");
      return this;
    }

    @Override
    public @NotNull Builder condition(final TextReplacementConfig.@NotNull Condition condition) {
      this.continuer = requireNonNull(condition, "continuation");
      return this;
    }

    @Override
    public @NotNull Builder replacement(final @NotNull BiFunction<MatchResult, TextComponent.Builder, @Nullable ComponentLike> replacement) {
      this.replacement = requireNonNull(replacement, "replacement");
      return this;
    }

    @Override
    public @NotNull TextReplacementConfig build() {
      if (this.matchPattern == null) throw new IllegalStateException("A pattern must be provided to match against");
      if (this.replacement == null) throw new IllegalStateException("A replacement action must be provided");
      return new TextReplacementConfigImpl(this);
    }
  }
}

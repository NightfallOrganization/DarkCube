/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.translation;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import eu.darkcube.system.libs.net.kyori.adventure.text.renderer.TranslatableComponentRenderer;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

final class GlobalTranslatorImpl implements GlobalTranslator {
  private static final Key NAME = Key.key("adventure", "global");
  static final GlobalTranslatorImpl INSTANCE = new GlobalTranslatorImpl();
  final TranslatableComponentRenderer<Locale> renderer = TranslatableComponentRenderer.usingTranslationSource(this);
  private final Set<Translator> sources = Collections.newSetFromMap(new ConcurrentHashMap<>());

  private GlobalTranslatorImpl() {
  }

  @Override
  public @NotNull Key name() {
    return NAME;
  }

  @Override
  public @NotNull Iterable<? extends Translator> sources() {
    return Collections.unmodifiableSet(this.sources);
  }

  @Override
  public boolean addSource(final @NotNull Translator source) {
    requireNonNull(source, "source");
    if (source == this) throw new IllegalArgumentException("GlobalTranslationSource");
    return this.sources.add(source);
  }

  @Override
  public boolean removeSource(final @NotNull Translator source) {
    requireNonNull(source, "source");
    return this.sources.remove(source);
  }

  @Override
  public @Nullable MessageFormat translate(final @NotNull String key, final @NotNull Locale locale) {
    requireNonNull(key, "key");
    requireNonNull(locale, "locale");
    for (final Translator source : this.sources) {
      final MessageFormat translation = source.translate(key, locale);
      if (translation != null) return translation;
    }
    return null;
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("sources", this.sources));
  }
}

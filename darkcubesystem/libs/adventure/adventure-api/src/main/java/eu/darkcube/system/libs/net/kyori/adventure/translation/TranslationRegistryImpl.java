/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.translation;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

final class TranslationRegistryImpl implements Examinable, TranslationRegistry {
  private final Key name;
  private final Map<String, Translation> translations = new ConcurrentHashMap<>();
  private Locale defaultLocale = Locale.US; // en_us

  TranslationRegistryImpl(final Key name) {
    this.name = name;
  }

  @Override
  public void register(final @NotNull String key, final @NotNull Locale locale, final @NotNull MessageFormat format) {
    this.translations.computeIfAbsent(key, Translation::new).register(locale, format);
  }

  @Override
  public void unregister(final @NotNull String key) {
    this.translations.remove(key);
  }

  @Override
  public @NotNull Key name() {
    return this.name;
  }

  @Override
  public boolean contains(final @NotNull String key) {
    return this.translations.containsKey(key);
  }

  @Override
  public @Nullable MessageFormat translate(final @NotNull String key, final @NotNull Locale locale) {
    final Translation translation = this.translations.get(key);
    if (translation == null) return null;
    return translation.translate(locale);
  }

  @Override
  public void defaultLocale(final @NotNull Locale defaultLocale) {
    this.defaultLocale = requireNonNull(defaultLocale, "defaultLocale");
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(ExaminableProperty.of("translations", this.translations));
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) return true;
    if (!(other instanceof TranslationRegistryImpl)) return false;

    final TranslationRegistryImpl that = (TranslationRegistryImpl) other;

    return this.name.equals(that.name)
      && this.translations.equals(that.translations)
      && this.defaultLocale.equals(that.defaultLocale);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.translations, this.defaultLocale);
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }

  final class Translation implements Examinable {
    private final String key;
    private final Map<Locale, MessageFormat> formats;

    Translation(final @NotNull String key) {
      this.key = requireNonNull(key, "translation key");
      this.formats = new ConcurrentHashMap<>();
    }

    void register(final @NotNull Locale locale, final @NotNull MessageFormat format) {
      if (this.formats.putIfAbsent(requireNonNull(locale, "locale"), requireNonNull(format, "message format")) != null) {
        throw new IllegalArgumentException(String.format("Translation already exists: %s for %s", this.key, locale));
      }
    }

    @Nullable MessageFormat translate(final @NotNull Locale locale) {
      MessageFormat format = this.formats.get(requireNonNull(locale, "locale"));
      if (format == null) {
        format = this.formats.get(new Locale(locale.getLanguage())); // try without country
        if (format == null) {
          format = this.formats.get(TranslationRegistryImpl.this.defaultLocale); // try local default locale
          if (format == null) {
            format = this.formats.get(TranslationLocales.global()); // try global default locale
          }
        }
      }
      return format;
    }

    @Override
    public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(
        ExaminableProperty.of("key", this.key),
        ExaminableProperty.of("formats", this.formats)
      );
    }

    @Override
    public boolean equals(final Object other) {
      if (this == other) return true;
      if (!(other instanceof Translation)) return false;
      final Translation that = (Translation) other;
      return this.key.equals(that.key) &&
        this.formats.equals(that.formats);
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.key, this.formats);
    }

    @Override
    public String toString() {
      return Internals.toString(this);
    }
  }
}

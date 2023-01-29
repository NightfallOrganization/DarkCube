/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.translation;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * A message format translator.
 *
 * <p>To see how to create a {@link Translator} with a {@link ResourceBundle}
 * see {@link TranslationRegistry#registerAll(Locale, ResourceBundle, boolean)}</p>
 *
 * <p>After creating a {@link Translator} you can add it to the {@link GlobalTranslator}
 * to enable automatic translations by the platforms.</p>
 *
 * @see TranslationRegistry
 * @since 4.0.0
 */
public interface Translator {
  /**
   * Parses a {@link Locale} from a {@link String}.
   *
   * @param string the string
   * @return a locale
   * @since 4.0.0
   */
  static @Nullable Locale parseLocale(final @NotNull String string) {
    final String[] segments = string.split("_", 3); // language_country_variant
    final int length = segments.length;
    if (length == 1) {
      return new Locale(string); // language
    } else if (length == 2) {
      return new Locale(segments[0], segments[1]); // language + country
    } else if (length == 3) {
      return new Locale(segments[0], segments[1], segments[2]); // language + country + variant
    }
    return null;
  }

  /**
   * A key identifying this translation source.
   *
   * <p>Intended to be used for display to users.</p>
   *
   * @return an identifier for this translation source
   * @since 4.0.0
   */
  @NotNull Key name();

  /**
   * Gets a message format from a key and locale.
   *
   * @param locale a locale
   * @param key a translation key
   * @return a message format or {@code null} to skip translation
   * @since 4.0.0
   */
  @Nullable MessageFormat translate(final @NotNull String key, final @NotNull Locale locale);
}

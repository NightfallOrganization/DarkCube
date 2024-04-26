/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.translation;

import java.util.Locale;
import java.util.function.Supplier;
import eu.darkcube.system.libs.net.kyori.adventure.internal.properties.AdventureProperties;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class TranslationLocales {
  private static final Supplier<Locale> GLOBAL;

  static {
    final @Nullable String property = AdventureProperties.DEFAULT_TRANSLATION_LOCALE.value();
    if (property == null || property.isEmpty()) {
      GLOBAL = () -> Locale.US;
    } else if (property.equals("system")) {
      GLOBAL = Locale::getDefault;
    } else {
      final Locale locale = Translator.parseLocale(property);
      GLOBAL = () -> locale;
    }
  }

  private TranslationLocales() {
  }

  static Locale global() {
    return GLOBAL.get();
  }
}

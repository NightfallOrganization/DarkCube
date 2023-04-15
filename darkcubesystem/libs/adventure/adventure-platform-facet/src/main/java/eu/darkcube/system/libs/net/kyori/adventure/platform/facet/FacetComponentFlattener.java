/*
 * Copyright (c) 2018-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.platform.facet;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.TranslatableComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.flattener.ComponentFlattener;
import eu.darkcube.system.libs.net.kyori.adventure.translation.GlobalTranslator;
import eu.darkcube.system.libs.net.kyori.adventure.translation.TranslationRegistry;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * A provider for component flatteners that uses the {@link Facet} system to access implementation details.
 *
 * @since 4.0.0
 */
@ApiStatus.Internal
public final class FacetComponentFlattener {
  private static final Pattern LOCALIZATION_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?s");

  private FacetComponentFlattener() {
  }

  /**
   * Create a new flattener, picking an applicable translator.
   *
   * <p>If no translators are available, keys will be passed through untransformed.</p>
   *
   * @param instance the game/server instance
   * @param candidates potential facets
   * @param <V> instance type
   * @return a new flattener
   * @since 4.0.0
   */
  public static <V> ComponentFlattener get(final V instance, final Collection<? extends Translator<V>> candidates) {
    final Translator<V> translator = Facet.of(candidates, instance);
    final ComponentFlattener.Builder flattenerBuilder = ComponentFlattener.basic().toBuilder();
    flattenerBuilder.complexMapper(TranslatableComponent.class, (translatable, consumer) -> {
      final String key = translatable.key();
      for (final eu.darkcube.system.libs.net.kyori.adventure.translation.Translator registry : GlobalTranslator.translator().sources()) {
        if (registry instanceof TranslationRegistry && ((TranslationRegistry) registry).contains(key)) {
          consumer.accept(GlobalTranslator.render(translatable, Locale.getDefault()));
          return;
        }
      }

      final @NotNull String translated = translator == null ? key : translator.valueOrDefault(instance, key);
      final Matcher matcher = LOCALIZATION_PATTERN.matcher(translated);
      final List<Component> args = translatable.args();
      int argPosition = 0;
      int lastIdx = 0;
      while (matcher.find()) {
        // append prior
        if (lastIdx < matcher.start()) consumer.accept(Component.text(translated.substring(lastIdx, matcher.start())));
        lastIdx = matcher.end();

        final @Nullable String argIdx = matcher.group(1);
        // calculate argument position
        if (argIdx != null) {
          try {
            final int idx = Integer.parseInt(argIdx) - 1;
            if (idx < args.size()) {
              consumer.accept(args.get(idx));
            }
          } catch (final NumberFormatException ex) {
            // ignore, drop the format placeholder
          }
        } else {
          final int idx = argPosition++;
          if (idx < args.size()) {
            consumer.accept(args.get(idx));
          }
        }
      }

      // append tail
      if (lastIdx < translated.length()) {
        consumer.accept(Component.text(translated.substring(lastIdx)));
      }
    });

    return flattenerBuilder.build();
  }

  /**
   * An interface to the game's own translation system.
   *
   * @param <V> the game
   * @since 4.0.0
   */
  public interface Translator<V> extends Facet<V> {
    /**
     * Get the translation for {@code key} or return {@code key}.
     *
     * @param game the game instance
     * @param key the key
     * @return a translation or the key
     * @since 4.0.0
     */
    @NotNull String valueOrDefault(final @NotNull V game, final @NotNull String key);
  }
}

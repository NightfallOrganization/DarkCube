/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.translation.GlobalTranslator;
import eu.darkcube.system.libs.net.kyori.adventure.translation.Translatable;
import eu.darkcube.system.libs.net.kyori.adventure.translation.TranslationRegistry;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A component that can display translated text.
 *
 * <p>This component consists of:</p>
 * <dl>
 *   <dt>key</dt>
 *   <dd>a translation key used together with the viewer locale to fetch a translated string.</dd>
 *   <dt>args(optional)</dt>
 *   <dd>components that can be used as arguments in the translated string.
 *   <p>(e.g "You picked up <b>{0}</b>." -&#62; "You picked up <b>Carrot</b>.")</p></dd>
 * </dl>
 *
 * <p>Displaying this component through an {@link Audience} will run it through the {@link GlobalTranslator} by default,
 * rendering the key as translated text if a translation with a key matching this components key is found in the viewers locale,
 * optionally switching arguments with any placeholders in the discovered translation. If no translation is registered for the viewers locale
 * adventure will first try to find similar locales that has a valid translation, and then find a translation in the default language({@link TranslationRegistry#defaultLocale(Locale) relevant method}).</p>
 *
 * <p>In addition to the initial attempts, if no translation is found in the serverside registry,
 * the translation key and arguments will be passed through to the client which will perform translation using any
 * keys defined in an active resource pack. (Hint: vanilla Minecraft is also considered a resource pack)</p>
 *
 * @see GlobalTranslator
 * @see TranslationRegistry
 * @since 4.0.0
 */
public interface TranslatableComponent extends BuildableComponent<TranslatableComponent, TranslatableComponent.Builder>, ScopedComponent<TranslatableComponent> {
  /**
   * Gets the translation key.
   *
   * @return the translation key
   * @since 4.0.0
   */
  @NotNull String key();

  /**
   * Sets the translation key.
   *
   * @param translatable the translatable object to get the key from
   * @return a translatable component
   * @since 4.8.0
   */
  @Contract(pure = true)
  default @NotNull TranslatableComponent key(final @NotNull Translatable translatable) {
    return this.key(Objects.requireNonNull(translatable, "translatable").translationKey());
  }

  /**
   * Sets the translation key.
   *
   * @param key the translation key
   * @return a translatable component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull TranslatableComponent key(final @NotNull String key);

  /**
   * Gets the unmodifiable list of translation arguments.
   *
   * @return the unmodifiable list of translation arguments
   * @since 4.0.0
   */
  @NotNull List<Component> args();

  /**
   * Sets the translation arguments for this component.
   *
   * @param args the translation arguments
   * @return a translatable component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull TranslatableComponent args(final @NotNull ComponentLike@NotNull... args);

  /**
   * Sets the translation arguments for this component.
   *
   * @param args the translation arguments
   * @return a translatable component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull TranslatableComponent args(final @NotNull List<? extends ComponentLike> args);

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
      Stream.of(
        ExaminableProperty.of("key", this.key()),
        ExaminableProperty.of("args", this.args())
      ),
      BuildableComponent.super.examinableProperties()
    );
  }

  /**
   * A text component builder.
   *
   * @since 4.0.0
   */
  interface Builder extends ComponentBuilder<TranslatableComponent, Builder> {
    /**
     * Sets the translation key.
     *
     * @param translatable the translatable object to get the key from
     * @return this builder
     * @since 4.8.0
     */
    @Contract(pure = true)
    default @NotNull Builder key(final @NotNull Translatable translatable) {
      return this.key(Objects.requireNonNull(translatable, "translatable").translationKey());
    }

    /**
     * Sets the translation key.
     *
     * @param key the translation key
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder key(final @NotNull String key);

    /**
     * Sets the translation args.
     *
     * @param arg the translation arg
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder args(final @NotNull ComponentBuilder<?, ?> arg);

    /**
     * Sets the translation args.
     *
     * @param args the translation args
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @SuppressWarnings("checkstyle:GenericWhitespace")
    @NotNull Builder args(final @NotNull ComponentBuilder<?, ?>@NotNull... args);

    /**
     * Sets the translation args.
     *
     * @param arg the translation arg
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder args(final @NotNull Component arg);

    /**
     * Sets the translation args.
     *
     * @param args the translation args
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder args(final @NotNull ComponentLike@NotNull... args);

    /**
     * Sets the translation args.
     *
     * @param args the translation args
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder args(final @NotNull List<? extends ComponentLike> args);
  }
}

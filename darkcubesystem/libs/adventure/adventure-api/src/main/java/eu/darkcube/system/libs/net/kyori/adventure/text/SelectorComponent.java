/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * A component that can display the name of entities found with a given selector.
 *
 * <p>This component consists of:</p>
 * <dl>
 *   <dt>selector</dt>
 *   <dd>a Minecraft selector.(e.g {@code @p}, {@code @a})</dd>
 * </dl>
 *
 * <p>This component is rendered serverside and can therefore receive platform-defined
 * context. See the documentation for your respective
 * platform for more info</p>
 *
 * @since 4.0.0
 */
public interface SelectorComponent extends BuildableComponent<SelectorComponent, SelectorComponent.Builder>, ScopedComponent<SelectorComponent> {
  /**
   * Gets the selector pattern.
   *
   * @return the selector pattern
   * @since 4.0.0
   */
  @NotNull String pattern();

  /**
   * Sets the selector pattern.
   *
   * @param pattern the selector pattern
   * @return a selector component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull SelectorComponent pattern(final @NotNull String pattern);

  /**
   * Gets the separator.
   *
   * @return the separator
   * @since 4.8.0
   */
  @Nullable Component separator();

  /**
   * Sets the separator.
   *
   * @param separator the separator
   * @return the separator
   * @since 4.8.0
   */
  @NotNull SelectorComponent separator(final @Nullable ComponentLike separator);

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
      Stream.of(
        ExaminableProperty.of("pattern", this.pattern()),
        ExaminableProperty.of("separator", this.separator())
      ),
      BuildableComponent.super.examinableProperties()
    );
  }

  /**
   * A selector component builder.
   *
   * @since 4.0.0
   */
  interface Builder extends ComponentBuilder<SelectorComponent, Builder> {
    /**
     * Sets the selector pattern.
     *
     * @param pattern the selector pattern
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder pattern(final @NotNull String pattern);

    /**
     * Sets the separator.
     *
     * @param separator the separator
     * @return this builder
     * @since 4.8.0
     */
    @Contract("_ -> this")
    @NotNull Builder separator(final @Nullable ComponentLike separator);
  }
}

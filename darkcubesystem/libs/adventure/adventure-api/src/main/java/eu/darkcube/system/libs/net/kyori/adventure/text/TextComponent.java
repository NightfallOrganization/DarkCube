/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A component that displays a string.
 *
 * <p>This component consists of:</p>
 * <dl>
 *   <dt>content</dt>
 *   <dd>string to be displayed</dd>
 * </dl>
 *
 * @since 4.0.0
 */
public interface TextComponent extends BuildableComponent<TextComponent, TextComponent.Builder>, ScopedComponent<TextComponent> {
  /**
   * Creates a component with {@code components} as the children.
   *
   * @param components the children
   * @return a text component
   * @since 4.0.0
   * @deprecated for removal since 4.9.0, use {@link Component#textOfChildren(ComponentLike...)} instead
   */
  @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
  @Deprecated
  static @NotNull TextComponent ofChildren(final @NotNull ComponentLike@NotNull... components) {
    return Component.textOfChildren(components);
  }

  /**
   * Gets the plain text content.
   *
   * @return the plain text content
   * @since 4.0.0
   */
  @NotNull String content();

  /**
   * Sets the plain text content.
   *
   * @param content the plain text content
   * @return a copy of this component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull TextComponent content(final @NotNull String content);

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
      Stream.of(
        ExaminableProperty.of("content", this.content())
      ),
      BuildableComponent.super.examinableProperties()
    );
  }

  /**
   * A text component builder.
   *
   * @since 4.0.0
   */
  interface Builder extends ComponentBuilder<TextComponent, Builder> {
    /**
     * Gets the plain text content.
     *
     * @return the plain text content
     * @since 4.0.0
     */
    @NotNull String content();

    /**
     * Sets the plain text content.
     *
     * @param content the plain text content
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder content(final @NotNull String content);
  }
}

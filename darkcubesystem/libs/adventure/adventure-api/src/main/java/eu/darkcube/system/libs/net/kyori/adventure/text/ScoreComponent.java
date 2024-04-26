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
 * A component that can display a player's score from a scoreboard objective,
 * with an optional fallback value if the search for the score fails.
 *
 * <p>This component consists of:</p>
 * <dl>
 *   <dt>name</dt>
 *   <dd>a player username or a Minecraft selector that leads to a single player</dd>
 *   <dt>objective</dt>
 *   <dd>a scoreboard objective</dd>
 *   <dt>value(optional)</dt>
 *   <dd>a value to use that will override any queried scoreboard value
 *   <p>This field is no longer present in the game from 1.16,
 *   which means it will be ignored</p></dd>
 * </dl>
 *
 * <p>This component is rendered serverside and can therefore receive platform-defined
 * context. See the documentation for your respective
 * platform for more info</p>
 *
 * @since 4.0.0
 */
public interface ScoreComponent extends BuildableComponent<ScoreComponent, ScoreComponent.Builder>, ScopedComponent<ScoreComponent> {
  /**
   * Gets the score name.
   *
   * @return the score name
   * @since 4.0.0
   */
  @NotNull String name();

  /**
   * Sets the score name.
   *
   * @param name the score name
   * @return a score component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull ScoreComponent name(final @NotNull String name);

  /**
   * Gets the objective name.
   *
   * @return the objective name
   * @since 4.0.0
   */
  @NotNull String objective();

  /**
   * Sets the score objective.
   *
   * @param objective the score objective
   * @return a score component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull ScoreComponent objective(final @NotNull String objective);

  /**
   * Gets the value.
   *
   * @return the value
   * @since 4.0.0
   * @deprecated since 4.7.0, not for removal, with no replacement. This field is no longer supported in 1.16.5.
   */
  @Deprecated
  @Nullable String value();

  /**
   * Sets the value.
   *
   * @param value the value
   * @return a score component
   * @since 4.0.0
   * @deprecated since 4.7.0, not for removal, with no replacement. This field is no longer supported in 1.16.5.
   */
  @Deprecated
  @Contract(pure = true)
  @NotNull ScoreComponent value(final @Nullable String value);

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
      Stream.of(
        ExaminableProperty.of("name", this.name()),
        ExaminableProperty.of("objective", this.objective()),
        ExaminableProperty.of("value", this.value())
      ),
      BuildableComponent.super.examinableProperties()
    );
  }

  /**
   * A score component builder.
   *
   * @since 4.0.0
   */
  interface Builder extends ComponentBuilder<ScoreComponent, Builder> {
    /**
     * Sets the score name.
     *
     * @param name the score name
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder name(final @NotNull String name);

    /**
     * Sets the score objective.
     *
     * @param objective the score objective
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder objective(final @NotNull String objective);

    /**
     * Sets the value.
     *
     * @param value the value
     * @return this builder
     * @since 4.0.0
     * @deprecated since 4.7.0, not for removal, with no replacement. This field is no longer supported in 1.16.5.
     */
    @Deprecated
    @Contract("_ -> this")
    @NotNull Builder value(final @Nullable String value);
  }
}

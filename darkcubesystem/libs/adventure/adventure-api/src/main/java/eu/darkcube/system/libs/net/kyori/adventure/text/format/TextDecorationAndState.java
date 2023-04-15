/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.format;

import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A combination of a {@link TextDecoration} and a {@link TextDecoration.State}.
 *
 * @since 4.8.0
 */
@ApiStatus.NonExtendable
public interface TextDecorationAndState extends Examinable, StyleBuilderApplicable {
  /**
   * Gets the decoration.
   *
   * @return the decoration
   * @since 4.8.0
   */
  @NotNull TextDecoration decoration();

  /**
   * Gets the state.
   *
   * @return the state
   * @since 4.8.0
   */
  TextDecoration.@NotNull State state();

  @Override
  default void styleApply(final Style.@NotNull Builder style) {
    style.decoration(this.decoration(), this.state());
  }

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("decoration", this.decoration()),
      ExaminableProperty.of("state", this.state())
    );
  }
}

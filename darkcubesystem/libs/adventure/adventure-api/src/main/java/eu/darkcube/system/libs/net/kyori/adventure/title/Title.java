/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.title;

import java.time.Duration;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.util.Ticks;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;

/**
 * Represents an in-game title, which can be displayed across the centre of the screen.
 *
 * @see Times
 * @since 4.0.0
 */
@ApiStatus.NonExtendable
public interface Title extends Examinable {
  /**
   * The default times.
   *
   * @since 4.0.0
   */
  Times DEFAULT_TIMES = Times.times(Ticks.duration(10), Ticks.duration(70), Ticks.duration(20));

  /**
   * Creates a title.
   *
   * @param title the title
   * @param subtitle the subtitle
   * @return the title
   * @since 4.0.0
   */
  static @NotNull Title title(final @NotNull Component title, final @NotNull Component subtitle) {
    return title(title, subtitle, DEFAULT_TIMES);
  }

  /**
   * Creates a title.
   *
   * @param title the title
   * @param subtitle the subtitle
   * @param times the times
   * @return the title
   * @since 4.0.0
   */
  static @NotNull Title title(final @NotNull Component title, final @NotNull Component subtitle, final @Nullable Times times) {
    return new TitleImpl(title, subtitle, times);
  }

  /**
   * Gets the title.
   *
   * @return the title
   * @since 4.0.0
   */
  @NotNull Component title();

  /**
   * Gets the subtitle.
   *
   * @return the subtitle
   * @since 4.0.0
   */
  @NotNull Component subtitle();

  /**
   * Gets the times.
   *
   * @return the times
   * @since 4.0.0
   */
  @Nullable Times times();

  /**
   * Gets a part.
   *
   * @param part the part
   * @param <T> the type of the part
   * @return the value
   * @since 4.9.0
   */
  <T> @UnknownNullability T part(final @NotNull TitlePart<T> part);

  /**
   * Title times.
   *
   * @since 4.0.0
   */
  interface Times extends Examinable {
    /**
     * Creates times.
     *
     * @param fadeIn the fade-in time
     * @param stay the stay time
     * @param fadeOut the fade-out time
     * @return times
     * @since 4.0.0
     * @deprecated for removal since 4.10.0, use {@link #times()}
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
    @Deprecated
    static @NotNull Times of(final @NotNull Duration fadeIn, final @NotNull Duration stay, final @NotNull Duration fadeOut) {
      return times(fadeIn, stay, fadeOut);
    }

    /**
     * Creates times.
     *
     * @param fadeIn the fade-in time
     * @param stay the stay time
     * @param fadeOut the fade-out time
     * @return times
     * @since 4.10.0
     */
    static @NotNull Times times(final @NotNull Duration fadeIn, final @NotNull Duration stay, final @NotNull Duration fadeOut) {
      return new TitleImpl.TimesImpl(fadeIn, stay, fadeOut);
    }

    /**
     * Gets the time the title will fade-in.
     *
     * @return the time the title will fade-in
     * @since 4.0.0
     */
    @NotNull Duration fadeIn();

    /**
     * Gets the time the title will stay.
     *
     * @return the time the title will stay
     * @since 4.0.0
     */
    @NotNull Duration stay();

    /**
     * Gets the time the title will fade-out.
     *
     * @return the time the title will fade-out
     * @since 4.0.0
     */
    @NotNull Duration fadeOut();
  }
}

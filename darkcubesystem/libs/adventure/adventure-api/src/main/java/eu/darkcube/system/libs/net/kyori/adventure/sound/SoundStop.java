/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.sound;

import java.util.function.Supplier;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * A sound and/or a sound source, used for stopping in-game sounds that
 * are being played on a game client matching the given sound and/or sound source.
 *
 * <p>For clarification: a {@link SoundStop} consisting of the sound "ambient.weather.rain" and the source {@link Sound.Source#AMBIENT}
 * will only stop sounds matching BOTH parameters and not sounds matching only the sound or only the source.</p>
 *
 *
 * @see Audience#stopSound(SoundStop)
 * @since 4.0.0
 */
@ApiStatus.NonExtendable
public interface SoundStop extends Examinable {
  /**
   * Stops all sounds.
   *
   * @return a sound stopper
   * @since 4.0.0
   */
  static @NotNull SoundStop all() {
    return SoundStopImpl.ALL;
  }

  /**
   * Stops all sounds named {@code sound}.
   *
   * @param sound the sound
   * @return a sound stopper
   * @since 4.0.0
   */
  static @NotNull SoundStop named(final @NotNull Key sound) {
    requireNonNull(sound, "sound");
    return new SoundStopImpl(null) {
      @Override
      public @NotNull Key sound() {
        return sound;
      }
    };
  }

  /**
   * Stops all sounds named {@code sound}.
   *
   * @param sound the sound
   * @return a sound stopper
   * @since 4.0.0
   */
  static @NotNull SoundStop named(final Sound.@NotNull Type sound) {
    requireNonNull(sound, "sound");
    return new SoundStopImpl(null) {
      @Override
      public @NotNull Key sound() {
        return sound.key();
      }
    };
  }

  /**
   * Stops all sounds named {@code sound}.
   *
   * @param sound the sound
   * @return a sound stopper
   * @since 4.0.0
   */
  static @NotNull SoundStop named(final @NotNull Supplier<? extends Sound.Type> sound) {
    requireNonNull(sound, "sound");
    return new SoundStopImpl(null) {
      @Override
      public @NotNull Key sound() {
        return sound.get().key();
      }
    };
  }

  /**
   * Stops all sounds on source {@code source}.
   *
   * @param source the source
   * @return a sound stopper
   * @since 4.0.0
   */
  static @NotNull SoundStop source(final Sound.@NotNull Source source) {
    requireNonNull(source, "source");
    return new SoundStopImpl(source) {
      @Override
      public @Nullable Key sound() {
        return null;
      }
    };
  }

  /**
   * Stops all sounds named {@code name} on source {@code source}.
   *
   * @param sound the sound
   * @param source the source
   * @return a sound stopper
   * @since 4.0.0
   */
  static @NotNull SoundStop namedOnSource(final @NotNull Key sound, final Sound.@NotNull Source source) {
    requireNonNull(sound, "sound");
    requireNonNull(source, "source");
    return new SoundStopImpl(source) {
      @Override
      public @NotNull Key sound() {
        return sound;
      }
    };
  }

  /**
   * Stops all sounds named {@code name} on source {@code source}.
   *
   * @param sound the sound
   * @param source the source
   * @return a sound stopper
   * @since 4.0.0
   */
  static @NotNull SoundStop namedOnSource(final Sound.@NotNull Type sound, final Sound.@NotNull Source source) {
    requireNonNull(sound, "sound");
    return namedOnSource(sound.key(), source);
  }

  /**
   * Stops all sounds named {@code name} on source {@code source}.
   *
   * @param sound the sound
   * @param source the source
   * @return a sound stopper
   * @since 4.0.0
   */
  static @NotNull SoundStop namedOnSource(final @NotNull Supplier<? extends Sound.Type> sound, final Sound.@NotNull Source source) {
    requireNonNull(sound, "sound");
    requireNonNull(source, "source");
    return new SoundStopImpl(source) {
      @Override
      public @NotNull Key sound() {
        return sound.get().key();
      }
    };
  }

  /**
   * Gets the sound.
   *
   * @return the sound
   * @since 4.0.0
   */
  @Nullable Key sound();

  /**
   * Gets the source.
   *
   * @return the source
   * @since 4.0.0
   */
  Sound.@Nullable Source source();
}

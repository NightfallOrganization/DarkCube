/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.sound;

import java.util.Objects;
import java.util.stream.Stream;

import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

abstract class SoundStopImpl implements SoundStop {
  static final SoundStop ALL = new SoundStopImpl(null) {
    @Override
    public @Nullable Key sound() {
      return null; // all
    }
  };
  private final Sound.@Nullable Source source;

  SoundStopImpl(final Sound.@Nullable Source source) {
    this.source = source;
  }

  @Override
  public Sound.@Nullable Source source() {
    return this.source;
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof SoundStopImpl)) return false;
    final SoundStopImpl that = (SoundStopImpl) other;
    return Objects.equals(this.sound(), that.sound())
      && Objects.equals(this.source, that.source);
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(this.sound());
    result = (31 * result) + Objects.hashCode(this.source);
    return result;
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("name", this.sound()),
      ExaminableProperty.of("source", this.source)
    );
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }
}

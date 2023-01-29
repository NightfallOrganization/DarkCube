/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.format;

import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

final class TextDecorationAndStateImpl implements TextDecorationAndState {
  private final TextDecoration decoration;
  private final TextDecoration.State state;

  TextDecorationAndStateImpl(final TextDecoration decoration, final TextDecoration.State state) {
    // no null check is required on the decoration since this constructor is always invoked in such a way that
    // decoration is always non-null
    this.decoration = decoration;
    this.state = requireNonNull(state, "state");
  }

  @Override
  public @NotNull TextDecoration decoration() {
    return this.decoration;
  }

  @Override
  public TextDecoration.@NotNull State state() {
    return this.state;
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || this.getClass() != other.getClass()) return false;
    final TextDecorationAndStateImpl that = (TextDecorationAndStateImpl) other;
    return this.decoration == that.decoration && this.state == that.state;
  }

  @Override
  public int hashCode() {
    int result = this.decoration.hashCode();
    result = (31 * result) + this.state.hashCode();
    return result;
  }
}

/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.List;
import java.util.Objects;

import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

abstract class NBTComponentImpl<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> extends AbstractComponent implements NBTComponent<C, B> {
  static final boolean INTERPRET_DEFAULT = false;
  final String nbtPath;
  final boolean interpret;
  final @Nullable Component separator;

  NBTComponentImpl(final @NotNull List<Component> children, final @NotNull Style style, final String nbtPath, final boolean interpret, final @Nullable Component separator) {
    super(children, style);
    this.nbtPath = nbtPath;
    this.interpret = interpret;
    this.separator = separator;
  }

  @Override
  public @NotNull String nbtPath() {
    return this.nbtPath;
  }

  @Override
  public boolean interpret() {
    return this.interpret;
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof NBTComponent)) return false;
    if (!super.equals(other)) return false;
    final NBTComponent<?, ?> that = (NBTComponent<?, ?>) other;
    return Objects.equals(this.nbtPath, that.nbtPath()) && this.interpret == that.interpret() && Objects.equals(this.separator, that.separator());
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (31 * result) + this.nbtPath.hashCode();
    result = (31 * result) + Boolean.hashCode(this.interpret);
    result = (31 * result) + Objects.hashCode(this.separator);
    return result;
  }
}

/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

abstract class AbstractNBTComponentBuilder<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> extends AbstractComponentBuilder<C, B> implements NBTComponentBuilder<C, B> {
  protected @Nullable String nbtPath;
  protected boolean interpret = NBTComponentImpl.INTERPRET_DEFAULT;
  protected @Nullable Component separator;

  AbstractNBTComponentBuilder() {
  }

  AbstractNBTComponentBuilder(final @NotNull C component) {
    super(component);
    this.nbtPath = component.nbtPath();
    this.interpret = component.interpret();
    this.separator = component.separator();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull B nbtPath(final @NotNull String nbtPath) {
    this.nbtPath = requireNonNull(nbtPath, "nbtPath");
    return (B) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull B interpret(final boolean interpret) {
    this.interpret = interpret;
    return (B) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull B separator(final @Nullable ComponentLike separator) {
    this.separator = ComponentLike.unbox(separator);
    return (B) this;
  }
}

/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.net.kyori.examination.string.StringExaminer;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.Debug;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * An abstract implementation of a text component.
 *
 * @since 4.0.0
 * @deprecated for removal since 4.10.0
 */
@ApiStatus.ScheduledForRemoval(inVersion = "5.0.0")
@Debug.Renderer(text = "this.debuggerString()", childrenArray = "this.children().toArray()", hasChildren = "!this.children().isEmpty()")
@Deprecated
public abstract class AbstractComponent implements Component {
  protected final List<Component> children;
  protected final Style style;

  protected AbstractComponent(final @NotNull List<? extends ComponentLike> children, final @NotNull Style style) {
    this.children = ComponentLike.asComponents(children, IS_NOT_EMPTY);
    this.style = style;
  }

  @Override
  public final @NotNull List<Component> children() {
    return this.children;
  }

  @Override
  public final @NotNull Style style() {
    return this.style;
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (!(other instanceof AbstractComponent)) return false;
    final AbstractComponent that = (AbstractComponent) other;
    return Objects.equals(this.children, that.children)
      && Objects.equals(this.style, that.style);
  }

  @Override
  public int hashCode() {
    int result = this.children.hashCode();
    result = (31 * result) + this.style.hashCode();
    return result;
  }

  @Override
  public abstract String toString();

  @SuppressWarnings("unused")
  private String debuggerString() {
    final Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren = this.examinableProperties()
      .filter(property -> !property.name().equals(ComponentInternals.CHILDREN_PROPERTY));
    return StringExaminer.simpleEscaping().examine(this.examinableName(), examinablePropertiesWithoutChildren);
  }
}

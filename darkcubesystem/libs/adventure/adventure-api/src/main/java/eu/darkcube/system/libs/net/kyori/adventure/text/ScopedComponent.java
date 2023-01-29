/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEventSource;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * Some magic to change return types.
 *
 * @param <C> the component type
 * @since 4.0.0
 */
public interface ScopedComponent<C extends Component> extends Component {
  @Override
  @NotNull C children(final @NotNull List<? extends ComponentLike> children);

  @Override
  @NotNull C style(final @NotNull Style style);

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C style(final @NotNull Consumer<Style.Builder> style) {
    return (C) Component.super.style(style);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C style(final Style.@NotNull Builder style) {
    return (C) Component.super.style(style);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C mergeStyle(final @NotNull Component that) {
    return (C) Component.super.mergeStyle(that);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C mergeStyle(final @NotNull Component that, final Style.@NotNull Merge@NotNull... merges) {
    return (C) Component.super.mergeStyle(that, merges);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C append(final @NotNull Component component) {
    return (C) Component.super.append(component);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C append(final @NotNull ComponentLike like) {
    return (C) Component.super.append(like);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C append(final @NotNull ComponentBuilder<?, ?> builder) {
    return (C) Component.super.append(builder);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C mergeStyle(final @NotNull Component that, final @NotNull Set<Style.Merge> merges) {
    return (C) Component.super.mergeStyle(that, merges);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C color(final @Nullable TextColor color) {
    return (C) Component.super.color(color);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C colorIfAbsent(final @Nullable TextColor color) {
    return (C) Component.super.colorIfAbsent(color);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C decorate(final @NotNull TextDecoration decoration) {
    return (C) Component.super.decorate(decoration);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C decoration(final @NotNull TextDecoration decoration, final boolean flag) {
    return (C) Component.super.decoration(decoration, flag);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C decoration(final @NotNull TextDecoration decoration, final TextDecoration.@NotNull State state) {
    return (C) Component.super.decoration(decoration, state);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C clickEvent(final @Nullable ClickEvent event) {
    return (C) Component.super.clickEvent(event);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C hoverEvent(final @Nullable HoverEventSource<?> event) {
    return (C) Component.super.hoverEvent(event);
  }

  @Override
  @SuppressWarnings("unchecked")
  default @NotNull C insertion(final @Nullable String insertion) {
    return (C) Component.super.insertion(insertion);
  }
}

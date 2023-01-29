/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.renderer;

import eu.darkcube.system.libs.net.kyori.adventure.text.BlockNBTComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.EntityNBTComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.KeybindComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.NBTComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.ScoreComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.SelectorComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.StorageNBTComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.TextComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.TranslatableComponent;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * An abstract implementation of a component renderer.
 *
 * @param <C> the context type
 * @since 4.0.0
 */
public abstract class AbstractComponentRenderer<C> implements ComponentRenderer<C> {
  @Override
  public @NotNull Component render(final @NotNull Component component, final @NotNull C context) {
    if (component instanceof TextComponent) {
      return this.renderText((TextComponent) component, context);
    } else if (component instanceof TranslatableComponent) {
      return this.renderTranslatable((TranslatableComponent) component, context);
    } else if (component instanceof KeybindComponent) {
      return this.renderKeybind((KeybindComponent) component, context);
    } else if (component instanceof ScoreComponent) {
      return this.renderScore((ScoreComponent) component, context);
    } else if (component instanceof SelectorComponent) {
      return this.renderSelector((SelectorComponent) component, context);
    } else if (component instanceof NBTComponent<?, ?>) {
      if (component instanceof BlockNBTComponent) {
        return this.renderBlockNbt((BlockNBTComponent) component, context);
      } else if (component instanceof EntityNBTComponent) {
        return this.renderEntityNbt((EntityNBTComponent) component, context);
      } else if (component instanceof StorageNBTComponent) {
        return this.renderStorageNbt((StorageNBTComponent) component, context);
      }
    }
    return component;
  }

  /**
   * Renders a block NBT component.
   *
   * @param component the component
   * @param context the context
   * @return the rendered component
   */
  protected abstract @NotNull Component renderBlockNbt(final @NotNull BlockNBTComponent component, final @NotNull C context);

  /**
   * Renders an entity NBT component.
   *
   * @param component the component
   * @param context the context
   * @return the rendered component
   */
  protected abstract @NotNull Component renderEntityNbt(final @NotNull EntityNBTComponent component, final @NotNull C context);

  /**
   * Renders a storage NBT component.
   *
   * @param component the component
   * @param context the context
   * @return the rendered component
   */
  protected abstract @NotNull Component renderStorageNbt(final @NotNull StorageNBTComponent component, final @NotNull C context);

  /**
   * Renders a keybind component.
   *
   * @param component the component
   * @param context the context
   * @return the rendered component
   */
  protected abstract @NotNull Component renderKeybind(final @NotNull KeybindComponent component, final @NotNull C context);

  /**
   * Renders a score component.
   *
   * @param component the component
   * @param context the context
   * @return the rendered component
   */
  protected abstract @NotNull Component renderScore(final @NotNull ScoreComponent component, final @NotNull C context);

  /**
   * Renders a selector component.
   *
   * @param component the component
   * @param context the context
   * @return the rendered component
   */
  protected abstract @NotNull Component renderSelector(final @NotNull SelectorComponent component, final @NotNull C context);

  /**
   * Renders a text component.
   *
   * @param component the component
   * @param context the context
   * @return the rendered component
   */
  protected abstract @NotNull Component renderText(final @NotNull TextComponent component, final @NotNull C context);

  /**
   * Renders a translatable component.
   *
   * @param component the component
   * @param context the context
   * @return the rendered component
   */
  protected abstract @NotNull Component renderTranslatable(final @NotNull TranslatableComponent component, final @NotNull C context);
}

/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text;

import java.util.Objects;
import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Contract;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A {@link Component} that displays the client's current keybind for the supplied action.
 *
 * <p>This component takes:</p>
 * <dl>
 *   <dt>keybind</dt>
 *   <dd>a keybind identifier for a action. (e.g key.inventory, key.jump etc..)</dd>
 * </dl>
 *
 * @since 4.0.0
 * @sinceMinecraft 1.12
 */
public interface KeybindComponent extends BuildableComponent<KeybindComponent, KeybindComponent.Builder>, ScopedComponent<KeybindComponent> {
  /**
   * Gets the keybind.
   *
   * @return the keybind
   * @since 4.0.0
   */
  @NotNull String keybind();

  /**
   * Sets the keybind.
   *
   * @param keybind the keybind
   * @return a copy of this component
   * @since 4.0.0
   */
  @Contract(pure = true)
  @NotNull KeybindComponent keybind(final @NotNull String keybind);

  /**
   * Sets the keybind.
   *
   * @param keybind the keybind
   * @return a copy of this component
   * @since 4.9.0
   */
  @Contract(pure = true)
  default @NotNull KeybindComponent keybind(final @NotNull KeybindLike keybind) {
    return this.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind());
  }

  @Override
  default @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
      Stream.of(
        ExaminableProperty.of("keybind", this.keybind())
      ),
      BuildableComponent.super.examinableProperties()
    );
  }

  /**
   * Something that can provide a keybind identifier.
   *
   * @since 4.9.0
   */
  interface KeybindLike {
    /**
     * Gets the keybind identifier.
     *
     * @return the keybind identifier
     * @since 4.9.0
     */
    @NotNull String asKeybind();
  }

  /**
   * A keybind component builder.
   *
   * @since 4.0.0
   */
  interface Builder extends ComponentBuilder<KeybindComponent, Builder> {
    /**
     * Sets the keybind.
     *
     * @param keybind the keybind
     * @return this builder
     * @since 4.0.0
     */
    @Contract("_ -> this")
    @NotNull Builder keybind(final @NotNull String keybind);

    /**
     * Sets the keybind.
     *
     * @param keybind the keybind
     * @return this builder
     * @since 4.9.0
     */
    @Contract(pure = true)
    default @NotNull Builder keybind(final @NotNull KeybindLike keybind) {
      return this.keybind(Objects.requireNonNull(keybind, "keybind").asKeybind());
    }
  }
}
/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.bossbar;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * {@link BossBar} internal implementation.
 *
 * @since 4.12.0
 */
@ApiStatus.Internal
public interface BossBarImplementation {
  /**
   * Gets an implementation, and casts it to {@code type}.
   *
   * @param bar the bossbar
   * @param type the implementation type
   * @param <I> the implementation type
   * @return a {@code I}
   * @since 4.12.0
   */
  @ApiStatus.Internal
  static <I extends BossBarImplementation> @NotNull I get(final @NotNull BossBar bar, final @NotNull Class<I> type) {
    return BossBarImpl.ImplementationAccessor.get(bar, type);
  }

  /**
   * A {@link BossBarImplementation} service provider.
   *
   * @since 4.12.0
   */
  @ApiStatus.Internal
  interface Provider {
    /**
     * Gets an implementation.
     *
     * @param bar the bossbar
     * @return a {@code I}
     * @since 4.12.0
     */
    @ApiStatus.Internal
    @NotNull BossBarImplementation create(final @NotNull BossBar bar);
  }
}

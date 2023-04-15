/*
 * Copyright (c) 2018-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.platform.facet;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * A base implementation of a facet that validates viewer type.
 *
 * <p>This is not supported API. Subject to change at any time.</p>
 *
 * @param <V> the viewer type
 * @since 4.0.0
 */
public abstract class FacetBase<V> implements Facet<V> {
  protected final Class<? extends V> viewerClass;

  protected FacetBase(final @Nullable Class<? extends V> viewerClass) {
    this.viewerClass = viewerClass;
  }

  @Override
  public boolean isSupported() {
    return this.viewerClass != null;
  }

  @Override
  public boolean isApplicable(final @NotNull V viewer) {
    return this.viewerClass != null && this.viewerClass.isInstance(viewer);
  }
}

/*
 * Copyright (c) 2018-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.platform.facet;

import java.util.Set;
import java.util.function.Function;
import eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

class FacetBossBarListener<V> implements Facet.BossBar<V> {
  private final Facet.BossBar<V> facet;
  private final Function<Component, Component> translator;

  FacetBossBarListener(final Facet.@NotNull BossBar<V> facet, final @NotNull Function<Component, Component> translator) {
    this.facet = facet;
    this.translator = translator;
  }

  @Override
  public void bossBarInitialized(final @NotNull BossBar bar) {
    this.facet.bossBarInitialized(bar);
    this.bossBarNameChanged(bar, bar.name(), bar.name()); // Redo name change with translation
  }

  @Override
  public void bossBarNameChanged(final @NotNull BossBar bar, final @NotNull Component oldName, final @NotNull Component newName) {
    this.facet.bossBarNameChanged(bar, oldName, this.translator.apply(newName));
  }

  @Override
  public void bossBarProgressChanged(final @NotNull BossBar bar, final float oldPercent, final float newPercent) {
    this.facet.bossBarProgressChanged(bar, oldPercent, newPercent);
  }

  @Override
  public void bossBarColorChanged(final @NotNull BossBar bar, final BossBar.@NotNull Color oldColor, final BossBar.@NotNull Color newColor) {
    this.facet.bossBarColorChanged(bar, oldColor, newColor);
  }

  @Override
  public void bossBarOverlayChanged(final @NotNull BossBar bar, final BossBar.@NotNull Overlay oldOverlay, final BossBar.@NotNull Overlay newOverlay) {
    this.facet.bossBarOverlayChanged(bar, oldOverlay, newOverlay);
  }

  @Override
  public void bossBarFlagsChanged(final @NotNull BossBar bar, final @NotNull Set<BossBar.Flag> flagsAdded, final @NotNull Set<BossBar.Flag> flagsRemoved) {
    this.facet.bossBarFlagsChanged(bar, flagsAdded, flagsRemoved);
  }

  @Override
  public void addViewer(final @NotNull V viewer) {
    this.facet.addViewer(viewer);
  }

  @Override
  public void removeViewer(final @NotNull V viewer) {
    this.facet.removeViewer(viewer);
  }

  @Override
  public boolean isEmpty() {
    return this.facet.isEmpty();
  }

  @Override
  public void close() {
    this.facet.close();
  }
}

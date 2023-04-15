/*
 * Copyright (c) 2018-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit;

import com.viaversion.viaversion.api.connection.UserConnection;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Function;
import eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar;
import eu.darkcube.system.libs.net.kyori.adventure.identity.Identity;
import eu.darkcube.system.libs.net.kyori.adventure.platform.facet.Facet;
import eu.darkcube.system.libs.net.kyori.adventure.platform.facet.FacetAudience;
import eu.darkcube.system.libs.net.kyori.adventure.platform.facet.FacetAudienceProvider;
import eu.darkcube.system.libs.net.kyori.adventure.platform.viaversion.ViaFacet;
import eu.darkcube.system.libs.net.kyori.adventure.pointer.Pointers;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

@SuppressWarnings("Convert2MethodRef")
final class BukkitAudience extends FacetAudience<CommandSender> {
  static final ThreadLocal<Plugin> PLUGIN = new ThreadLocal<>();
  private static final Function<Player, UserConnection> VIA = new BukkitFacet.ViaHook();
  private static final Collection<Facet.Chat<? extends CommandSender, ?>> CHAT = Facet.of(
    () -> new ViaFacet.Chat<>(Player.class, VIA),
    //    () -> new SpigotFacet.ChatWithType(),
    //    () -> new SpigotFacet.Chat(),
    () -> new CraftBukkitFacet.Chat1_19_3(),
    () -> new CraftBukkitFacet.Chat(),
    () -> new BukkitFacet.Chat());
  private static final Collection<Facet.ActionBar<Player, ?>> ACTION_BAR = Facet.of(
    () -> new ViaFacet.ActionBarTitle<>(Player.class, VIA),
    () -> new ViaFacet.ActionBar<>(Player.class, VIA),
    //    () -> new SpigotFacet.ActionBar(),
    () -> new CraftBukkitFacet.ActionBar_1_17(),
    () -> new CraftBukkitFacet.ActionBar(),
    () -> new CraftBukkitFacet.ActionBarLegacy());
  private static final Collection<Facet.Title<Player, ?, ?, ?>> TITLE = Facet.of(
    () -> new ViaFacet.Title<>(Player.class, VIA),
    // () -> new PaperFacet.Title(),
    () -> new CraftBukkitFacet.Title_1_17(),
    () -> new CraftBukkitFacet.Title());
  private static final Collection<Facet.Sound<Player, Vector>> SOUND = Facet.of(
    () -> new BukkitFacet.SoundWithCategory(),
    () -> new BukkitFacet.Sound());
  private static final Collection<Facet.EntitySound<Player, Object>> ENTITY_SOUND = Facet.of(
    () -> new CraftBukkitFacet.EntitySound_1_19_3(),
    () -> new CraftBukkitFacet.EntitySound()
  );
  private static final Collection<Facet.Book<Player, ?, ?>> BOOK = Facet.of(
    //    () -> new SpigotFacet.Book(),
    () -> new CraftBukkitFacet.BookPost1_13(),
    () -> new CraftBukkitFacet.Book1_13(),
    () -> new CraftBukkitFacet.BookPre1_13());
  private static final Collection<Facet.BossBar.Builder<Player, ?>> BOSS_BAR = Facet.of(
    () -> new ViaFacet.BossBar.Builder<>(Player.class, VIA),
    () -> new ViaFacet.BossBar.Builder1_9_To_1_15<>(Player.class, VIA),
    () -> new CraftBukkitFacet.BossBar.Builder(),
    () -> new BukkitFacet.BossBarBuilder(),
    () -> new CraftBukkitFacet.BossBarWither.Builder());
  private static final Collection<Facet.TabList<Player, ?>> TAB_LIST = Facet.of(
    () -> new ViaFacet.TabList<>(Player.class, VIA),
    () -> new PaperFacet.TabList(),
    () -> new CraftBukkitFacet.TabList(),
    () -> new BukkitFacet.TabList()
  );
  private static final Collection<Facet.Pointers<? extends CommandSender>> POINTERS = Facet.of(
    () -> new BukkitFacet.CommandSenderPointers(),
    () -> new BukkitFacet.ConsoleCommandSenderPointers(),
    () -> new BukkitFacet.PlayerPointers()
  );

  private final @NotNull Plugin plugin;
  // Bukkit only provides this as a String
  private @Nullable Locale locale;

  BukkitAudience(final @NotNull Plugin plugin, final FacetAudienceProvider<?, ?> provider, final @NotNull Collection<CommandSender> viewers) {
    super(provider, viewers, CHAT, ACTION_BAR, TITLE, SOUND, ENTITY_SOUND, BOOK, BOSS_BAR, TAB_LIST, POINTERS);
    this.plugin = plugin;
  }

  void locale(final @Nullable Locale locale) {
    final boolean changed = this.locale != (this.locale = locale);
    if (changed) {
      this.refresh();
    }
  }

  @Nullable Locale locale() {
    return this.locale;
  }

  @Override
  protected void contributePointers(final Pointers.Builder builder) {
    builder.withDynamic(Identity.LOCALE, BukkitAudience.this::locale);
  }

  @Override
  public void showBossBar(final @NotNull BossBar bar) {
    // Some boss bar listeners need access to a Plugin to register events.
    // So keep track of the last plugin before each boss bar invocation.
    PLUGIN.set(this.plugin);

    super.showBossBar(bar);

    // Unset plugin after boss bar is created.
    PLUGIN.set(null);
  }
}

/*
 * Copyright (c) 2018-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import java.lang.invoke.MethodHandle;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import eu.darkcube.system.libs.net.kyori.adventure.identity.Identity;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.permission.PermissionChecker;
import eu.darkcube.system.libs.net.kyori.adventure.platform.facet.Facet;
import eu.darkcube.system.libs.net.kyori.adventure.platform.facet.FacetBase;
import eu.darkcube.system.libs.net.kyori.adventure.platform.facet.FacetPointers;
import eu.darkcube.system.libs.net.kyori.adventure.sound.SoundStop;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftReflection.findMethod;
import static eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftReflection.hasMethod;
import static eu.darkcube.system.libs.net.kyori.adventure.platform.facet.Knob.logError;
import static eu.darkcube.system.libs.net.kyori.adventure.platform.facet.Knob.logUnsupported;

class BukkitFacet<V extends CommandSender> extends FacetBase<V> {
  protected BukkitFacet(final @Nullable Class<? extends V> viewerClass) {
    super(viewerClass);
  }

  static class Message<V extends CommandSender> extends BukkitFacet<V> implements Facet.Message<V, String> {
    protected Message(final @Nullable Class<? extends V> viewerClass) {
      super(viewerClass);
    }

    @Override
    public @NotNull String createMessage(final @NotNull V viewer, final @NotNull Component message) {
      return BukkitComponentSerializer.legacy().serialize(message);
    }
  }

  static class Chat extends Message<CommandSender> implements Facet.Chat<CommandSender, String> {
    protected Chat() {
      super(CommandSender.class);
    }

    @Override
    public void sendMessage(final @NotNull CommandSender viewer, final @NotNull Identity source, final @NotNull String message, final @NotNull Object type) {
      viewer.sendMessage(message);
    }
  }

  static class Position extends BukkitFacet<Player> implements Facet.Position<Player, Vector> {
    protected Position() {
      super(Player.class);
    }

    @Override
    public @NotNull Vector createPosition(final @NotNull Player viewer) {
      return viewer.getLocation().toVector();
    }

    @Override
    public @NotNull Vector createPosition(final double x, final double y, final double z) {
      return new Vector(x, y, z);
    }
  }

  static class Sound extends Position implements Facet.Sound<Player, Vector> {
    private static final boolean KEY_SUPPORTED = MinecraftReflection.hasClass("org.bukkit.NamespacedKey"); // Added MC 1.13
    private static final boolean STOP_SUPPORTED = MinecraftReflection.hasMethod(Player.class, "stopSound", String.class); // Added MC 1.9
    private static final MethodHandle STOP_ALL_SUPPORTED = MinecraftReflection.findMethod(Player.class, "stopAllSounds", void.class);

    @Override
    public void playSound(final @NotNull Player viewer, final eu.darkcube.system.libs.net.kyori.adventure.sound.@NotNull Sound sound, final @NotNull Vector vector) {
      final String name = name(sound.name());
      final Location location = vector.toLocation(viewer.getWorld());

      viewer.playSound(location, name, sound.volume(), sound.pitch());
    }

    @Override
    public void stopSound(final @NotNull Player viewer, final @NotNull SoundStop stop) {
      if (STOP_SUPPORTED) {
        final String name = name(stop.sound());
        if (name.isEmpty() && STOP_ALL_SUPPORTED != null) {
          try {
            STOP_ALL_SUPPORTED.invoke(viewer);
          } catch (final Throwable error) {
            logError(error, "Could not invoke stopAllSounds on %s", viewer);
          }
          return;
        }
        viewer.stopSound(name);
      }
    }

    protected static @NotNull String name(final @Nullable Key name) {
      if (name == null) {
        return "";
      }
      if (KEY_SUPPORTED) { // Sound format changed to use identifiers
        return name.asString();
      } else {
        return name.value();
      }
    }
  }

  static class SoundWithCategory extends Sound {
    private static final boolean SUPPORTED = MinecraftReflection.hasMethod(Player.class, "stopSound", String.class, MinecraftReflection.findClass("org.bukkit.SoundCategory")); // Added MC 1.11

    @Override
    public boolean isSupported() {
      return super.isSupported() && SUPPORTED;
    }

    @Override
    public void playSound(final @NotNull Player viewer, final eu.darkcube.system.libs.net.kyori.adventure.sound.@NotNull Sound sound, final @NotNull Vector vector) {
      final SoundCategory category = this.category(sound.source());
      if (category == null) {
        super.playSound(viewer, sound, vector);
      } else {
        final String name = name(sound.name());
        viewer.playSound(vector.toLocation(viewer.getWorld()), name, category, sound.volume(), sound.pitch());
      }
    }

    @Override
    public void stopSound(final @NotNull Player viewer, final @NotNull SoundStop stop) {
      final SoundCategory category = this.category(stop.source());
      if (category == null) {
        super.stopSound(viewer, stop);
      } else {
        final String name = name(stop.sound());
        viewer.stopSound(name, category);
      }
    }

    private @Nullable SoundCategory category(final eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.@Nullable Source source) {
      if (source == null) {
        return null;
      }
      if (source == eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source.MASTER) {
        return SoundCategory.MASTER;
      } else if (source == eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source.MUSIC) {
        return SoundCategory.MUSIC;
      } else if (source == eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source.RECORD) {
        return SoundCategory.RECORDS;
      } else if (source == eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source.WEATHER) {
        return SoundCategory.WEATHER;
      } else if (source == eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source.BLOCK) {
        return SoundCategory.BLOCKS;
      } else if (source == eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source.HOSTILE) {
        return SoundCategory.HOSTILE;
      } else if (source == eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source.NEUTRAL) {
        return SoundCategory.NEUTRAL;
      } else if (source == eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source.PLAYER) {
        return SoundCategory.PLAYERS;
      } else if (source == eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source.AMBIENT) {
        return SoundCategory.AMBIENT;
      } else if (source == eu.darkcube.system.libs.net.kyori.adventure.sound.Sound.Source.VOICE) {
        return SoundCategory.VOICE;
      }
      logUnsupported(this, source);
      return null;
    }
  }

  static class BossBarBuilder extends BukkitFacet<Player> implements Facet.BossBar.Builder<Player, BukkitFacet.BossBar> {
    private static final boolean SUPPORTED = MinecraftReflection.hasClass("org.bukkit.boss.BossBar"); // Added MC 1.9

    protected BossBarBuilder() {
      super(Player.class);
    }

    @Override
    public boolean isSupported() {
      return super.isSupported() && SUPPORTED;
    }

    @Override
    public BukkitFacet.@NotNull BossBar createBossBar(final @NotNull Collection<Player> viewers) {
      return new BukkitFacet.BossBar(viewers);
    }
  }

  static class BossBar extends Message<Player> implements Facet.BossBar<Player> {
    protected final org.bukkit.boss.BossBar bar;

    protected BossBar(final @NotNull Collection<Player> viewers) {
      super(Player.class);
      this.bar = Bukkit.createBossBar("", BarColor.PINK, BarStyle.SOLID);
      this.bar.setVisible(false);
      for (final Player viewer : viewers) {
        this.bar.addPlayer(viewer);
      }
    }

    @Override
    public void bossBarInitialized(final eu.darkcube.system.libs.net.kyori.adventure.bossbar.@NotNull BossBar bar) {
      Facet.BossBar.super.bossBarInitialized(bar);
      this.bar.setVisible(true);
    }

    @Override
    public void bossBarNameChanged(final eu.darkcube.system.libs.net.kyori.adventure.bossbar.@NotNull BossBar bar, final @NotNull Component oldName, final @NotNull Component newName) {
      if (!this.bar.getPlayers().isEmpty()) {
        this.bar.setTitle(this.createMessage(this.bar.getPlayers().get(0), newName));
      }
    }

    @Override
    public void bossBarProgressChanged(final eu.darkcube.system.libs.net.kyori.adventure.bossbar.@NotNull BossBar bar, final float oldPercent, final float newPercent) {
      this.bar.setProgress(newPercent);
    }

    @Override
    public void bossBarColorChanged(final eu.darkcube.system.libs.net.kyori.adventure.bossbar.@NotNull BossBar bar, final eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.@NotNull Color oldColor, final eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.@NotNull Color newColor) {
      final BarColor color = this.color(newColor);
      if (color != null) {
        this.bar.setColor(color);
      }
    }

    private @Nullable BarColor color(final eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.@NotNull Color color) {
      if (color == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Color.PINK) {
        return BarColor.PINK;
      } else if (color == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Color.BLUE) {
        return BarColor.BLUE;
      } else if (color == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Color.RED) {
        return BarColor.RED;
      } else if (color == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Color.GREEN) {
        return BarColor.GREEN;
      } else if (color == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Color.YELLOW) {
        return BarColor.YELLOW;
      } else if (color == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Color.PURPLE) {
        return BarColor.PURPLE;
      } else if (color == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Color.WHITE) {
        return BarColor.WHITE;
      }
      logUnsupported(this, color);
      return null;
    }

    @Override
    public void bossBarOverlayChanged(final eu.darkcube.system.libs.net.kyori.adventure.bossbar.@NotNull BossBar bar, final eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.@NotNull Overlay oldOverlay, final eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.@NotNull Overlay newOverlay) {
      final BarStyle style = this.style(newOverlay);
      if (style != null) {
        this.bar.setStyle(style);
      }
    }

    private @Nullable BarStyle style(final eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.@NotNull Overlay overlay) {
      if (overlay == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS) {
        return BarStyle.SOLID;
      } else if (overlay == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_6) {
        return BarStyle.SEGMENTED_6;
      } else if (overlay == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_10) {
        return BarStyle.SEGMENTED_10;
      } else if (overlay == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_12) {
        return BarStyle.SEGMENTED_12;
      } else if (overlay == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_20) {
        return BarStyle.SEGMENTED_20;
      }
      logUnsupported(this, overlay);
      return null;
    }

    @Override
    public void bossBarFlagsChanged(final eu.darkcube.system.libs.net.kyori.adventure.bossbar.@NotNull BossBar bar, final @NotNull Set<eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Flag> flagsAdded, final @NotNull Set<eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Flag> flagsRemoved) {
      for (final eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Flag removeFlag : flagsRemoved) {
        final BarFlag flag = this.flag(removeFlag);
        if (flag != null) {
          this.bar.removeFlag(flag);
        }
      }
      for (final eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Flag addFlag : flagsAdded) {
        final BarFlag flag = this.flag(addFlag);
        if (flag != null) {
          this.bar.addFlag(flag);
        }
      }
    }

    private @Nullable BarFlag flag(final eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.@NotNull Flag flag) {
      if (flag == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Flag.DARKEN_SCREEN) {
        return BarFlag.DARKEN_SKY;
      } else if (flag == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Flag.PLAY_BOSS_MUSIC) {
        return BarFlag.PLAY_BOSS_MUSIC;
      } else if (flag == eu.darkcube.system.libs.net.kyori.adventure.bossbar.BossBar.Flag.CREATE_WORLD_FOG) {
        return BarFlag.CREATE_FOG;
      }
      logUnsupported(this, flag);
      return null;
    }

    @Override
    public void addViewer(final @NotNull Player viewer) {
      this.bar.addPlayer(viewer);
    }

    @Override
    public void removeViewer(final @NotNull Player viewer) {
      this.bar.removePlayer(viewer);
    }

    @Override
    public boolean isEmpty() {
      return !this.bar.isVisible() || this.bar.getPlayers().isEmpty();
    }

    @Override
    public void close() {
      this.bar.removeAll();
    }
  }

  static final class ViaHook implements Function<Player, UserConnection> {
    @Override
    public UserConnection apply(final @NotNull Player player) {
      return Via.getManager().getConnectionManager().getConnectedClient(player.getUniqueId());
    }
  }

  static final class TabList extends Message<Player> implements Facet.TabList<Player, String> {
    // All methods added at the same time
    private static final boolean SUPPORTED = MinecraftReflection.hasMethod(Player.class, "setPlayerListHeader", String.class);

    TabList() {
      super(Player.class);
    }

    @Override
    public boolean isSupported() {
      return SUPPORTED && super.isSupported();
    }

    @Override
    public void send(final Player viewer, final @Nullable String header, final @Nullable String footer) {
      if (header != null && footer != null) {
        viewer.setPlayerListHeaderFooter(header, footer);
      } else if (header != null) {
        viewer.setPlayerListHeader(header);
      } else if (footer != null) {
        viewer.setPlayerListFooter(footer);
      }
    }
  }

  static final class CommandSenderPointers extends BukkitFacet<CommandSender> implements Facet.Pointers<CommandSender> {

    CommandSenderPointers() {
      super(CommandSender.class);
    }

    @Override
    public void contributePointers(final CommandSender viewer, final eu.darkcube.system.libs.net.kyori.adventure.pointer.Pointers.Builder builder) {
      // Name
      builder.withDynamic(Identity.NAME, viewer::getName);
      // Permission (technically up in Permissible but *shrug*)
      builder.withStatic(PermissionChecker.POINTER, perm -> {
        if (viewer.isPermissionSet(perm)) {
          return viewer.hasPermission(perm) ? TriState.TRUE : TriState.FALSE;
        } else {
          return TriState.NOT_SET;
        }
      });
    }
  }

  static final class ConsoleCommandSenderPointers extends BukkitFacet<ConsoleCommandSender> implements Facet.Pointers<ConsoleCommandSender> {
    ConsoleCommandSenderPointers() {
      super(ConsoleCommandSender.class);
    }

    @Override
    public void contributePointers(final ConsoleCommandSender viewer, final eu.darkcube.system.libs.net.kyori.adventure.pointer.Pointers.Builder builder) {
      builder.withStatic(FacetPointers.TYPE, FacetPointers.Type.CONSOLE);
    }
  }

  static final class PlayerPointers extends BukkitFacet<Player> implements Facet.Pointers<Player> {

    PlayerPointers() {
      super(Player.class);
    }

    @Override
    public void contributePointers(final Player viewer, final eu.darkcube.system.libs.net.kyori.adventure.pointer.Pointers.Builder builder) {
      builder.withDynamic(Identity.UUID, viewer::getUniqueId);
      builder.withDynamic(Identity.DISPLAY_NAME, () -> BukkitComponentSerializer.legacy().deserializeOrNull(viewer.getDisplayName()));
      builder.withStatic(FacetPointers.TYPE, FacetPointers.Type.PLAYER);
      builder.withDynamic(FacetPointers.WORLD, () -> Key.key(viewer.getWorld().getName())); // :(
    }
  }
}

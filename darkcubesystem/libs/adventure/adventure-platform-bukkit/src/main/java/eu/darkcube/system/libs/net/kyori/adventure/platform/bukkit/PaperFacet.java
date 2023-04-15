/*
 * Copyright (c) 2018-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import eu.darkcube.system.libs.net.kyori.adventure.platform.facet.Facet;
import eu.darkcube.system.libs.net.kyori.adventure.platform.facet.FacetBase;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftReflection.findStaticMethod;
import static eu.darkcube.system.libs.net.kyori.adventure.platform.facet.Knob.isEnabled;
import static eu.darkcube.system.libs.net.kyori.adventure.platform.facet.Knob.logError;

class PaperFacet<V extends CommandSender> extends FacetBase<V> {
  private static final boolean SUPPORTED = isEnabled("paper", true);
  static final Class<?> NATIVE_COMPONENT_CLASS = MinecraftReflection.findClass(String.join(".", "net", "kyori", "adventure", "text", "Component"));
  private static final Class<?> NATIVE_GSON_COMPONENT_SERIALIZER_CLASS = MinecraftReflection.findClass(String.join(".", "net", "kyori", "adventure", "text", "serializer", "gson", "GsonComponentSerializer"));
  private static final Class<?> NATIVE_GSON_COMPONENT_SERIALIZER_IMPL_CLASS = MinecraftReflection.findClass(String.join(".", "net", "kyori", "adventure", "text", "serializer", "gson", "GsonComponentSerializerImpl"));
  private static final MethodHandle NATIVE_GSON_COMPONENT_SERIALIZER_GSON_GETTER = MinecraftReflection.findStaticMethod(NATIVE_GSON_COMPONENT_SERIALIZER_CLASS, "gson", NATIVE_GSON_COMPONENT_SERIALIZER_CLASS);
  private static final MethodHandle NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD = findNativeDeserializeMethod();

  private static @Nullable MethodHandle findNativeDeserializeMethod() {
    try {
      final Method method = NATIVE_GSON_COMPONENT_SERIALIZER_IMPL_CLASS.getDeclaredMethod("deserialize", String.class);
      method.setAccessible(true);
      return MinecraftReflection.lookup().unreflect(method);
    } catch (final NoSuchMethodException | IllegalAccessException | NullPointerException e) {
      return null;
    }
  }

  protected PaperFacet(final @Nullable Class<? extends V> viewerClass) {
    super(viewerClass);
  }

  @Override
  public boolean isSupported() {
    return super.isSupported() && SUPPORTED;
  }

  // don't use without resolving parts handling
  static class Title extends SpigotFacet.Message<Player> implements Facet.Title<Player, BaseComponent[], com.destroystokyo.paper.Title.Builder, com.destroystokyo.paper.Title> {
    private static final boolean SUPPORTED = MinecraftReflection.hasClass("com.destroystokyo.paper.Title");

    protected Title() {
      super(Player.class);
    }

    @Override
    public boolean isSupported() {
      return super.isSupported() && SUPPORTED;
    }

    @Override
    public com.destroystokyo.paper.Title.@NotNull Builder createTitleCollection() {
      return com.destroystokyo.paper.Title.builder();
    }

    @Override
    public void contributeTitle(final com.destroystokyo.paper.Title.@NotNull Builder coll, final BaseComponent @NotNull [] title) {
      coll.title(title);
    }

    @Override
    public void contributeSubtitle(final com.destroystokyo.paper.Title.@NotNull Builder coll, final BaseComponent @NotNull [] subtitle) {
      coll.subtitle(subtitle);
    }

    @Override
    public void contributeTimes(final com.destroystokyo.paper.Title.@NotNull Builder coll, final int inTicks, final int stayTicks, final int outTicks) {
      if (inTicks > -1) coll.fadeIn(inTicks);
      if (stayTicks > -1) coll.stay(stayTicks);
      if (outTicks > -1) coll.fadeOut(outTicks);
    }

    @Nullable
    @Override
    public com.destroystokyo.paper.Title completeTitle(final com.destroystokyo.paper.Title.@NotNull Builder coll) {
      return coll.build(); // todo: can't really do parts
    }

    @Override
    public void showTitle(final @NotNull Player viewer, final com.destroystokyo.paper.@NotNull Title title) {
      viewer.sendTitle(title);
    }

    @Override
    public void clearTitle(final @NotNull Player viewer) {
      viewer.hideTitle();
    }

    @Override
    public void resetTitle(final @NotNull Player viewer) {
      viewer.resetTitle();
    }
  }

  static class TabList extends CraftBukkitFacet.TabList {
    private static final boolean SUPPORTED = MinecraftReflection.hasField(CLASS_CRAFT_PLAYER, NATIVE_COMPONENT_CLASS, "playerListHeader") && MinecraftReflection.hasField(CLASS_CRAFT_PLAYER, NATIVE_COMPONENT_CLASS, "playerListFooter");
    private static final MethodHandle NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD_BOUND = createBoundNativeDeserializeMethodHandle();

    private static @Nullable MethodHandle createBoundNativeDeserializeMethodHandle() {
      if (SUPPORTED) {
        try {
          return NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD.bindTo(NATIVE_GSON_COMPONENT_SERIALIZER_GSON_GETTER.invoke());
        } catch (final Throwable throwable) {
          logError(throwable, "Failed to access native GsonComponentSerializer");
          return null;
        }
      }
      return null;
    }

    @Override
    public boolean isSupported() {
      return SUPPORTED && super.isSupported() && CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER != null && CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER != null;
    }

    @Override
    protected Object create117Packet(final Player viewer, final @Nullable Object header, final @Nullable Object footer) throws Throwable {
      final Object packet = CLIENTBOUND_TAB_LIST_PACKET_CTOR.invoke(null, null);
      CLIENTBOUND_TAB_LIST_PACKET_SET_HEADER.invoke(packet, header == null ? this.createMessage(viewer, Component.empty()) : header);
      CLIENTBOUND_TAB_LIST_PACKET_SET_FOOTER.invoke(packet, footer == null ? this.createMessage(viewer, Component.empty()) : footer);
      return packet;
    }

    @Override
    public @Nullable Object createMessage(final @NotNull Player viewer, final @NotNull Component message) {
      try {
        return NATIVE_GSON_COMPONENT_SERIALIZER_DESERIALIZE_METHOD_BOUND.invoke(GsonComponentSerializer.gson().serialize(message));
      } catch (final Throwable throwable) {
        logError(throwable, "Failed to create native Component message");
        return null;
      }
    }
  }
}

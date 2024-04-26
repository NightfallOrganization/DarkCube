/*
 * Copyright (c) 2018-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit;

import java.util.Collection;
import eu.darkcube.system.libs.net.kyori.adventure.platform.facet.Facet;
import eu.darkcube.system.libs.net.kyori.adventure.platform.facet.FacetComponentFlattener;
import eu.darkcube.system.libs.net.kyori.adventure.text.flattener.ComponentFlattener;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.legacyimpl.NBTLegacyHoverEventSerializer;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import static eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.MinecraftReflection.findEnum;

/**
 * A pair of component serializers for {@link org.bukkit.Bukkit}.
 *
 * @since 4.0.0
 */
public final class BukkitComponentSerializer {
  private BukkitComponentSerializer() {
  }

  private static final boolean IS_1_16 = findEnum(Material.class, "NETHERITE_PICKAXE") != null;

  private static final Collection<FacetComponentFlattener.Translator<Server>> TRANSLATORS = Facet.of(
    SpigotFacet.Translator::new,
    CraftBukkitFacet.Translator::new
  );
  private static final LegacyComponentSerializer LEGACY_SERIALIZER;
  private static final GsonComponentSerializer GSON_SERIALIZER;
  static final ComponentFlattener FLATTENER;

  static {
    FLATTENER = FacetComponentFlattener.get(Bukkit.getServer(), TRANSLATORS);
    if (IS_1_16) {
      LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
        .hexColors()
        .useUnusualXRepeatedCharacterHexFormat()
        .flattener(FLATTENER)
        .build();
      GSON_SERIALIZER = GsonComponentSerializer.builder()
        .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
        .build();
    } else {
      LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
        .character(LegacyComponentSerializer.SECTION_CHAR)
        .flattener(FLATTENER)
        .build();
      GSON_SERIALIZER = GsonComponentSerializer.builder()
        .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
        .emitLegacyHoverEvent()
        .downsampleColors()
        .build();
    }
  }

  /**
   * Gets the legacy component serializer.
   *
   * @return a legacy component serializer
   * @since 4.0.0
   */
  public static @NotNull LegacyComponentSerializer legacy() {
    return LEGACY_SERIALIZER;
  }

  /**
   * Gets the gson component serializer.
   *
   * <p>Not available on servers before 1.7.2, will be {@code null}.</p>
   *
   * @return a gson component serializer
   * @since 4.0.0
   */
  public static @NotNull GsonComponentSerializer gson() {
    return GSON_SERIALIZER;
  }
}

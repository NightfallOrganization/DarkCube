/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.impl.version;

import java.util.ServiceLoader;

import dev.derklaro.aerogel.binding.BindingBuilder;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.bukkit.impl.item.BukkitEquipmentSlotProvider;
import eu.darkcube.system.bukkit.impl.item.enchant.BukkitEnchantmentProvider;
import eu.darkcube.system.bukkit.impl.item.firework.BukkitFireworkEffectProvider;
import eu.darkcube.system.bukkit.impl.item.flag.BukkitItemFlagProvider;
import eu.darkcube.system.bukkit.impl.item.material.BukkitMaterialProvider;
import eu.darkcube.system.server.item.EquipmentSlotProvider;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;
import eu.darkcube.system.server.item.firework.FireworkEffectProvider;
import eu.darkcube.system.server.item.flag.ItemFlagProvider;
import eu.darkcube.system.server.item.material.MaterialProvider;
import eu.darkcube.system.version.Version;

public abstract class AbstractVersionHandler implements BukkitVersionHandler {
    public AbstractVersionHandler() {
        var version = ServiceLoader.load(Version.class, getClass().getClassLoader()).findFirst().orElseThrow();
        var ext = InjectionLayer.ext();
        ext.install(BindingBuilder.create().bind(Version.class).toInstance(version));
        ext.install(BindingBuilder.create().bind(ItemFlagProvider.class).toInstance(new BukkitItemFlagProvider()));
        ext.install(BindingBuilder.create().bind(FireworkEffectProvider.class).toInstance(new BukkitFireworkEffectProvider()));
        ext.install(BindingBuilder.create().bind(EnchantmentProvider.class).toInstance(new BukkitEnchantmentProvider()));
        ext.install(BindingBuilder.create().bind(MaterialProvider.class).toInstance(new BukkitMaterialProvider()));
        ext.install(BindingBuilder.create().bind(EquipmentSlotProvider.class).toInstance(new BukkitEquipmentSlotProvider()));
    }
}

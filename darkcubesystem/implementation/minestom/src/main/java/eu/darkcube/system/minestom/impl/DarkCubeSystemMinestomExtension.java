/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.impl;

import dev.derklaro.aerogel.Injector;
import dev.derklaro.aerogel.binding.BindingBuilder;
import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.system.internal.PacketDeclareProtocolVersion;
import eu.darkcube.system.internal.PacketRequestProtocolVersionDeclaration;
import eu.darkcube.system.minestom.impl.adventure.MinestomAdventureSupportImpl;
import eu.darkcube.system.minestom.impl.item.MinestomEquipmentSlotProvider;
import eu.darkcube.system.minestom.impl.item.MinestomItemProvider;
import eu.darkcube.system.minestom.impl.item.attribute.MinestomAttributeModifierProvider;
import eu.darkcube.system.minestom.impl.item.attribute.MinestomAttributeProvider;
import eu.darkcube.system.minestom.impl.item.enchant.MinestomEnchantmentProvider;
import eu.darkcube.system.minestom.impl.item.firework.MinestomFireworkEffectProvider;
import eu.darkcube.system.minestom.impl.item.flag.MinestomItemFlagProvider;
import eu.darkcube.system.minestom.impl.item.material.MinestomMaterialProvider;
import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.server.item.EquipmentSlotProvider;
import eu.darkcube.system.server.item.ItemProvider;
import eu.darkcube.system.server.item.attribute.AttributeModifierProvider;
import eu.darkcube.system.server.item.attribute.AttributeProvider;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;
import eu.darkcube.system.server.item.firework.FireworkEffectProvider;
import eu.darkcube.system.server.item.flag.ItemFlagProvider;
import eu.darkcube.system.server.item.material.MaterialProvider;
import eu.darkcube.system.server.util.DarkCubeServer;
import eu.darkcube.system.server.version.ServerVersion;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.GameState;
import eu.darkcube.system.version.Version;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.extensions.Extension;

public class DarkCubeSystemMinestomExtension extends Extension {
    private final PacketHandler<PacketRequestProtocolVersionDeclaration> versionDeclarationHandler = packet -> declareVersion();
    private final ServiceListener listener = new ServiceListener();

    @Override public void preInitialize() {
        var ext = InjectionLayer.ext();
        install(ext, Version.class, new MinestomVersion());
        install(ext, AdventureSupport.class, new MinestomAdventureSupportImpl());
        install(ext, EquipmentSlotProvider.class, new MinestomEquipmentSlotProvider());
        install(ext, ItemProvider.class, new MinestomItemProvider());
        install(ext, MaterialProvider.class, new MinestomMaterialProvider());
        install(ext, ItemFlagProvider.class, new MinestomItemFlagProvider());
        install(ext, FireworkEffectProvider.class, new MinestomFireworkEffectProvider());
        install(ext, EnchantmentProvider.class, new MinestomEnchantmentProvider());
        install(ext, AttributeProvider.class, new MinestomAttributeProvider());
        install(ext, AttributeModifierProvider.class, new MinestomAttributeModifierProvider());
    }

    private <T> void install(InjectionLayer<?> layer, Class<T> type, T instance) {
        layer.install(BindingBuilder.create().bind(type).toInstance(instance));
    }

    @Override public void initialize() {
        InjectionLayer.boot().instance(EventManager.class).registerListener(listener);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, event -> {
            if (!DarkCubeServer.autoConfigure()) return;
            DarkCubeServer.playingPlayers().decrementAndGet();
            InjectionLayer.boot().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
        });
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            if (!DarkCubeServer.autoConfigure()) return;
            DarkCubeServer.playingPlayers().incrementAndGet();
            InjectionLayer.boot().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
        });
    }

    @Override public void postInitialize() {
        PacketAPI.instance().registerHandler(PacketRequestProtocolVersionDeclaration.class, versionDeclarationHandler);
        declareVersion();
        if (DarkCubeServer.autoConfigure()) {
            DarkCubeServer.gameState(GameState.INGAME);
            InjectionLayer.ext().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
        }
    }

    @Override public void terminate() {
        InjectionLayer.boot().instance(EventManager.class).unregisterListener(listener);
        PacketAPI.instance().unregisterHandler(versionDeclarationHandler);
    }

    private Packet declareVersion() {
        var componentInfo = InjectionLayer.boot().instance(ComponentInfo.class);
        var version = ServerVersion.version().protocolVersion();
        new PacketDeclareProtocolVersion(componentInfo.componentName(), new int[]{version}).sendAsync();
        return null;
    }
}

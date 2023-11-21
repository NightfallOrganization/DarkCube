/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom;

import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.system.internal.PacketDeclareProtocolVersion;
import eu.darkcube.system.internal.PacketRequestProtocolVersionDeclaration;
import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.packetapi.PacketHandler;
import eu.darkcube.system.server.util.DarkCubeServer;
import eu.darkcube.system.util.GameState;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extensions.Extension;

public class DarkCubeSystemMinestomExtension extends Extension {
    private final PacketHandler<PacketRequestProtocolVersionDeclaration> versionDeclarationHandler = packet -> declareVersion();
    private final ServiceListener listener = new ServiceListener();

    @Override public void initialize() {
        InjectionLayer.boot().instance(EventManager.class).registerListener(listener);
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, event -> {
            if (!DarkCubeServer.autoConfigure()) return;
            DarkCubeServer.playingPlayers().decrementAndGet();
            InjectionLayer.boot().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, event -> {
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
        ComponentInfo componentInfo = InjectionLayer.boot().instance(ComponentInfo.class);
        new PacketDeclareProtocolVersion(componentInfo.componentName(), new int[]{MinecraftServer.PROTOCOL_VERSION}).sendAsync();
        return null;
    }
}

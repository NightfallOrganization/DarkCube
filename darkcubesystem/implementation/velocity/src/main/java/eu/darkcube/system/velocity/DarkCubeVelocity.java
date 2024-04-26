/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.viaversion.viaversion.api.Via;
import eu.darkcube.system.internal.PacketDeclareProtocolVersion;
import eu.darkcube.system.internal.PacketRequestProtocolVersionDeclaration;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.packets.PacketWNPlayerLogin;
import org.slf4j.Logger;

import java.util.Arrays;

@Plugin(id = "darkcubesystem", name = "DarkCubeSystem", version = "1.0", authors = {"DasBabyPixel"}, dependencies = {@Dependency(id = "viaversion")})
public class DarkCubeVelocity {
    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public DarkCubeVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void handle(ProxyInitializeEvent event) {
        PacketAPI.instance().registerHandler(PacketDeclareProtocolVersion.class, packet -> {
            logger.info("Server " + packet.serverName() + " has protocol versions " + Arrays.toString(packet.protocolVersions()));
            Via.proxyPlatform().protocolDetectorService().setProtocolVersions(packet.serverName(), packet.protocolVersions());
            return null;
        });
        new PacketRequestProtocolVersionDeclaration().sendAsync();
    }

    @Subscribe
    public void handle(LoginEvent event) {
        new PacketWNPlayerLogin(event.getPlayer().getUniqueId(), event.getPlayer().getUsername()).sendQuery(PacketWNPlayerLogin.Response.class);
    }
}

/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system;

import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.arguments.EntityOptions;
import eu.darkcube.system.internal.PacketDeclareProtocolVersion;
import eu.darkcube.system.internal.PacketRequestProtocolVersionDeclaration;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.link.LinkManager;
import eu.darkcube.system.link.cloudnet.CloudNetLink;
import eu.darkcube.system.link.luckperms.LuckPermsLink;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.BukkitUserAPI;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.AsyncExecutor;
import eu.darkcube.system.version.BukkitVersion;
import eu.darkcube.system.version.Version;
import eu.darkcube.system.version.VersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.Objects;

@ApiStatus.Internal public final class DarkCubeSystem extends DarkCubePlugin implements Listener {
    private final LinkManager linkManager = new LinkManager();
    private BukkitUserAPI userAPI;

    public DarkCubeSystem() {
        super("system");
        DarkCubePlugin.systemPlugin(this);
    }

    // Suppress the conversion warnings as the LuckPermsLink has to be lazily loaded with a new inner class
    @SuppressWarnings("Convert2MethodRef") @Override public void onLoad() {
        VersionSupport.version();
        AsyncExecutor.start();
        EntityOptions.registerOptions();
        PacketAPI.init();
        CommandAPI.init(this);
        userAPI = new BukkitUserAPI();
        linkManager.addLink(() -> new LuckPermsLink());
        linkManager.addLink(() -> new CloudNetLink(this));
    }

    @Override public void onDisable() {
        AsyncExecutor.stop();
        AdventureSupport.audienceProvider().close();
        linkManager.unregisterLinks();
    }

    @Override public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        AdventureSupport.audienceProvider(); // Initializes adventure
        linkManager.enableLinks();
        Version v = VersionSupport.version();
        if (v instanceof BukkitVersion) {
            ((BukkitVersion) v).enabled(this);
        }
        PacketAPI.getInstance().registerHandler(PacketRequestProtocolVersionDeclaration.class, packet -> {
            declareVersion();
            return null;
        });
        declareVersion();
    }

    public void declareVersion() {
        new PacketDeclareProtocolVersion(InjectionLayer
                .boot()
                .instance(ComponentInfo.class)
                .componentName(), ((BukkitVersion) VersionSupport.version()).protocolVersion()).sendAsync();
    }

    @EventHandler public void handle(PlayerKickEvent event) {
        if (Objects.equals(event.getReason(), "disconnect.spam")) {
            event.setCancelled(true);
        }
    }
}

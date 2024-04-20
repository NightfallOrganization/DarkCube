/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.impl;

import java.util.Objects;

import dev.derklaro.aerogel.binding.BindingBuilder;
import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.bukkit.commandapi.argument.EntityOptions;
import eu.darkcube.system.bukkit.impl.link.cloudnet.CloudNetLink;
import eu.darkcube.system.bukkit.impl.link.luckperms.LuckPermsLink;
import eu.darkcube.system.bukkit.impl.util.BukkitAdventureSupportImpl;
import eu.darkcube.system.bukkit.impl.version.BukkitVersionHandler;
import eu.darkcube.system.bukkit.impl.version.BukkitVersionImpl;
import eu.darkcube.system.bukkit.impl.version.BukkitVersionLoader;
import eu.darkcube.system.bukkit.link.LinkManager;
import eu.darkcube.system.bukkit.provider.via.ViaSupport;
import eu.darkcube.system.internal.PacketDeclareProtocolVersion;
import eu.darkcube.system.internal.PacketRequestProtocolVersionDeclaration;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.server.version.ServerVersion;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.AsyncExecutor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

@ApiStatus.Internal
public final class DarkCubeSystemBukkit extends DarkCubePlugin implements Listener {
    private final LinkManager linkManager = new LinkManager();
    private final BukkitVersionHandler versionHandler;

    public DarkCubeSystemBukkit() {
        super("system");
        versionHandler = new BukkitVersionLoader().load();
        DarkCubePlugin.systemPlugin(this);
        PacketAPI.instance().classLoader(getClassLoader());
    }

    @Override
    public void onLoad() {
        versionHandler.onLoad();
        AsyncExecutor.start();
        EntityOptions.registerOptions();
        PacketAPI.init();
        CommandAPI.init();
        linkManager.addLink(() -> new LuckPermsLink(this));
        linkManager.addLink(() -> new CloudNetLink(this));
    }

    @Override
    public void onDisable() {
        AsyncExecutor.stop();
        AdventureSupport.adventureSupport().audienceProvider().close();
        linkManager.unregisterLinks();
        versionHandler.onDisable();
    }

    @Override
    public void onEnable() {
        InjectionLayer.ext().install(BindingBuilder.create().bind(AdventureSupport.class).toInstance(new BukkitAdventureSupportImpl(this)));
        versionHandler.onEnable();
        Bukkit.getPluginManager().registerEvents(this, this);
        linkManager.enableLinks();
        BukkitVersionImpl.version().enabled(this);
        Runnable run = () -> {
            PacketAPI.instance().registerHandler(PacketRequestProtocolVersionDeclaration.class, packet -> {
                declareVersion();
                return null;
            });
            declareVersion();
        };
        if (ServerVersion.version().provider().service(ViaSupport.class).supported()) {
            Bukkit.getScheduler().runTaskLater(this, run, 2);
        } else {
            run.run();
        }
    }

    public void declareVersion() {
        var via = ServerVersion.version().provider().service(ViaSupport.class);
        var supported = via.supported() ? via.supportedVersions() : new int[0];
        if (supported.length == 0) supported = new int[]{ServerVersion.version().protocolVersion()};
        new PacketDeclareProtocolVersion(InjectionLayer.boot().instance(ComponentInfo.class).componentName(), supported).sendAsync();
    }

    @EventHandler
    public void handle(PlayerKickEvent event) {
        if (Objects.equals(event.getReason(), "disconnect.spam")) {
            event.setCancelled(true);
        }
    }
}

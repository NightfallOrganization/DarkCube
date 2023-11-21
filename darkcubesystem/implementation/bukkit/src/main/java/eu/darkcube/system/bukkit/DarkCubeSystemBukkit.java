/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit;

import dev.derklaro.reflexion.MethodAccessor;
import dev.derklaro.reflexion.Reflexion;
import dev.derklaro.reflexion.Result;
import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.bukkit.commandapi.argument.EntityOptions;
import eu.darkcube.system.bukkit.link.LinkManager;
import eu.darkcube.system.bukkit.link.cloudnet.CloudNetLink;
import eu.darkcube.system.bukkit.link.luckperms.LuckPermsLink;
import eu.darkcube.system.bukkit.provider.via.ViaSupport;
import eu.darkcube.system.bukkit.userapi.BukkitUserAPI;
import eu.darkcube.system.bukkit.util.BukkitAdventureSupportImpl;
import eu.darkcube.system.bukkit.version.BukkitVersionImpl;
import eu.darkcube.system.internal.PacketDeclareProtocolVersion;
import eu.darkcube.system.internal.PacketRequestProtocolVersionDeclaration;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.server.version.ServerVersion;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.AdventureSupportHolderAccess;
import eu.darkcube.system.util.AsyncExecutor;
import eu.darkcube.system.version.Version;
import eu.darkcube.system.version.VersionHolderAccess;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.Objects;
import java.util.Optional;

@ApiStatus.Internal public final class DarkCubeSystemBukkit extends DarkCubePlugin implements Listener {
    private final LinkManager linkManager = new LinkManager();
    private BukkitUserAPI userAPI;

    public DarkCubeSystemBukkit() {
        super("system");
        DarkCubePlugin.systemPlugin(this);
    }

    @Override public void onLoad() {
        Version version = loadVersion();
        VersionHolderAccess.instance(version);
        AsyncExecutor.start();
        EntityOptions.registerOptions();
        PacketAPI.init();
        CommandAPI.init();
        userAPI = new BukkitUserAPI();
        linkManager.addLink(() -> new LuckPermsLink(this));
        linkManager.addLink(() -> new CloudNetLink(this));
    }

    @Override public void onDisable() {
        AsyncExecutor.stop();
        userAPI.close();
        AdventureSupport.adventureSupport().audienceProvider().close();
        linkManager.unregisterLinks();
    }

    @Override public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        AdventureSupportHolderAccess.instance(new BukkitAdventureSupportImpl(this));
        linkManager.enableLinks();
        BukkitVersionImpl.version().enabled(this);
        PacketAPI.instance().registerHandler(PacketRequestProtocolVersionDeclaration.class, packet -> {
            declareVersion();
            return null;
        });
        declareVersion();
    }

    public void declareVersion() {
        var via = ServerVersion.version().provider().service(ViaSupport.class);
        var supported = via.supported() ? via.supportedVersions() : new int[]{ServerVersion.version().protocolVersion()};
        new PacketDeclareProtocolVersion(InjectionLayer.boot().instance(ComponentInfo.class).componentName(), supported).sendAsync();
    }

    private Version loadVersion() {
        String className = getClass().getPackage().getName() + ".version.v" + Bukkit
                .getServer()
                .getBukkitVersion()
                .replace('.', '_')
                .split("-", 2)[0] + ".VersionImpl";
        Optional<Reflexion> reflexion;
        try {
            Class<?> cls = Class.forName(className);
            reflexion = Optional.of(Reflexion.on(cls));
        } catch (ClassNotFoundException e) {
            reflexion = Optional.empty();
        }
        return (Version) reflexion
                .flatMap(Reflexion::findConstructor)
                .map(MethodAccessor::invoke)
                .flatMap(Result::asOptional)
                .orElseThrow(() -> new AssertionError("Failed to load class " + className));
    }

    @EventHandler public void handle(PlayerKickEvent event) {
        if (Objects.equals(event.getReason(), "disconnect.spam")) {
            event.setCancelled(true);
        }
    }
}

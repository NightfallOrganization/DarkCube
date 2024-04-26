/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit;

import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.system.bukkit.Plugin;
import eu.darkcube.system.bukkit.commandapi.deprecated.CommandAPI;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.pserver.bukkit.command.CommandPServer;
import eu.darkcube.system.pserver.bukkit.command.PServerCommand;
import eu.darkcube.system.pserver.bukkit.packethandler.*;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packets.nw.*;
import eu.darkcube.system.pserver.common.packets.wn.PacketGetUniqueId;
import eu.darkcube.system.pserver.common.packets.wn.PacketOnlinePlayersSet;
import eu.darkcube.system.pserver.common.packets.wn.PacketSetRunning;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class PServerWrapper extends Plugin {

    private static PServerWrapper instance;
    private static PServerCommand PServerCommand;
    // private UniqueId id;

    public PServerWrapper() {
        super("pserver");
        instance = this;
    }

    public static PServerCommand getPServerCommand() {
        return PServerCommand;
    }

    public static void setPServerCommand(PServerCommand pServerCommand) {
        PServerWrapper.PServerCommand = pServerCommand;
    }

    public static PServerWrapper getInstance() {
        return instance;
    }

    @Override public void onLoad() {

        WrapperServiceInfoUtil.init();
        WrapperPServerProvider.init();

        PacketAPI pm = PacketAPI.getInstance();
        pm.registerHandler(PacketStart.class, new HandlerStart());
        pm.registerHandler(PacketStop.class, new HandlerStop());
        pm.registerHandler(PacketUpdate.class, new HandlerUpdate());
        pm.registerHandler(PacketAddOwner.class, new HandlerAddOwner());
        pm.registerHandler(PacketRemoveOwner.class, new HandlerRemoveOwner());

        UniqueId id;
        tr:
        try {
            ComponentInfo componentInfo = InjectionLayer.boot().instance(ComponentInfo.class);
            ServiceInfoHolder serviceInfoHolder = InjectionLayer.boot().instance(ServiceInfoHolder.class);
            EventManager eventManager = InjectionLayer.boot().instance(EventManager.class);
            id = new PacketGetUniqueId(componentInfo.componentName()).sendQuery(PacketGetUniqueId.Response.class).id();
            if (id == null) {
                System.out.println("[PSERVER] LOADING PSERVER API...");
                break tr;
            }
            ConfigurationListener ll = new ConfigurationListener(id);
            eventManager.registerListener(ll);
            serviceInfoHolder.publishServiceInfoUpdate();
            System.out.println("[PSERVER] LOADING PSERVER...");
            WrapperPServerProvider.instance().self = WrapperPServerProvider.instance().pserver(id).get();
            System.out.println("[PSERVER] PSERVER ID: " + id);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("[PSERVER] LOADING PSERVER API...");
        }
    }

    @Override public void onEnable() {
        CommandAPI.enable(this, new CommandPServer());
        if (PServerProvider.instance().isPServer()) {
            if (PServerProvider.instance().isPServer()) {
                new PacketOnlinePlayersSet(PServerProvider.instance().currentPServer().id(), 0).sendAsync();
                Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
            }
            new BukkitRunnable() {
                @Override public void run() {
                    System.out.println("[PServer] Set status as RUNNING");
                    new PacketSetRunning(PServerProvider.instance().currentPServer().id()).sendAsync();
                }
            }.runTaskAsynchronously(PServerWrapper.getInstance());
        }
    }

    @Override public String getCommandPrefix() {
        return "PServer";
    }
}

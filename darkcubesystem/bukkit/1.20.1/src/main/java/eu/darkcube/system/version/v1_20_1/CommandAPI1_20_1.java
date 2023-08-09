/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version.v1_20_1;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.version.BukkitCommandAPI;
import eu.darkcube.system.version.v1_20_1.commandapi.CommandConverter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_20_R1.command.VanillaCommandWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

public class CommandAPI1_20_1 extends BukkitCommandAPI implements Listener {
    private final Map<VanillaCommandWrapper, VanillaCommandWrapper> custom = new HashMap<>();
    private volatile boolean requireSync = false;
    private int requireSyncTick = 0;

    private static VanillaCommandWrapper[] wrappers(CommandExecutor command) {
        final String prefix = command.getPrefix().toLowerCase(Locale.ROOT);
        if (prefix.contains(" ")) throw new IllegalArgumentException("Can't register command with whitespace in prefix!");

        for (String name : command.getNames()) {
            if (name.contains(" ")) throw new IllegalArgumentException("Can't register command with whitespace in name!");
        }

        CommandConverter converter = new CommandConverter(command);
        return converter.convert();
    }

    private void requireSync() {
        requireSync = true;
        requireSyncTick = MinecraftServer.getServer().getTickCount();
    }

    public void enabled(DarkCubeSystem system) {
        new BukkitRunnable() {
            @Override public void run() {
                if (requireSync) {
                    if (requireSyncTick + 10 >= MinecraftServer.getServer().getTickCount()) return;
                    requireSync = false;
                    ((CraftServer) Bukkit.getServer()).syncCommands();
                }
            }
        }.runTaskTimer(system, 1, 1);
    }

    @Override public String getSpigotUnknownCommandMessage() {
        return SpigotConfig.unknownCommandMessage;
    }

    @Override public PluginCommand registerLegacy(Plugin plugin, Command command) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand plugincommand = constructor.newInstance(command.getName(), plugin);
            plugincommand.setAliases(Arrays.asList(command.getAliases()));
            plugincommand.setUsage(command.getSimpleLongUsage());
            plugincommand.setPermission(command.getPermission());
            CraftServer server = (CraftServer) Bukkit.getServer();
            CraftCommandMap commandMap = (CraftCommandMap) server.getCommandMap();
            register(commandMap.getKnownCommands(), plugin, plugincommand);
            return plugincommand;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void unregister(String name) {
        Map<String, org.bukkit.command.Command> knownCommands = Bukkit.getServer().getCommandMap().getKnownCommands();
        knownCommands.remove(name);
    }

    @Override public void register(CommandExecutor command) {
        try {
            unregister(command);
            VanillaCommandWrapper[] wrappers = wrappers(command);

            CommandDispatcher<CommandSourceStack> mcd = MinecraftServer.getServer().vanillaCommandDispatcher.getDispatcher();

            for (VanillaCommandWrapper wrapper : wrappers) {
                Iterator<CommandNode<CommandSourceStack>> it = mcd.getRoot().getChildren().iterator();
                while (it.hasNext()) {
                    CommandNode<CommandSourceStack> node = it.next();
                    if (node.getName().equals(wrapper.getName())) {
                        org.bukkit.command.Command cmd = Bukkit.getCommandMap().getKnownCommands().get(node.getName());
                        if (cmd == null) {
                            Bukkit
                                    .getCommandMap()
                                    .getKnownCommands()
                                    .put(node.getName(), new VanillaCommandWrapper(MinecraftServer.getServer().vanillaCommandDispatcher, node));
                        }
                        it.remove();
                    }
                }
                org.bukkit.command.Command cmd = Bukkit.getCommandMap().getKnownCommands().put(wrapper.getName(), wrapper);

                if (cmd != null) {
                    Logger
                            .getLogger("CommandAPI")
                            .warning("Overridden command: " + cmd.getName() + " (" + cmd.getClass().getSimpleName() + ")");
                    if (cmd instanceof VanillaCommandWrapper w) {
                        custom.put(wrapper, w);
                    } else {
                        custom.put(wrapper, wrapper);
                    }
                } else {
                    custom.put(wrapper, wrapper);
                }
                mcd.getRoot().addChild(wrapper.vanillaCommand);
            }
            if (command.getPermission() != null) {
                if (Bukkit.getPluginManager().getPermission(command.getPermission()) == null) {
                    Bukkit.getPluginManager().addPermission(new Permission(command.getPermission()));
                }
            }
            requireSync();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override public void unregister(CommandExecutor command) {
        Map<String, org.bukkit.command.Command> knownCommands = Bukkit.getCommandMap().getKnownCommands();
        String prefix = command.getPrefix().toLowerCase(Locale.ROOT);
        for (String name : command.getNames()) {
            unregister(knownCommands, name.toLowerCase(Locale.ROOT));
            unregister(knownCommands, prefix + ":" + name.toLowerCase(Locale.ROOT));
        }
        requireSync();
    }

    private void unregister(Map<String, org.bukkit.command.Command> knownCommands, String name) {
        org.bukkit.command.Command cmd = knownCommands.get(name);
        if (!(cmd instanceof VanillaCommandWrapper w)) return;
        if (!custom.containsKey(w)) return;
        VanillaCommandWrapper wrapper = custom.remove(w);
        if (wrapper == w) wrapper = null;
        knownCommands.remove(name);
        for (CommandNode<CommandSourceStack> node : new ArrayList<>(MinecraftServer.getServer().vanillaCommandDispatcher
                .getDispatcher()
                .getRoot()
                .getChildren())) {
            if (node.getName().equals(name)) {
                MinecraftServer.getServer().vanillaCommandDispatcher.getDispatcher().getRoot().getChildren().remove(node);
                if (wrapper != null) {
                    Logger.getLogger("CommandAPI").warning("Reinstalling command: " + wrapper.getName());
                    knownCommands.put(node.getName(), wrapper);
                    MinecraftServer.getServer().vanillaCommandDispatcher.getDispatcher().getRoot().addChild(wrapper.vanillaCommand);
                }
                break;
            }
        }
    }

    @Override public double[] getEntityBB(Entity entity) {
        return new double[]{entity.getBoundingBox().getMinX(), entity.getBoundingBox().getMinY(), entity.getBoundingBox().getMinZ(), entity.getBoundingBox().getMaxX(), entity.getBoundingBox().getMaxY(), entity.getBoundingBox().getMaxZ()};
    }

    private void register(Map<String, org.bukkit.command.Command> known, Plugin plugin, PluginCommand command) {
        String name = command.getName().toLowerCase(Locale.ENGLISH);
        String prefix = plugin.getName().toLowerCase(Locale.ENGLISH);
        List<String> successfulNames = new ArrayList<>();
        register(known, name, prefix, false, command, successfulNames);
        for (String alias : command.getAliases()) {
            register(known, alias, prefix, true, command, successfulNames);
        }
    }

    private void register(Map<String, org.bukkit.command.Command> known, String name, String prefix, boolean alias, org.bukkit.command.Command command, List<String> successfulNames) {
        String key = prefix + ":" + name;
        boolean work = false;
        if (known.containsKey(key)) {
            org.bukkit.command.Command ex = known.get(key);
            DarkCubePlugin
                    .systemPlugin()
                    .getLogger()
                    .warning("[CommandAPI] Failed to register command: Command with that name already exists");
            DarkCubePlugin.systemPlugin().getLogger().warning("[CommandAPI] Command: " + key + " - " + ex);
            if (!alias) {
                command.setLabel(key);
            }
        } else {
            work = true;
        }
        if (work) {
            known.put(key, command);
            successfulNames.add(key);
        }
        work = false;
        if (known.containsKey(name)) {
            org.bukkit.command.Command ex = known.get(name);
            if (ex instanceof VanillaCommandWrapper) {
                work = true;
            } else {
                DarkCubePlugin
                        .systemPlugin()
                        .getLogger()
                        .warning("[CommandAPI] Failed to register command: Command with that name already exists");
                DarkCubePlugin.systemPlugin().getLogger().warning("[CommandAPI] Command: " + name + " - " + ex);
            }
        } else {
            work = true;
        }
        if (work) {
            known.put(name, command);
            successfulNames.add(name);
        }
    }
}

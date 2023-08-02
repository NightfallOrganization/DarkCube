/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version.v1_19_3;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.InternalCommandTabExecutor;
import eu.darkcube.system.version.BukkitCommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_19_R2.command.VanillaCommandWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandAPI1_19_3 extends BukkitCommandAPI {

    private final TabExecutor executor = new InternalCommandTabExecutor();

    @Override
    public String getSpigotUnknownCommandMessage() {
        return SpigotConfig.unknownCommandMessage;
    }

    @Override
    public PluginCommand registerLegacy(Plugin plugin, Command command) {
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

    @Override
    public void unregister(String name) {
        SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
        final Map<String, org.bukkit.command.Command> knownCommands = commandMap.getKnownCommands();
        knownCommands.remove(name);
    }

    @Override
    public void register(CommandExecutor command) {
        try {
            final Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            final String name = command.getName().toLowerCase();
            if (name.contains(" ")) {
                throw new IllegalArgumentException("Can't register command with whitespace in name!");
            }
            final String[] aliases = command.getAliases();
            for (int i = 0; i < aliases.length; i++) {
                aliases[i] = aliases[i].toLowerCase();
            }
            final PluginCommand plugincommand = constructor.newInstance(name, DarkCubePlugin.systemPlugin());
            plugincommand.setAliases(Arrays.asList(aliases));
            plugincommand.setUsage("/" + name);
            plugincommand.setPermission(command.getPermission());
            plugincommand.setExecutor(this.executor);
            plugincommand.setTabCompleter(this.executor);
            Field ftimings = plugincommand.getClass().getField("timings");
            Method timingsOf = Class.forName("co.aikar.timings.Timings").getMethod("of", Plugin.class, String.class);
            ftimings.set(plugincommand, timingsOf.invoke(null, DarkCubePlugin.systemPlugin(), DarkCubePlugin
                    .systemPlugin()
                    .getName() + ":" + name));
            SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
            final Map<String, org.bukkit.command.Command> knownCommands = commandMap.getKnownCommands();
            final String prefix = command.getPrefix().toLowerCase();
            if (prefix.contains(" ")) {
                throw new IllegalArgumentException("Can't register command with whitespace in prefix!");
            }
            for (String n : command.getNames()) {
                final String registerName = prefix + ":" + n;
                this.checkAmbiguities(registerName, knownCommands, plugincommand);
            }
            for (String n : command.getNames()) {
                this.checkAmbiguities(n, knownCommands, plugincommand);
            }
            if (command.getPermission() != null) {
                if (Bukkit.getPluginManager().getPermission(command.getPermission()) == null) {
                    Bukkit.getPluginManager().addPermission(new Permission(command.getPermission()));
                }
            }
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public double[] getEntityBB(Entity entity) {
        return new double[]{entity.getBoundingBox().getMinX(), entity.getBoundingBox().getMinY(), entity.getBoundingBox().getMinZ(), entity.getBoundingBox().getMaxX(), entity.getBoundingBox().getMaxY(), entity.getBoundingBox().getMaxZ()};
    }

    private void checkAmbiguities(String registerName, Map<String, org.bukkit.command.Command> knownCommands, PluginCommand command) {
        registerName = registerName.toLowerCase();
        if (knownCommands.containsKey(registerName)) {
            org.bukkit.command.Command cmd = knownCommands.get(registerName);
            if (cmd instanceof PluginCommand pcmd) {
                if (pcmd.getExecutor() == this.executor && pcmd.getTabCompleter() == this.executor) {
                    return;
                }
                Bukkit
                        .getConsoleSender()
                        .sendMessage("§6Overriding command " + registerName + " from Plugin " + pcmd.getPlugin().getName() + "!");
            } else {
                Bukkit
                        .getConsoleSender()
                        .sendMessage("§6Overriding command " + registerName + " from type " + cmd.getClass().getSimpleName() + "!");
            }
        }
        knownCommands.put(registerName, command);
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

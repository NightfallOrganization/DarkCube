/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version.v1_20_1;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendSuggestionsEvent;
import com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.InternalCommandTabExecutor;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.context.StringRange;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.version.BukkitCommandAPI;
import eu.darkcube.system.version.v1_20_1.commandapi.CommandConverter;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.command.CraftCommandMap;
import org.bukkit.craftbukkit.v1_20_R1.command.VanillaCommandWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandAPI1_20_1 extends BukkitCommandAPI implements Listener {
    private final Collection<VanillaCommandWrapper> custom = new ArrayList<>();

    @Override public String getSpigotUnknownCommandMessage() {
        return SpigotConfig.unknownCommandMessage;
    }

    public static com.mojang.brigadier.suggestion.Suggestions convert(Suggestions suggestions, int offset) {
        return new com.mojang.brigadier.suggestion.Suggestions(convertRange(suggestions.getRange(), offset), suggestions
                .getList()
                .stream()
                .map(s -> new Suggestion(convertRange(s.getRange(), offset), s.getText()))
                .toList());
    }

    private static com.mojang.brigadier.context.StringRange convertRange(StringRange range, int offset) {
        return new com.mojang.brigadier.context.StringRange(range.getStart() + offset, range.getEnd() + offset);
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
            VanillaCommandWrapper[] wrappers = wrappers(command);

            for (VanillaCommandWrapper wrapper : wrappers) {
                Bukkit.getCommandMap().getKnownCommands().put(wrapper.getName(), wrapper);
                MinecraftServer.getServer().vanillaCommandDispatcher.getDispatcher().getRoot().addChild(wrapper.vanillaCommand);
            }
//            final Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
//            constructor.setAccessible(true);
//
//            final String[] aliases = command.getAliases();
//            for (int i = 0; i < aliases.length; i++) aliases[i] = aliases[i].toLowerCase();
//            final PluginCommand plugincommand = constructor.newInstance(name, DarkCubePlugin.systemPlugin());
//            plugincommand.setAliases(Arrays.asList(aliases));
//            plugincommand.setUsage("/" + name);
//            plugincommand.setPermission(command.getPermission());
//            plugincommand.setExecutor(this.executor);
//            plugincommand.setTabCompleter(this.executor);
//            //noinspection removal
//            plugincommand.timings = co.aikar.timings.Timings.of(DarkCubeSystem.systemPlugin(), DarkCubeSystem
//                    .systemPlugin()
//                    .getName() + ":" + name);
//            SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
//            final Map<String, org.bukkit.command.Command> knownCommands = commandMap.getKnownCommands();
//
//            for (String n : command.getNames()) {
//                final String registerName = prefix + ":" + n;
//                this.checkAmbiguities(registerName, knownCommands, plugincommand);
//            }
//            for (String n : command.getNames()) {
//                this.checkAmbiguities(n, knownCommands, plugincommand);
//            }
            if (command.getPermission() != null) {
                if (Bukkit.getPluginManager().getPermission(command.getPermission()) == null) {
                    Bukkit.getPluginManager().addPermission(new Permission(command.getPermission()));
                }
            }
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static VanillaCommandWrapper[] wrappers(CommandExecutor command) {
        final String prefix = command.getPrefix().toLowerCase(Locale.ROOT);
        if (prefix.contains(" ")) throw new IllegalArgumentException("Can't register command with whitespace in prefix!");

        for (String name : command.getNames()) {
            if (name.contains(" ")) throw new IllegalArgumentException("Can't register command with whitespace in name!");
        }

        CommandConverter converter = new CommandConverter(command);
        return converter.convert();
    }

    @Override public void unregister(CommandExecutor command) {
        Map<String, org.bukkit.command.Command> knownCommands = Bukkit.getCommandMap().getKnownCommands();
        String prefix = command.getPrefix().toLowerCase(Locale.ROOT);
        for (String name : command.getNames()) {
            unregister(knownCommands, name.toLowerCase(Locale.ROOT));
            unregister(knownCommands, prefix + ":" + name.toLowerCase(Locale.ROOT));
        }
        ((CraftServer) Bukkit.getServer()).syncCommands();
    }

    private void unregister(Map<String, org.bukkit.command.Command> knownCommands, String name) {
        org.bukkit.command.Command cmd = knownCommands.get(name);
        if (!(cmd instanceof VanillaCommandWrapper w)) return;
        if (!custom.contains(w)) return;
        custom.remove(w);
        knownCommands.remove(name);
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

    private static class TabQueue {

        private final Queue<Entry> entries = new ConcurrentLinkedQueue<>();

        private record Entry(String commandLine, Suggestions suggestions) {
        }
    }
}

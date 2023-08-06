/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.provider.via.ViaSupport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BukkitCommandAPI implements Version.CommandAPI {
    private static String join(String label, String[] args) {
        return args.length == 0 ? label : (label + " " + String.join(" ", args));
    }

    @Override public List<String> tabComplete(CommandSender sender, Command command, String label, String[] args) {
        String commandLine = join(label, args);
        String commandLineStart = join(label, Arrays.copyOfRange(args, 0, args.length - 1)) + " ";
        CommandSource source = CommandSource.create(sender);
        ParseResults<CommandSource> parse = CommandAPI.instance().getCommands().getDispatcher().parse(commandLine, source);
        return getCompletions(source, parse, sender, commandLine, commandLineStart);
    }

    public List<String> convertCompletions(Suggestions suggestions, CommandSource source, ParseResults<CommandSource> parse, CommandSender sender, String commandLine, String commandLineStart) {
        if (!suggestions.isEmpty()) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ViaSupport via = VersionSupport.version().provider().service(ViaSupport.class);
                if (via.supported()) {
                    int server = via.serverVersion();
                    if (server < 393) { // TODO: Move this into MC version 1.8
                        int version = via.version(p.getUniqueId());
                        if (version < 393) { // 393 is the version for 1.13
                            // https://minecraft.fandom.com/wiki/Protocol_version
                            source.sendMessage(Component.text(" "));
                            source.sendCompletions(commandLine, suggestions);
                        } else {
                            return via.tabComplete(version, p, commandLine, parse, suggestions);
                        }
                    }
                } else {
                    source.sendMessage(Component.text(" "));
                    source.sendCompletions(commandLine, suggestions);
                }
            }
        }
        List<String> r = new ArrayList<>();
        for (Suggestion completion : suggestions.getList()) {
            String c = completion.apply(commandLine);
            if (!c.startsWith(commandLineStart)) continue;
            r.add(c.substring(commandLineStart.length()));
        }
        return r;
    }

    public List<String> getCompletions(CommandSource source, ParseResults<CommandSource> parse, CommandSender sender, String commandLine, String commandLineStart) {
        Suggestions completions = CommandAPI.instance().getCommands().getTabCompletionsSync(parse);
        return convertCompletions(completions, source, parse, sender, commandLine, commandLineStart);
    }
}

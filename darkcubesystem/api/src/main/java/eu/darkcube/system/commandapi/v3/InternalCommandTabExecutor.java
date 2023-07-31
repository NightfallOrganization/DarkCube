/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApiStatus.Internal
public class InternalCommandTabExecutor implements TabExecutor {
    private static String sjoin(String label, String[] args) {
        return args.length == 0 ? label : (label + " " + String.join(" ", args));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        String commandLine = sjoin(label, args);
        String commandLine2 = sjoin(label, Arrays.copyOfRange(args, 0, args.length - 1)) + " ";
        List<Suggestion> completions = eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().getCommands().getTabCompletions(sender, commandLine);
        ICommandExecutor executor = new BukkitCommandExecutor(sender);
        if (!completions.isEmpty()) {
            if (sender instanceof Player) {
//                Player p = (Player) sender;
//                if (ViaSupport.supported()) {
//                    int version = ViaSupport.getVersion(p);
//                    if (version < 393) { // 393 is the version for 1.13
//                        // https://minecraft.fandom.com/wiki/Protocol_version
//                        executor.sendMessage(Component.text(" "));
//                        executor.sendCompletions(commandLine, completions);
//                    } else {
//                        ViaTabExecutor.work(version, p);
//                        return Collections.emptyList();
//                    }
//                } else {
                executor.sendMessage(Component.text(" "));
                executor.sendCompletions(commandLine, completions);
//                }
            }
        }
        List<String> r = new ArrayList<>();
        for (Suggestion completion : completions) {
            String c = completion.apply(commandLine);
            if (!c.startsWith(commandLine2)) continue;
            r.add(c.substring(commandLine2.length()));
        }
        return r;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        String commandLine = sjoin(label, args);
        eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().getCommands().executeCommand(sender, commandLine);
        return true;
    }
}

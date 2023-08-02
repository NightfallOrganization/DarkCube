/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.version.VersionSupport;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

@ApiStatus.Internal
public class InternalCommandTabExecutor implements TabExecutor {
    private static String join(String label, String[] args) {
        return args.length == 0 ? label : (label + " " + String.join(" ", args));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return VersionSupport.version().commandApi().tabComplete(sender, command, label, args);
//        String commandLine = join(label, args);
//        String commandLine2 = join(label, Arrays.copyOfRange(args, 0, args.length - 1)) + " ";
//        ParseResults<CommandSource> parse = CommandAPI
//                .getInstance()
//                .getCommands()
//                .getDispatcher()
//                .parse(commandLine, CommandSource.create(sender));
//        List<Suggestion> completions = CommandAPI.getInstance().getCommands().getTabCompletionsSync(parse);
//        ICommandExecutor executor = new BukkitCommandExecutor(sender);
//        if (!completions.isEmpty()) {
//            if (sender instanceof Player) {
//                Player p = (Player) sender;
//                ViaSupport via = VersionSupport.version().provider().service(ViaSupport.class);
//                if (via.supported()) {
//                    int server = via.serverVersion();
//                    if (server < 393) {
//                        int version = via.version(p.getUniqueId());
//                        if (version < 393) { // 393 is the version for 1.13
//                            // https://minecraft.fandom.com/wiki/Protocol_version
//                            executor.sendMessage(Component.text(" "));
//                            executor.sendCompletions(commandLine, completions);
//                        } else {
//                            return via.tabComplete(version, p, commandLine, parse, completions);
////                            int id = ViaTabExecutor.work(version, p, commandLine, completions);
////                            return Collections.singletonList(via.TAB_COMPLETE_CANCEL + id);
//                        }
//                    }
//                } else {
//                    executor.sendMessage(Component.text(" "));
//                    executor.sendCompletions(commandLine, completions);
//                }
//            }
//        }
//        List<String> r = new ArrayList<>();
//        for (Suggestion completion : completions) {
//            String c = completion.apply(commandLine);
//            if (!c.startsWith(commandLine2)) continue;
//            r.add(c.substring(commandLine2.length()));
//        }
//        return r;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        String commandLine = join(label, args);
        eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().getCommands().executeCommand(sender, commandLine);
        return true;
    }
}

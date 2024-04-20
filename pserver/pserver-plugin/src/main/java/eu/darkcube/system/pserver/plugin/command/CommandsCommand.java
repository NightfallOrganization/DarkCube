/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServer;

public class CommandsCommand extends PServer {

    public CommandsCommand() {
        super("commands", new String[]{"befehle", "help"}, b -> b.executes(context -> {
            CommandSource source = context.getSource();
            source.sendMessage(Message.COMMANDS_PREFIX, PServer.COMMAND_NAMES.size());
            for (String name : PServer.COMMAND_NAMES) {
                source.sendMessage(Message.COMMANDS_COMMANDINFO, name);
            }
            return 0;
        }));
    }

}

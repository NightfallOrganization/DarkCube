/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit.command;

import eu.darkcube.system.bukkit.commandapi.deprecated.Command;
import eu.darkcube.system.pserver.bukkit.PServerWrapper;
import org.bukkit.command.CommandSender;

public class CommandPServer extends Command {

    public CommandPServer() {
        super(PServerWrapper.getInstance(), "pserver", new Command[0], "PServer");
        setAliases("ps", "s", "privateserver", "privatserver");
    }

    @Override public boolean execute(CommandSender sender, String[] args) {
        if (PServerWrapper.getPServerCommand() != null) {
            return PServerWrapper.getPServerCommand().execute(sender, args);
        }
        sender.sendMessage("§cPServer not configured.");
        return true;
    }
}

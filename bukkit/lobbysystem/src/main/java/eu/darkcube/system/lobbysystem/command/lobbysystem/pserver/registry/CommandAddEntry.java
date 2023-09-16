/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.pserver.registry;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;

public class CommandAddEntry extends LobbyCommand {
    public CommandAddEntry(Lobby lobby) {
        super("addEntry", b -> b.then(Commands
                .argument("task", StringArgumentType.word())
                .then(Commands.argument("data", StringArgumentType.word()).executes(ctx -> {
                    var task = StringArgumentType.getString(ctx, "task");
                    var data = StringArgumentType.getString(ctx, "data");
                    var protocol = Document.newJsonDocument();
                    protocol.append("task", task);
                    protocol.append("data", data);
                    lobby.gameRegistry().addEntry(task, data, protocol);
                    ctx.getSource().sendMessage(Component.text("Entry created"));
                    return 0;
                }))));
    }
}

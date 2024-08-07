/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import java.util.Set;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServer;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.inventory.ItemStack;

public class CommandBlockCommand extends PServer {

    public CommandBlockCommand() {
        super("commandblock", new String[0], b -> b.then(Commands.literal("add").then(Commands.argument("message", StringArgumentType.greedyString()).executes(context -> {
            var text = StringArgumentType.getString(context, "message");
            var block = getCommandBlock(context);
            block.setCommand(block.getCommand() + text);
            block.update(true);
            context.getSource().sendMessage(Message.COMMAND_BLOCK_CONTENT, block.getCommand());
            return 0;
        }))).then(Commands.literal("clear").executes(context -> {
            var block = getCommandBlock(context);
            block.setCommand("");
            block.update(true);
            context.getSource().sendMessage(Message.CLEARED_COMMAND_BLOCK);
            return 0;
        })).then(Commands.literal("get").executes(context -> {
            var block = getCommandBlock(context);
            context.getSource().sendMessage(Message.COMMAND_BLOCK_CONTENT, block.getCommand());
            return 0;
        })).then(Commands.literal("give").executes(context -> {
            context.getSource().assertIsEntity().getWorld().dropItem(context.getSource().assertIsEntity().getLocation().add(0, 1, 0), new ItemStack(Material.COMMAND));
            context.getSource().sendMessage(Message.COMMAND_BLOCK_GIVEN);
            return 0;
        })));
    }

    private static CommandBlock getCommandBlock(CommandContext<CommandSource> context) throws CommandSyntaxException {
        var source = context.getSource();
        var player = source.asPlayer();
        var block = player.getTargetBlock((Set<Material>) null, 10);
        if (block.getType() != Material.COMMAND) {
            throw Message.NOT_COMMAND_BLOCK.newSimpleCommandExceptionType().create();
        }
        return (CommandBlock) block.getState();
    }
}

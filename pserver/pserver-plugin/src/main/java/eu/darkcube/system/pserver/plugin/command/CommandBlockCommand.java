/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.MessageArgument;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;
import eu.darkcube.system.pserver.plugin.user.UserManager;

public class CommandBlockCommand extends PServerExecutor {

	public CommandBlockCommand() {
		super("commandblock", new String[0],
						b -> b.then(Commands.literal("add").then(Commands.argument("message", MessageArgument.message()).executes(context -> {
							String text = MessageArgument.getMessage(context, "message");
							CommandBlock block = getCommandBlock(context);
							block.setCommand(block.getCommand() + text);
							block.update(true);
							context.getSource().sendFeedback(Message.COMMAND_BLOCK_CONTENT.getMessage(context.getSource(), block.getCommand()), true);
							return 0;
						}))).then(Commands.literal("clear").executes(context -> {
							CommandBlock block = getCommandBlock(context);
							block.setCommand("");
							block.update(true);
							context.getSource().sendFeedback(Message.CLEARED_COMMAND_BLOCK.getMessage(context.getSource()), true);
							return 0;
						})).then(Commands.literal("get").executes(context -> {
							CommandBlock block = getCommandBlock(context);
							context.getSource().sendFeedback(Message.COMMAND_BLOCK_CONTENT.getMessage(context.getSource(), block.getCommand()), true);
							return 0;
						})));
	}

	private static CommandBlock
					getCommandBlock(CommandContext<CommandSource> context)
									throws CommandSyntaxException {
		CommandSource source = context.getSource();
		Player player = source.asPlayer();
		Block block = player.getTargetBlock((Set<Material>) null, 10);
		if (block.getType() != Material.COMMAND) {
			SimpleCommandExceptionType NOT_A_COMMAND = new SimpleCommandExceptionType(
							() -> Message.NOT_COMMAND_BLOCK.getMessageString(UserManager.getInstance().getUser(player)));
			throw NOT_A_COMMAND.create();
		}
		CommandBlock cmd = (CommandBlock) block.getState();
		return cmd;
	}
}

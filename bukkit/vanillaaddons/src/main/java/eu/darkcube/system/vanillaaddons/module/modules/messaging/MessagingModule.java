/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.messaging;

import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.module.Module;
import eu.darkcube.system.vanillaaddons.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class MessagingModule implements Module {
	private static final Message SELF_RECEIVED = new Message("COMMAND_MESSAGE_SELF_RECEIVED");
	private static final Message SELF_SENT = new Message("COMMAND_MESSAGE_SELF_SENT");
	private static final Message MESSAGE = new Message("COMMAND_MESSAGE_MESSAGE");
	private final VanillaAddons addons;
	private final Key key;
	private final CommandReply commandReply;
	private final CommandMessage commandMessage;

	public MessagingModule(VanillaAddons addons) {
		this.addons = addons;
		this.key = new Key(addons, "command_r_last_message_by");
		this.commandMessage = new CommandMessage();
		this.commandReply = new CommandReply();
	}

	@Override
	public void onEnable() {
		CommandAPI.getInstance().register(commandMessage);
		CommandAPI.getInstance().register(commandReply);
	}

	@Override
	public void onDisable() {
		CommandAPI.getInstance().unregister(commandMessage);
		CommandAPI.getInstance().unregister(commandReply);
	}

	private void message(CommandSource source, Collection<Player> targets, String message)
	throws CommandSyntaxException {
		User sender = null;
		if (source.getEntity() != null && source.getEntity() instanceof Player) {
			Player player = source.asPlayer();
			sender = UserAPI.getInstance().getUser(player);
		}
		for (Player target : targets) {
			User user = UserAPI.getInstance().getUser(target);
			user.sendMessage(MESSAGE, source.getName(), PlainTextComponentSerializer.plainText()
					.serialize(SELF_RECEIVED.getMessage(user)), message);
			target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.5F, 2.0F);
			source.sendMessage(MESSAGE, PlainTextComponentSerializer.plainText()
							.serialize(SELF_SENT.getMessage(source.getSource())), target.getName(),
					message);
			if (sender != null) {
				user.getMetaDataStorage().set(key, sender.getUniqueId());
			}
		}
		if (sender != null && targets.size() == 1) {
			sender.getMetaDataStorage()
					.set(key, targets.stream().findFirst().orElseThrow().getUniqueId());
		}
	}

	private final class CommandMessage extends CommandExecutor {

		public CommandMessage() {
			super("vanillaaddons", "message", new String[] {"msg"}, b -> b.then(
					Commands.argument("targets", EntityArgument.players())
							.then(Commands.argument("message", StringArgumentType.greedyString())
									.executes(ctx -> {
										Collection<Player> targets =
												EntityArgument.getPlayers(ctx, "targets");
										String message =
												StringArgumentType.getString(ctx, "message");
										message(ctx.getSource(), targets, message);
										return 0;
									}))));
		}
	}

	private final class CommandReply extends CommandExecutor {

		private static final SimpleCommandExceptionType NO_PLAYER_TO_ANSWER =
				new Message("COMMAND_R_NO_PLAYER_TO_ANSWER").newSimpleCommandExceptionType();

		public CommandReply() {
			super("vanillaaddons", "reply", new String[] {"r"}, b -> b.then(
					Commands.argument("message", StringArgumentType.greedyString())
							.executes(ctx -> {
								Player player = ctx.getSource().asPlayer();
								User user = UserAPI.getInstance().getUser(player);
								if (!user.getMetaDataStorage().has(key)) {
									throw NO_PLAYER_TO_ANSWER.create();
								}
								UUID uuid = user.getMetaDataStorage().get(key);
								Player target = Bukkit.getPlayer(uuid);
								if (target == null || !target.isOnline()) {
									user.getMetaDataStorage().remove(key);
									throw NO_PLAYER_TO_ANSWER.create();
								}
								message(ctx.getSource(), Collections.singleton(target),
										StringArgumentType.getString(ctx, "message"));
								return 0;
							})));
		}
	}
}

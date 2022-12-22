/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.function.Function;

public class BukkitCommandExecutor implements ILanguagedCommandExecutor {

	private CommandSender sender;
	private TextComponent[] messagePrefix =
			new CustomComponentBuilder("[").color(ChatColor.DARK_GRAY).append("???")
					.color(ChatColor.GOLD).append("]").color(ChatColor.DARK_GRAY).append(" ")
					.color(ChatColor.GRAY).create();
	private Function<Message, String> messageToStringFunction = Message::getKey;

	public BukkitCommandExecutor(CommandSender sender) {
		this.sender = sender;
		Bukkit.getPluginManager().callEvent(new BukkitCommandExecutorConfigureEvent(this));
	}

	@Override
	public Language getLanguage() {
		if (sender instanceof Player) {
			return UserAPI.getInstance().getUser((Player) sender).getLanguage();
		}
		return Language.DEFAULT;
	}

	@Override
	public void setLanguage(Language language) {
		if (sender instanceof Player) {
			UserAPI.getInstance().getUser((Player) sender).setLanguage(language);
		}
		DarkCubeSystem.getInstance().getLogger().warning("Can't set language of the console!");
	}

	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public String getName() {
		return sender.getName();
	}

	@Override
	public void sendMessage(Consumer<CustomComponentBuilder> messageCreator) {
		CustomComponentBuilder b = new CustomComponentBuilder(this.messagePrefix);
		messageCreator.accept(b);
		if (sender instanceof Player) {
			((Player) sender).spigot().sendMessage(b.create());
		} else {
			sender.sendMessage(TextComponent.toLegacyText(b.create()));
		}
	}

	public CommandSender getSender() {
		return sender;
	}

	@Override
	public TextComponent[] getMessagePrefix() {
		return messagePrefix;
	}

	@Override
	public String getCommandPrefix() {
		return sender instanceof Player ? "/" : ILanguagedCommandExecutor.super.getCommandPrefix();
	}

	@Override
	public void setMessagePrefix(TextComponent[] messagePrefix) {
		this.messagePrefix = messagePrefix;
	}

	@Override
	public Function<Message, String> getMessageToStringFunction() {
		return messageToStringFunction;
	}

	@Override
	public void setMessageToStringFunction(Function<Message, String> messageToStringFunction) {
		this.messageToStringFunction = messageToStringFunction;
	}

	@Override
	public boolean shouldReceiveFeedback() {
		return true;
	}

	@Override
	public boolean shouldReceiveErrors() {
		return true;
	}

	@Override
	public boolean allowLogging() {
		return true;
	}

}

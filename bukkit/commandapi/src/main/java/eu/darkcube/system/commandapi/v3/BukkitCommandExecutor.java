package eu.darkcube.system.commandapi.v3;

import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class BukkitCommandExecutor implements ICommandExecutor {

	private CommandSender sender;
	private TextComponent[] messagePrefix = new CustomComponentBuilder("[").color(ChatColor.DARK_GRAY)
			.append("???")
			.color(ChatColor.GOLD)
			.append("]")
			.color(ChatColor.DARK_GRAY)
			.append(" ")
			.color(ChatColor.GRAY)
			.create();
	private Function<Message, String> messageToStringFunction = msg -> {
		return msg.getKey();
	};

	public BukkitCommandExecutor(CommandSender sender) {
		this.sender = sender;
		Bukkit.getPluginManager().callEvent(new BukkitCommandExecutorConfigureEvent(this));
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
		return sender instanceof Player ? "/" : ICommandExecutor.super.getCommandPrefix();
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

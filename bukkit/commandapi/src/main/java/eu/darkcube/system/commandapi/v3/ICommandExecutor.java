/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.brigadier.suggestion.Suggestion;

import eu.darkcube.system.commandapi.v3.Message.DynamicMessageWrapper;
import eu.darkcube.system.commandapi.v3.Message.SimpleMessageWrapper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public interface ICommandExecutor {

	ICommandExecutor DUMMY = new ICommandExecutor() {
		@Override
		public boolean shouldReceiveFeedback() {
			return false;
		}

		@Override
		public boolean shouldReceiveErrors() {
			return false;
		}

		@Override
		public boolean allowLogging() {
			return false;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public boolean hasPermission(String permission) {
			return false;
		}

		@Override
		public void sendMessage(Consumer<CustomComponentBuilder> messageCreator) {
		}

		@Override
		public void setMessagePrefix(TextComponent[] messagePrefix) {
		}

		@Override
		public TextComponent[] getMessagePrefix() {
			return null;
		}

		@Override
		public void sendMessage(Message message, Object... components) {
		}

		@Override
		public Function<Message, String> getMessageToStringFunction() {
			return null;
		}

		@Override
		public void setMessageToStringFunction(Function<Message, String> function) {
		}
	};

	@Deprecated
	default void sendMessage(String message) {
		this.sendMessage(CustomComponentBuilder.cast(TextComponent.fromLegacyText(message)));
	}

	@Deprecated
	default void sendMessage(String... messages) {
		for (String message : messages)
			this.sendMessage(message);
	}

	default void sendMessage(Consumer<CustomComponentBuilder> prefixModifier, TextComponent... components) {
		this.sendMessage(CustomComponentBuilder.applyPrefixModifier(prefixModifier, components));
	}

	default void sendMessage(TextComponent... components) {
		this.sendMessage(b -> {
		}, components);
	}

	default void sendMessage(com.mojang.brigadier.Message message, Object... components) {
		this.sendMessage(b -> {
		}, message, components);
	}

	default void sendMessage(Consumer<CustomComponentBuilder> prefixModfifier, com.mojang.brigadier.Message message,
			Object... components) {
		if (message instanceof SimpleMessageWrapper) {
			this.sendMessage(prefixModfifier, ((SimpleMessageWrapper) message).getMessage(), components);
		} else if (message instanceof DynamicMessageWrapper) {
			this.sendMessage(prefixModfifier, ((DynamicMessageWrapper) message).getMessage(),
					((DynamicMessageWrapper) message).getComponents());
		} else {
			this.sendMessage(String.format(message.getString(), components));
		}
	}

	default void sendMessage(Message message, Object... components) {
		this.sendMessage(message.apply(this.getMessageToStringFunction(), components));
	}

	default void sendMessage(Consumer<CustomComponentBuilder> prefixModifier, Message message, Object... components) {
		this.sendMessage(message.apply(prefixModifier, this.getMessageToStringFunction(), components));
	}

	void sendMessage(Consumer<CustomComponentBuilder> messageCreator);

	default void sendCompletions(String commandLine, Collection<Suggestion> completions) {
		for (Suggestion completion : completions) {
			String cmd = getCommandPrefix() + getCompletionCommand(commandLine, completion);
			this.sendMessage(b -> {
				b.append(" - ")
						.color(ChatColor.GREEN)
						.append(completion.getText())
						.color(ChatColor.AQUA)
						.event(new ClickEvent(Action.SUGGEST_COMMAND, cmd));
				if (completion.getTooltip() != null && completion.getTooltip().getString() != null) {
					b.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new CustomComponentBuilder(completion.getTooltip().getString()).create()));
				} else {
					b.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new CustomComponentBuilder("Click to insert command!").create()));
				}
			});
		}
	}

	default String getCompletionCommand(String commandLine, Suggestion completion) {
		return completion.apply(commandLine);
	}

	default String getCommandPrefix() {
		return "";
	}

	Function<Message, String> getMessageToStringFunction();

	void setMessageToStringFunction(Function<Message, String> function);

	String getName();

	void setMessagePrefix(TextComponent[] messagePrefix);

	boolean hasPermission(String permission);

	TextComponent[] getMessagePrefix();

	boolean shouldReceiveFeedback();

	boolean shouldReceiveErrors();

	boolean allowLogging();

}

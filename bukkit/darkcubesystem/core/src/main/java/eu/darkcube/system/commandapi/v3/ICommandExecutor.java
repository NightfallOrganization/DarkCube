/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collection;

public interface ICommandExecutor extends Audience {

	//	default void sendMessage(Consumer<CustomComponentBuilder> prefixModfifier,
	//			com.mojang.brigadier.Message message, Object... components) {
	//		if (message instanceof SimpleMessageWrapper) {
	//			this.sendMessage(prefixModfifier, ((SimpleMessageWrapper) message).getMessage(),
	//					components);
	//		} else if (message instanceof DynamicMessageWrapper) {
	//			this.sendMessage(prefixModfifier, ((DynamicMessageWrapper) message).getMessage(),
	//					((DynamicMessageWrapper) message).getComponents());
	//		} else {
	//			this.sendMessage(String.format(message.getString(), components));
	//		}
	//	}

	default void sendMessage(Messages message, Object... components) {
		this.sendMessage(message.getMessage(this, components));
	}

	default void sendCompletions(String commandLine, Collection<Suggestion> completions) {
		for (Suggestion completion : completions) {
			String cmd = getCommandPrefix() + getCompletionCommand(commandLine, completion);
			Component clickable = Component.text(completion.getText()).color(NamedTextColor.AQUA)
					.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));
			if (completion.getTooltip() != null && completion.getTooltip().getString() != null) {
				clickable = clickable.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
						Component.text(completion.getTooltip().getString())));
			} else {
				clickable = clickable.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
						Component.text("Click to insert command!")));
			}
			Component c = Component.text(" - ").color(NamedTextColor.GREEN).append(clickable);
			sendMessage(c);
			//			this.sendMessage(b -> {
			//				b.append(" - ").color(ChatColor.GREEN).append(completion.getText())
			//						.color(ChatColor.AQUA).event(new ClickEvent(Action.SUGGEST_COMMAND, cmd));
			//				if (completion.getTooltip() != null
			//						&& completion.getTooltip().getString() != null) {
			//					b.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new CustomComponentBuilder(
			//							completion.getTooltip().getString()).create()));
			//				} else {
			//					b.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
			//							new CustomComponentBuilder("Click to insert command!").create()));
			//				}
			//			});
		}
	}

	default String getCompletionCommand(String commandLine, Suggestion completion) {
		return completion.apply(commandLine);
	}

	default String getCommandPrefix() {
		return "";
	}
}

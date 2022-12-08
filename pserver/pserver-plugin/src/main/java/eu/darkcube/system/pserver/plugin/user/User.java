/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.user;

import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.ICommandExecutor;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.pserver.plugin.Message;
import net.md_5.bungee.api.chat.TextComponent;

public interface User {

	Language getLanguage();

	ICommandExecutor getCommandExecutor();

	UUID getUUID();

	Player getOnlinePlayer();
	
	JsonObject getExtra();
	
	void setExtra(JsonObject extra);
	
	void saveExtra();
	
	boolean isOnline();

	default void sendMessage(Message message, Object... args) {
		this.sendMessage(message.getMessage(getCommandExecutor(), args));
	}

	default void sendMessage(Consumer<CustomComponentBuilder> messageCreator) {
		getCommandExecutor().sendMessage(messageCreator);
	}

	default void sendMessage(TextComponent... message) {
		getCommandExecutor().sendMessage(message);
	}

}

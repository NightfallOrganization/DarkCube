/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system;

import eu.darkcube.system.commandapi.v3.BukkitCommandExecutor;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.ICommandExecutor;
import eu.darkcube.system.commandapi.v3.ILanguagedCommandExecutor;
import eu.darkcube.system.util.Language;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public interface BaseMessage {

	default String getPrefixModifier() {
		return "";
	}

	String getKey();

	default String getMessageString(CommandSender sender, Object... args) {
		return getMessageString(new BukkitCommandExecutor(sender), args);
	}

	default TextComponent[] getMessage(CommandSender sender, Object... args) {
		return getMessage(new BukkitCommandExecutor(sender), args);
	}

	default String getMessageString(ICommandExecutor executor, Object... args) {
		return getMessageString(executor, new String[0], args);
	}

	default String getMessageString(ICommandExecutor executor, String[] prefixes, Object... args) {
		Language language = Language.DEFAULT;
		if (executor instanceof ILanguagedCommandExecutor) {
			language = ((ILanguagedCommandExecutor) executor).getLanguage();
		}
		return language.getMessage(getPrefixModifier() + String.join("", prefixes) + getKey(),
				args);
	}

	default TextComponent[] getMessage(ICommandExecutor executor, Object... args) {
		return CustomComponentBuilder.cast(
				TextComponent.fromLegacyText(getMessageString(executor, args)));
	}
}

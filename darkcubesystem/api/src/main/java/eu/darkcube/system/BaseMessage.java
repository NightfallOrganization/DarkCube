/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system;

import eu.darkcube.system.commandapi.v3.BukkitCommandExecutor;
import eu.darkcube.system.commandapi.v3.ICommandExecutor;
import eu.darkcube.system.commandapi.v3.ILanguagedCommandExecutor;
import eu.darkcube.system.commandapi.v3.Messages.MessageWrapper;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.util.Language;
import org.bukkit.command.CommandSender;

public interface BaseMessage {

	default String getPrefixModifier() {
		return "";
	}

	default MessageWrapper newWrapper(Object... components) {
		return new MessageWrapper(this, components);
	}

	default SimpleCommandExceptionType newSimpleCommandExceptionType() {
		return new SimpleCommandExceptionType(new MessageWrapper(this));
	}

	default DynamicCommandExceptionType newDynamicCommandExceptionType() {
		return new DynamicCommandExceptionType(o -> {
			if (!(o instanceof Object[])) {
				o = new Object[] {o};
			}
			Object[] components = (Object[]) o;
			return new MessageWrapper(this, components);
		});
	}

	String key();

	default Component getMessage(CommandSender sender, Object... args) {
		return getMessage(new BukkitCommandExecutor(sender), args);
	}

	default String getMessageString(ICommandExecutor executor, Object... args) {
		return LegacyComponentSerializer.legacySection()
				.serialize(getMessage(executor, new String[0], args));
	}

	default Component getMessage(ICommandExecutor executor, Object... args) {
		return getMessage(executor, new String[0], args);
	}

	default Component getMessage(Language language, Object... args) {
		return getMessage(language, new String[0], args);
	}

	default Component getMessage(Language language, String[] prefixes, Object... args) {
		return language.getMessage(getPrefixModifier() + String.join("", prefixes) + getKey(),
				args);
	}

	default Component getMessage(ICommandExecutor executor, String[] prefixes, Object... args) {
		Language language = Language.DEFAULT;
		if (executor instanceof ILanguagedCommandExecutor) {
			language = ((ILanguagedCommandExecutor) executor).getLanguage();
		}
		return getMessage(language, prefixes, args);
	}

}

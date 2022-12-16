/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3.arguments;

import java.util.Arrays;
import java.util.Collection;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import eu.darkcube.system.commandapi.v3.CommandSource;

public class MessageArgument implements ArgumentType<MessageArgument.Message> {
	private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

	public static MessageArgument message() {
		return new MessageArgument();
	}

	public static String getMessage(CommandContext<CommandSource> context,
					String name) {
		return context.getArgument(name, MessageArgument.Message.class).getText();
	}

	@Override
	public MessageArgument.Message parse(StringReader reader)
					throws CommandSyntaxException {
		return MessageArgument.Message.parse(reader);
	}

	@Override
	public Collection<String> getExamples() {
		return MessageArgument.EXAMPLES;
	}

	public static class Message {
		private final String text;

		public Message(String textIn) {
			this.text = textIn;
		}

		public String getText() {
			return this.text;
		}

		public static MessageArgument.Message parse(StringReader reader) {
			String s = reader.getString().substring(reader.getCursor(), reader.getTotalLength());
			reader.setCursor(reader.getTotalLength());
			return new Message(s);
		}
	}
}

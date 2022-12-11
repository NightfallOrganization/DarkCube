/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.argument;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Message;

public class MapArgument implements ArgumentType<Map> {
	private static final DynamicCommandExceptionType INVALID_ENUM = Message.INVALID_ENUM.newDynamicCommandExceptionType();

	private MapArgument() {
	}

	public static MapArgument mapArgument() {
		return new MapArgument();
	}

	public static Map getMap(CommandContext<CommandSource> context,
					String name) {
		return context.getArgument(name, Map.class);
	}

	@Override
	public Map parse(StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		String in = reader.readUnquotedString();
		Map map = WoolBattle.getInstance().getMapManager().getMap(in);
		if (map == null) {
			reader.setCursor(cursor);
			throw INVALID_ENUM.createWithContext(reader, in);
		}
		return map;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(
					CommandContext<S> context, SuggestionsBuilder builder) {
		List<String> suggestions = new ArrayList<>();
		for (Map map : maps()) {
			suggestions.add(map.getName());
		}
		return ISuggestionProvider.suggest(suggestions, builder);
	}

	private Map[] maps() {
		return WoolBattle.getInstance().getMapManager().getMaps().toArray(new Map[0]);
	}
}

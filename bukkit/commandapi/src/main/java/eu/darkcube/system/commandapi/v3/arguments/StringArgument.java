package eu.darkcube.system.commandapi.v3.arguments;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;

public class StringArgument implements ArgumentType<String> {

	private final String[] suggestions;

	private StringArgument(String... suggestions) {
		this.suggestions = suggestions;
	}

	public static StringArgument string(String... suggestions) {
		return new StringArgument(suggestions);
	}

	public static String getString(CommandContext<CommandSource> context,
					String name) throws CommandSyntaxException {
		return context.getArgument(name, String.class);
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		return reader.readString();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(
					CommandContext<S> context, SuggestionsBuilder builder) {
		return ISuggestionProvider.suggest(suggestions, builder);
	}
}

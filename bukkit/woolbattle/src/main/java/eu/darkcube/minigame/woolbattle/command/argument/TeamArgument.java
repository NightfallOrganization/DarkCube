package eu.darkcube.minigame.woolbattle.command.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Message;

public class TeamArgument implements ArgumentType<TeamType> {
	private static final DynamicCommandExceptionType INVALID_ENUM = Message.INVALID_ENUM.newDynamicCommandExceptionType();
	private final Predicate<TeamType> filter;
	private final Supplier<TeamType[]> values;
	private final Function<TeamType, String[]> toStringFunction;
	private final Function<String, TeamType> fromStringFunction;

	private TeamArgument(Supplier<TeamType[]> teams, Predicate<TeamType> filter,
					Function<TeamType, String[]> toStringFunction,
					Function<String, TeamType> fromStringFunction) {
		this.values = teams == null ? () -> TeamType.values() : teams;
		this.filter = filter == null ? t -> true : filter;
		this.toStringFunction = toStringFunction == null
						? defaultToStringFunction()
						: toStringFunction;
		this.fromStringFunction = fromStringFunction == null
						? defaultFromStringFunction()
						: fromStringFunction;
	}

	public static TeamArgument teamArgument() {
		return new TeamArgument(null, null, null, null);
	}

	public static TeamArgument teamArgument(Predicate<TeamType> filter) {
		return new TeamArgument(null, filter, null, null);
	}

	public static TeamType getTeam(CommandContext<CommandSource> context,
					String name) {
		return context.getArgument(name, TeamType.class);
	}

	private TeamType[] values() {
		return Arrays.asList(values.get()).stream().filter(filter).collect(Collectors.toList()).toArray(new TeamType[0]);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(
					CommandContext<S> context, SuggestionsBuilder builder) {
		List<String> suggestions = new ArrayList<>();
		for (TeamType t : values()) {
			suggestions.addAll(Arrays.asList(toStringFunction.apply(t)));
		}
		return ISuggestionProvider.suggest(suggestions, builder);
	}

	@Override
	public TeamType parse(StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		String in = reader.readUnquotedString();
		TeamType type = fromStringFunction.apply(in);
		if (type == null) {
			reader.setCursor(cursor);
			throw INVALID_ENUM.createWithContext(reader, in);
		}
		return type;
	}

	private final Function<String, TeamType> defaultFromStringFunction() {
		final Map<String, TeamType> map = new HashMap<>();
		for (TeamType t : values()) {
			String[] arr = toStringFunction.apply(t);
			for (String s : arr) {
				if (map.containsKey(s)) {
					System.out.println("[TeamArgument] Ambiguous name: " + s);
				} else {
					map.put(s, t);
				}
			}
		}
		return map::get;
	}

	private final Function<TeamType, String[]> defaultToStringFunction() {
		final Map<TeamType, String[]> map = new HashMap<>();
		for (TeamType t : values()) {
			map.put(t, new String[] {
							t.getDisplayNameKey()
			});
		}
		return map::get;
	}
}

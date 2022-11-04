package eu.darkcube.minigame.woolbattle.command.argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Message;

public class PerkArgument implements ArgumentType<PerkTypeSelector> {
	private static final DynamicCommandExceptionType INVALID_ENUM = Message.INVALID_ENUM
			.newDynamicCommandExceptionType();
	private final Supplier<PerkType[]> values;
	private final Function<PerkType, String[]> toStringFunction;
	private final Function<String, PerkType> fromStringFunction;

	private PerkArgument(Supplier<PerkType[]> teams, Predicate<PerkType> filter,
			Function<PerkType, String[]> toStringFunction, Function<String, PerkType> fromStringFunction,
			boolean single) {
		this.values = teams == null ? () -> PerkType.values() : teams;
		this.toStringFunction = toStringFunction == null ? defaultToStringFunction() : toStringFunction;
		this.fromStringFunction = fromStringFunction == null ? defaultFromStringFunction() : fromStringFunction;
	}

	public static PerkArgument perkArgument() {
		return new PerkArgument(null, null, null, null, true);
	}

	public static PerkArgument perkArgument(Predicate<PerkType> filter) {
		return new PerkArgument(null, filter, null, null, true);
	}

	public static Collection<PerkType> getPerkTypes(CommandContext<CommandSource> context, String name) {
		return context.getArgument(name, PerkTypeSelector.class).select();
	}

	private PerkType[] values() {
		return values.get();
	}

	public Function<PerkType, String[]> getToStringFunction() {
		return toStringFunction;
	}

	public Function<String, PerkType> getFromStringFunction() {
		return fromStringFunction;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		List<String> suggestions = new ArrayList<>();
		for (PerkType t : values()) {
			suggestions.addAll(Arrays.asList(toStringFunction.apply(t)));
		}
		suggestions.add("*");
		return ISuggestionProvider.suggest(suggestions, builder);
	}

	@Override
	public PerkTypeSelector parse(StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		reader.skipWhitespace();
		String in = read(reader);
		if (in.equals("*")) {
			return new PerkTypeSelector(true, null);
		}
		PerkType type = fromStringFunction.apply(in);
		if (type == null) {
			reader.setCursor(cursor);
			throw INVALID_ENUM.createWithContext(reader, in);
		}
		return new PerkTypeSelector(false, type);
	}

	private String read(StringReader reader) {
		char c = ' ';
		StringBuilder b = new StringBuilder();
		while (reader.canRead() && reader.peek() != c) {
			b.append(reader.peek());
			reader.skip();
		}
		return b.toString();
	}

	private final Function<String, PerkType> defaultFromStringFunction() {
		final Map<String, PerkType> map = new HashMap<>();
		for (PerkType t : values()) {
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

	private final Function<PerkType, String[]> defaultToStringFunction() {
		final Map<PerkType, String[]> map = new HashMap<>();
		for (PerkType t : values()) {
			map.put(t, new String[] { t.getPerkName().getName() });
		}
		return map::get;
	}
}

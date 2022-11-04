package eu.darkcube.system.commandapi.v3.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Message;

public class EnumArgument<T extends Enum<?>> implements ArgumentType<T> {

	private static final DynamicCommandExceptionType INVALID_ENUM = Message.INVALID_ENUM.newDynamicCommandExceptionType();

	private final Function<T, String[]> toStringFunction;
	private final Function<String, T> fromStringFunction;

	private final T[] values;

	private EnumArgument(T[] values) {
		this(values, null, null);
	}

	private EnumArgument(T[] values, Function<T, String[]> toStringFunction,
					Function<String, T> fromStringFunction) {
		this.values = values;
		this.toStringFunction = toStringFunction == null
						? defaultToStringFunction()
						: toStringFunction;
		this.fromStringFunction = fromStringFunction == null
						? defaultFromStringFunction()
						: fromStringFunction;
	}

	private final Function<String, T> defaultFromStringFunction() {
		final Map<String, T> map = new HashMap<>();
		for (T t : values) {
			String[] arr = toStringFunction.apply(t);
			for (String s : arr) {
				if (map.containsKey(s)) {
					System.out.println("[EnumArgument] Ambiguous name: " + s);
				} else {
					map.put(s, t);
				}
			}
		}
		return map::get;
	}

	private final Function<T, String[]> defaultToStringFunction() {
		final Map<T, String[]> map = new HashMap<>();
		for (T t : values) {
			map.put(t, new String[] {
							t.name()
			});
		}
		return map::get;
	}

	public static <T extends Enum<?>> T getEnumArgument(
					CommandContext<CommandSource> context, String name,
					Class<T> enumClass) {
		return context.getArgument(name, enumClass);
	}

	public static <T extends Enum<?>> EnumArgument<T> enumArgument(T[] values) {
		return new EnumArgument<>(values);
	}

	public static <T extends Enum<?>> EnumArgument<T> enumArgument(T[] values,
					Function<T, String[]> toStringFunction) {
		return new EnumArgument<>(values, toStringFunction, null);
	}

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		String in = reader.readUnquotedString();
		T type = fromStringFunction.apply(in);
		if (type == null) {
			reader.setCursor(cursor);
			throw INVALID_ENUM.createWithContext(reader, in);
		}
		return type;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(
					CommandContext<S> context, SuggestionsBuilder builder) {
		List<String> suggestions = new ArrayList<>();
		for (T t : values) {
			suggestions.addAll(Arrays.asList(toStringFunction.apply(t)));
		}
		return ISuggestionProvider.suggest(suggestions, builder);
	}
}
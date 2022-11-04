package eu.darkcube.system.commandapi.v3.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Message;

public class RotationArgument implements ArgumentType<ILocationArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
	public static final SimpleCommandExceptionType ROTATION_INCOMPLETE = Message.ROTATION_INCOMPLETE
			.newSimpleCommandExceptionType();

	public static RotationArgument rotation() {
		return new RotationArgument();
	}

	public static ILocationArgument getRotation(CommandContext<CommandSource> context, String name) {
		return context.getArgument(name, ILocationArgument.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> source, SuggestionsBuilder builder) {
		if (!(source.getSource() instanceof ISuggestionProvider)) {
			return Suggestions.empty();
		} else {
			String s = builder.getRemaining();
			Collection<ISuggestionProvider.Coordinates> collection;
			if (!s.isEmpty() && s.charAt(0) == '^') {
				collection = Collections.emptyList();
			} else {
				collection = ((ISuggestionProvider) source.getSource()).getCoordinates();
			}

			return ISuggestionProvider.suggestVec2(s, collection, builder, Commands.predicate(this::parse));
		}
	}

	@Override
	public ILocationArgument parse(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();
		if (!reader.canRead()) {
			throw ROTATION_INCOMPLETE.createWithContext(reader);
		} else {
			LocationPart yaw = LocationPart.parseDouble(reader, false);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				LocationPart pitch = LocationPart.parseDouble(reader, false);
				return new LocationInput(yaw, pitch, new LocationPart(true, 0.0D));
			} else {
				reader.setCursor(i);
				throw ROTATION_INCOMPLETE.createWithContext(reader);
			}
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}

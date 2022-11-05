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
import eu.darkcube.system.commandapi.v3.Vector3d;

public class Vec3Argument implements ArgumentType<ILocationArgument> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5",
			"0.1 -0.5 .9", "~0.5 ~1 ~-5");
	public static final SimpleCommandExceptionType POS_INCOMPLETE = Message.POS_INCOMPLETE
			.newSimpleCommandExceptionType();
	public static final SimpleCommandExceptionType POS_MIXED_TYPES = Message.POS_MIXED_TYPES
			.newSimpleCommandExceptionType();
	private final boolean centerIntegers;

	public Vec3Argument(boolean centerIntegersIn) {
		this.centerIntegers = centerIntegersIn;
	}

	public static Vec3Argument vec3() {
		return new Vec3Argument(true);
	}

	public static Vec3Argument vec3(boolean centerIntegersIn) {
		return new Vec3Argument(centerIntegersIn);
	}

	public static Vector3d getVec3(CommandContext<CommandSource> context, String name) {
		return context.getArgument(name, ILocationArgument.class).getPosition(context.getSource());
	}

	public static ILocationArgument getLocation(CommandContext<CommandSource> context, String name) {
		return context.getArgument(name, ILocationArgument.class);
	}

	@Override
	public ILocationArgument parse(StringReader reader) throws CommandSyntaxException {
		return reader.canRead() && reader.peek() == '^' ? LocalLocationArgument.parse(reader)
				: LocationInput.parseDouble(reader, this.centerIntegers);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> source,
			SuggestionsBuilder builder) {
		if (!(source.getSource() instanceof ISuggestionProvider)) {
			return Suggestions.empty();
		}
		String s = builder.getRemaining();
		Collection<ISuggestionProvider.Coordinates> collection;
		if (!s.isEmpty() && s.charAt(0) == '^') {
			collection = Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_LOCAL);
		} else {
			collection = ((ISuggestionProvider) source.getSource()).getCoordinates();
		}
		return ISuggestionProvider.suggestVec3(s, collection, builder, Commands.predicate(this::parse));
	}

	@Override
	public Collection<String> getExamples() {
		return Vec3Argument.EXAMPLES;
	}
}

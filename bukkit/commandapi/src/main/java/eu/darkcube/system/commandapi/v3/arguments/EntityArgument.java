package eu.darkcube.system.commandapi.v3.arguments;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Message;

public class EntityArgument implements ArgumentType<EntitySelector> {

	public static final SimpleCommandExceptionType SELECTOR_NOT_ALLOWED = Message.SELECTOR_NOT_ALLOWED.newSimpleCommandExceptionType();
	public static final SimpleCommandExceptionType TOO_MANY_ENTITIES = Message.TOO_MANY_ENTITIES.newSimpleCommandExceptionType();
	public static final SimpleCommandExceptionType TOO_MANY_PLAYERS = Message.TOO_MANY_PLAYERS.newSimpleCommandExceptionType();
	public static final SimpleCommandExceptionType ONLY_PLAYERS_ALLOWED = Message.ONLY_PLAYERS_ALLOWED.newSimpleCommandExceptionType();
	public static final SimpleCommandExceptionType ENTITY_NOT_FOUND = Message.ENTITY_NOT_FOUND.newSimpleCommandExceptionType();
	public static final SimpleCommandExceptionType PLAYER_NOT_FOUND = Message.PLAYER_NOT_FOUND.newSimpleCommandExceptionType();

	private final boolean single, playersOnly;

	public EntityArgument(boolean singleIn, boolean playersOnlyIn) {
		this.single = singleIn;
		this.playersOnly = playersOnlyIn;
	}

	@Override
	public EntitySelector parse(StringReader reader)
					throws CommandSyntaxException {
		EntitySelectorParser entityselectorparser = new EntitySelectorParser(
						reader);
		EntitySelector entityselector = entityselectorparser.parse();
		if (entityselector.getLimit() > 1 && this.single) {
			if (this.playersOnly) {
				reader.setCursor(0);
				throw TOO_MANY_PLAYERS.createWithContext(reader);
			} else {
				reader.setCursor(0);
				throw TOO_MANY_ENTITIES.createWithContext(reader);
			}
		} else if (entityselector.includesEntities() && this.playersOnly
						&& !entityselector.isSelfSelector()) {
			reader.setCursor(0);
			throw ONLY_PLAYERS_ALLOWED.createWithContext(reader);
		} else {
			return entityselector;
		}
	}

	public static Entity getEntity(CommandContext<CommandSource> context,
					String name) throws CommandSyntaxException {
		return context.getArgument(name, EntitySelector.class).selectOne(context.getSource());
	}

	public static Collection<? extends Entity> getEntities(
					CommandContext<CommandSource> context, String name)
					throws CommandSyntaxException {
		Collection<? extends Entity> collection = getEntitiesAllowingNone(context, name);
		if (collection.isEmpty()) {
			throw ENTITY_NOT_FOUND.create();
		} else {
			return collection;
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(
					CommandContext<S> context, SuggestionsBuilder builder) {
		if (context.getSource() instanceof ISuggestionProvider) {
			StringReader reader = new StringReader(builder.getInput());
			reader.setCursor(builder.getStart());
			ISuggestionProvider suggestionProvider = (ISuggestionProvider) context.getSource();
			EntitySelectorParser parser = new EntitySelectorParser(reader,
							true);

			try {
				parser.parse();
			} catch (CommandSyntaxException commandsyntaxexception) {
			}

			return parser.fillSuggestions(builder, sbuilder -> {
				Collection<String> collection = suggestionProvider.getPlayerNames();
				Iterable<String> iterable = this.playersOnly ? collection
								: Iterables.concat(collection, suggestionProvider.getTargetedEntity());
				ISuggestionProvider.suggest(iterable, sbuilder);
			});
		} else {
			return Suggestions.empty();
		}
	}

	public static Player getPlayer(CommandContext<CommandSource> context,
					String name) throws CommandSyntaxException {
		return context.getArgument(name, EntitySelector.class).selectOnePlayer(context.getSource());
	}

	public static Collection<Player> getPlayers(
					CommandContext<CommandSource> context, String name)
					throws CommandSyntaxException {
		Collection<Player> list = getPlayersAllowingNone(context, name);
		if (list.isEmpty()) {
			throw PLAYER_NOT_FOUND.create();
		} else {
			return list;
		}
	}

	public static Collection<? extends Entity> getEntitiesAllowingNone(
					CommandContext<CommandSource> context, String name)
					throws CommandSyntaxException {
		return context.getArgument(name, EntitySelector.class).select(context.getSource());
	}

	public static Collection<Player> getPlayersAllowingNone(
					CommandContext<CommandSource> context, String name)
					throws CommandSyntaxException {
		return context.getArgument(name, EntitySelector.class).selectPlayers(context.getSource());
	}

	public static EntityArgument entity() {
		return new EntityArgument(true, false);
	}

	public static EntityArgument entities() {
		return new EntityArgument(false, false);
	}

	public static EntityArgument player() {
		return new EntityArgument(true, true);
	}

	public static EntityArgument players() {
		return new EntityArgument(false, true);
	}
}
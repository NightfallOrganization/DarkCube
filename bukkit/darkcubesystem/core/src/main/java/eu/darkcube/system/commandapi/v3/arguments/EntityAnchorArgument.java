/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3.arguments;

import com.google.common.collect.Maps;
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
import eu.darkcube.system.commandapi.v3.Vector3d;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class EntityAnchorArgument
				implements ArgumentType<EntityAnchorArgument.Type> {
	private static final Collection<String> EXMAPLES = Arrays.asList("eyes", "feet");
	private static final DynamicCommandExceptionType ANCHOR_INVALID = Message.ANCHOR_INVALID.newDynamicCommandExceptionType();

	public static Type getEntityAnchor(
					CommandContext<CommandSource> context, String name) {
		return context.getArgument(name, Type.class);
	}

	public static EntityAnchorArgument entityAnchor() {
		return new EntityAnchorArgument();
	}

	@Override
	public Type parse(StringReader reader)
					throws CommandSyntaxException {
		int i = reader.getCursor();
		String s = reader.readUnquotedString();
		Type entityanchorargument$type = Type.getByName(s);
		if (entityanchorargument$type == null) {
			reader.setCursor(i);
			throw EntityAnchorArgument.ANCHOR_INVALID.createWithContext(reader, s);
		}
		return entityanchorargument$type;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(
					CommandContext<S> context, SuggestionsBuilder builder) {
		return ISuggestionProvider.suggest(Type.BY_NAME.keySet(), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EntityAnchorArgument.EXMAPLES;
	}

	public static enum Type {
		FEET("feet", (pos, entity) -> {
			return pos;
		}),
		EYES("eyes", (pos, entity) -> {
			return new Vector3d(pos.x, pos.y + (entity instanceof LivingEntity
							? ((LivingEntity) entity).getEyeHeight()
							: 0), pos.z);
		});

		private static final Map<String, Type> BY_NAME = Maps.newHashMap();
		static {
			for (Type type : Type.values()) {
				Type.BY_NAME.put(type.name, type);
			}
		}
		private final String name;
		private final BiFunction<Vector3d, Entity, Vector3d> offsetFunc;

		private Type(String nameIn,
						BiFunction<Vector3d, Entity, Vector3d> offsetFuncIn) {
			this.name = nameIn;
			this.offsetFunc = offsetFuncIn;
		}

		public static Type getByName(String nameIn) {
			return Type.BY_NAME.get(nameIn);
		}

		public Vector3d apply(Entity entityIn) {
			return this.offsetFunc.apply(Vector3d.position(entityIn.getLocation()), entityIn);
		}

		public Vector3d apply(CommandSource sourceIn) {
			Entity entity = sourceIn.getEntity();
			return entity == null ? sourceIn.getPos()
							: this.offsetFunc.apply(sourceIn.getPos(), entity);
		}
	}
}

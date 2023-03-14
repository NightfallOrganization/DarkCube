/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.argument;

import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.system.commandapi.v3.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public class CooldownArgument implements ArgumentType<Cooldown> {
	private static final DynamicCommandExceptionType INVALID_ENUM =
			Messages.INVALID_ENUM.newDynamicCommandExceptionType();

	private final Unit forcedUnit;

	public CooldownArgument() {
		this(null);
	}

	public CooldownArgument(Unit forcedUnit) {
		this.forcedUnit = forcedUnit;
	}

	@Override
	public Cooldown parse(StringReader reader) throws CommandSyntaxException {
		int cd = reader.readInt();
		Unit unit;
		String in = reader.readUnquotedString();
		try {
			unit = Unit.valueOf(in);
			if (forcedUnit != null && unit != forcedUnit) {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException ignored) {
			throw INVALID_ENUM.createWithContext(reader, in);
		}
		return new Cooldown(unit, cd);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
			SuggestionsBuilder builder) {
		return ArgumentType.super.listSuggestions(context, builder);
	}
}

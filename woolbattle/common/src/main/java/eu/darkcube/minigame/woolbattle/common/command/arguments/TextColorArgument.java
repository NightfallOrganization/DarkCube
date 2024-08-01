package eu.darkcube.minigame.woolbattle.common.command.arguments;

import static eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor.NAMES;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;

class TextColorArgument implements ArgumentType<TextColor> {
    private static final DynamicCommandExceptionType INVALID_COLOR = Messages.INVALID_NAME_COLOR.newDynamicCommandExceptionType();

    public static TextColorArgument color() {
        return new TextColorArgument();
    }

    public static TextColor getColor(CommandContext<?> ctx, String name) {
        return ctx.getArgument(name, TextColor.class);
    }

    @Override
    public TextColor parse(StringReader reader) throws CommandSyntaxException {
        var input = reader.readString();
        var lowercaseInput = input.toLowerCase(Locale.ROOT);
        {
            var color = NAMES.value(lowercaseInput);
            if (color != null) return color;
        }
        {
            var color = TextColor.fromCSSHexString(input);
            if (color != null) return color;
        }
        throw INVALID_COLOR.createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(NAMES.keys(), builder);
    }
}

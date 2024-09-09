package eu.darkcube.minigame.woolbattle.common.command.arguments;

import java.util.concurrent.CompletableFuture;

import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWoolProvider;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

class WoolColorArgument implements ArgumentType<ColoredWool> {
    private static final DynamicCommandExceptionType INVALID_COLOR = Messages.INVALID_WOOL_COLOR.newDynamicCommandExceptionType();
    private final ColoredWoolProvider provider;

    public WoolColorArgument(ColoredWoolProvider provider) {
        this.provider = provider;
    }

    public static WoolColorArgument woolColor(@NotNull ColoredWoolProvider provider) {
        return new WoolColorArgument(provider);
    }

    public static ColoredWool getWoolColor(CommandContext<?> ctx, String name) {
        return ctx.getArgument(name, ColoredWool.class);
    }

    @Override
    public ColoredWool parse(StringReader reader) throws CommandSyntaxException {
        var cursor = reader.getCursor();
        var input = reader.readString();
        for (var woolColor : provider.woolColors()) {
            if (woolColor.name().equalsIgnoreCase(input)) {
                return woolColor;
            }
        }
        reader.setCursor(cursor);
        throw INVALID_COLOR.createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(provider.woolColors().stream().map(ColoredWool::name), builder);
    }
}

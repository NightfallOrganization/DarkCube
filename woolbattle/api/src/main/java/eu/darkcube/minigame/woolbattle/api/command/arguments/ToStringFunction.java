package eu.darkcube.minigame.woolbattle.api.command.arguments;

import java.util.function.Function;

import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;

public interface ToStringFunction<Type> {
    static <Type> ToStringFunction<Type> of(Function<Type, String> simpleFunction) {
        return (_, o) -> new String[]{simpleFunction.apply(o)};
    }

    String[] toString(CommandContext<?> ctx, Type type) throws CommandSyntaxException;
}

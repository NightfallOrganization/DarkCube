package eu.darkcube.minigame.woolbattle.api.command.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public interface FromStringFunction<Type> {
    static <Type> FromStringFunction<Type> of(@NotNull ToStringFunction<@NotNull Type> toStringFunction, @NotNull Supplier<@NotNull Collection<Type>> supplier) {
        return (ctx, string) -> {
            var data = supplier.get();
            for (var element : data) {
                var array = toStringFunction.toString(ctx, element);
                if (Arrays.asList(array).contains(string)) {
                    return element;
                }
            }
            return null;
        };
    }

    @Nullable
    Type fromString(CommandContext<?> ctx, String string) throws CommandSyntaxException;
}

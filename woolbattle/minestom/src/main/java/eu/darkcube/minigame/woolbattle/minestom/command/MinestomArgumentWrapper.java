package eu.darkcube.minigame.woolbattle.minestom.command;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinestomArgumentWrapper extends Argument<String> {
    public MinestomArgumentWrapper(@NotNull String id) {
        super(id, true, true);
    }

    @Override
    public @NotNull String parse(@NotNull CommandSender sender, @NotNull String input) throws ArgumentSyntaxException {
        return input;
    }

    @Override
    public String parser() {
        return "brigadier:string";
    }

    @Override
    public byte @Nullable [] nodeProperties() {
        return BinaryWriter.makeArray(packetWriter -> {
            packetWriter.writeVarInt(2); // Greedy phrase
        });
    }
}

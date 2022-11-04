package eu.darkcube.system.pserver.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;

import com.mojang.brigadier.context.CommandContext;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EnumArgument;
import eu.darkcube.system.commandapi.v3.arguments.WorldArgument;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class DifficultyCommand extends PServerExecutor {

	public DifficultyCommand() {
		super("difficulty", new String[0],
						b -> b.then(Commands.argument("difficulty", EnumArgument.enumArgument(Difficulty.values(), s -> new String[] {
										s.name().toLowerCase()
						})).executes(context -> {
							return difficulty(context, EnumArgument.getEnumArgument(context, "difficulty", Difficulty.class), null);
						}).then(Commands.argument("world", WorldArgument.world()).executes(context -> {
							return difficulty(context, EnumArgument.getEnumArgument(context, "difficulty", Difficulty.class), WorldArgument.getWorld(context, "world"));
						}))));
	}

	private static int difficulty(CommandContext<CommandSource> context,
					Difficulty difficulty, World world) {
		if (world == null) {
			Bukkit.getWorlds().forEach(w -> {
				difficulty(context, difficulty, w);
			});
			return 0;
		}
		world.setDifficulty(difficulty);
		context.getSource().sendFeedback(Message.CHANGED_DIFFICULTY_FOR_WORLD.getMessage(context.getSource(), world.getName(), difficulty.name().toLowerCase()), true);
		return 0;
	}
}
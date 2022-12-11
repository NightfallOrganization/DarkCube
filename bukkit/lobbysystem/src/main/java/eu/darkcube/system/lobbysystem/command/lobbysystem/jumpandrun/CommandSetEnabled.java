package eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.arguments.BooleanArgument;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import net.md_5.bungee.api.ChatColor;

import java.util.function.Consumer;

public class CommandSetEnabled extends LobbyCommandExecutor {
	public CommandSetEnabled() {
		super("setEnabled", b -> b.then(
				Commands.argument("enabled", BooleanArgument.booleanArgument()).executes(ctx -> {
					boolean s = BooleanArgument.getBoolean(ctx, "enabled");
					Lobby.getInstance().getDataManager().setJumpAndRunEnabled(s);
					ctx.getSource().sendFeedback(
							new CustomComponentBuilder().append("JumpAndRun Enabled: " + s)
									.color(ChatColor.GOLD).create(), true);
					return 0;
				})));
	}
}

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.arguments.BooleanArgument;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

public class CommandWinter extends LobbyCommandExecutor {
	public CommandWinter() {
		super("winter", b -> b.then(
				Commands.argument("winter", BooleanArgument.booleanArgument()).executes(ctx -> {
					boolean winter = BooleanArgument.getBoolean(ctx, "winter");
					Lobby.getInstance().getDataManager().setWinter(winter);
					ctx.getSource().sendFeedback(
							new CustomComponentBuilder().append("Winter: " + winter)
									.color(ChatColor.GOLD).create(), true);
					return 0;
				})));
	}
}

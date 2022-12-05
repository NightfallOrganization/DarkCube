package eu.darkcube.system.lobbysystem.command.lobbysystem;

import java.util.Collection;
import java.util.Collections;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;
import net.md_5.bungee.api.ChatColor;

public class CommandBuild extends LobbyCommandExecutor {

	public CommandBuild() {
		super("build", b -> b
				.then(Commands.argument("players", EntityArgument.players())
						.executes(ctx -> toggle(ctx, EntityArgument.getPlayers(ctx, "players"))))
				.executes(ctx -> toggle(ctx, Collections.singleton(ctx.getSource().asPlayer()))));
	}

	private static int toggle(CommandContext<CommandSource> ctx, Collection<Player> players)
			throws CommandSyntaxException {
		for (Player t : players) {
			LobbyUser user = UserWrapper.fromUser(UserAPI.getInstance().getUser(t));
			if (user.isBuildMode()) {
				Lobby.getInstance().setupPlayer(user);
				user.setGadget(user.getGadget());
				user.setBuildMode(false);
				Lobby.getInstance().sendMessage("§cNun nicht mehr im §eBau-Modus§c!", t);
				if (!ctx.getSource().assertIsEntity().equals(t)) {
					ctx.getSource().sendFeedback(new CustomComponentBuilder(t.getName())
							.color(ChatColor.GOLD).append(" ist nun nicht mehr im ")
							.color(ChatColor.RED).append("Bau-Modus").color(ChatColor.YELLOW)
							.append("!").color(ChatColor.RED).create(), true);
					// Lobby.getInstance().sendMessage(
					// "§6" + name + "§c ist nun nicht mehr im §eBau-Modus§c!", sender);
				}
			} else {
				Lobby.getInstance().savePlayer(user);
				user.setBuildMode(true);
				t.setGameMode(GameMode.CREATIVE);
				Lobby.getInstance().sendMessage("§aNun im §eBau-Modus§a!", t);
				if (!ctx.getSource().assertIsEntity().equals(t)) {
					ctx.getSource().sendFeedback(new CustomComponentBuilder(t.getName())
							.color(ChatColor.GOLD).append(" ist nun im ").color(ChatColor.GREEN)
							.append("Bau-Modus").color(ChatColor.YELLOW).append("!")
							.color(ChatColor.GREEN).create(), true);
				}
			}
		}
		return 0;
	}

}

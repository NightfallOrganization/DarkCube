package eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun;

import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.jumpandrun.JaRRegion;
import net.md_5.bungee.api.ChatColor;

public class CommandListRegions extends LobbyCommandExecutor {

	public CommandListRegions() {
		super("listRegions", b -> b.executes(ctx -> {
			CustomComponentBuilder ccb =
					new CustomComponentBuilder("Regions: ").color(ChatColor.GRAY)
							.append(Integer.toString(
									Lobby.getInstance().getJaRManager().getRegions().size()))
							.color(ChatColor.GOLD);
			int i = 0;
			for (JaRRegion r : Lobby.getInstance().getJaRManager().getRegions()) {
				ccb.append("\n - " + i + ": ").color(ChatColor.YELLOW).append(r.toString())
						.color(ChatColor.GOLD);
				i++;
			}
			ctx.getSource().sendFeedback(ccb.create(), true);
			return 0;
		}));
	}

}

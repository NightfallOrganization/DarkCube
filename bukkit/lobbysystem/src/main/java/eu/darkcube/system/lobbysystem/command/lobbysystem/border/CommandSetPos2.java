package eu.darkcube.system.lobbysystem.command.lobbysystem.border;

import com.mojang.brigadier.context.CommandContext;
import org.bukkit.Location;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.arguments.BooleanArgument;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.parser.Locations;
import eu.darkcube.system.lobbysystem.util.Border;
import eu.darkcube.system.lobbysystem.util.DataManager;
import net.md_5.bungee.api.ChatColor;

public class CommandSetPos2 extends LobbyCommandExecutor {

	public CommandSetPos2() {
		super("setPos2",
				b -> b.executes(ctx -> cmd(ctx, ctx.getSource().asPlayer().getLocation()))
						.then(Commands.argument("makenice", BooleanArgument.booleanArgument())
								.executes(ctx -> cmd(ctx,
										BooleanArgument.getBoolean(ctx, "makenice")
												? Locations.getNiceLocation(
														ctx.getSource().asPlayer().getLocation())
												: ctx.getSource().asPlayer().getLocation()))));
	}

	private static int cmd(CommandContext<CommandSource> ctx, Location loc) {
		DataManager data = Lobby.getInstance().getDataManager();
		Border border = data.getBorder();
		border = new Border(border.getShape(), border.getRadius(), border.getLoc1(), loc);
		data.setBorder(border);
		ctx.getSource().sendFeedback(
				new CustomComponentBuilder("Position 2 gesetzt!").color(ChatColor.GREEN).create(),
				true);
		return 0;
	}

}

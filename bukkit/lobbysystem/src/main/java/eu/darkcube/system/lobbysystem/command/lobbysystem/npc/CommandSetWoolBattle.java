package eu.darkcube.system.lobbysystem.command.lobbysystem.npc;

import com.mojang.brigadier.context.CommandContext;
import org.bukkit.Location;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.Vector2f;
import eu.darkcube.system.commandapi.v3.Vector3d;
import eu.darkcube.system.commandapi.v3.arguments.BooleanArgument;
import eu.darkcube.system.commandapi.v3.arguments.ILocationArgument;
import eu.darkcube.system.commandapi.v3.arguments.RotationArgument;
import eu.darkcube.system.commandapi.v3.arguments.Vec3Argument;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.parser.Locations;
import net.md_5.bungee.api.ChatColor;

public class CommandSetWoolBattle extends LobbyCommandExecutor {

	public CommandSetWoolBattle() {
		super("setWoolBattle", b -> {
			b.then(Commands.argument("location", Vec3Argument.vec3()).executes(
					ctx -> setSpawn(ctx, Vec3Argument.getLocation(ctx, "location"), null, false))
					.then(Commands.argument("rotation", RotationArgument.rotation())
							.executes(
									ctx -> setSpawn(ctx, Vec3Argument.getLocation(ctx, "location"),
											RotationArgument.getRotation(ctx, "rotation"), false))
							.then(Commands.argument("makenice", BooleanArgument.booleanArgument())
									.executes(ctx -> setSpawn(ctx,
											Vec3Argument.getLocation(ctx, "location"),
											RotationArgument.getRotation(ctx, "rotation"),
											BooleanArgument.getBoolean(ctx, "makenice")))))
					.then(Commands.argument("makenice", BooleanArgument.booleanArgument()).executes(
							ctx -> setSpawn(ctx, Vec3Argument.getLocation(ctx, "location"), null,
									BooleanArgument.getBoolean(ctx, "makenice")))))
					.then(Commands.argument("makenice", BooleanArgument.booleanArgument())
							.executes(ctx -> setSpawn(ctx, null, null,
									BooleanArgument.getBoolean(ctx, "makenice"))));
		});
	}

	private static int setSpawn(CommandContext<CommandSource> context, ILocationArgument location,
			ILocationArgument rotation, boolean makenice) {
		Vector3d pos = location == null ? pos(context.getSource().getEntity().getLocation())
				: location.getPosition(context.getSource());
		Vector2f rot = rotation == null ? rot(context.getSource().getEntity().getLocation())
				: location.getRotation(context.getSource());
		Location loc = new Location(context.getSource().getEntity().getWorld(), pos.x, pos.y, pos.z,
				rot.x, rot.y);
		if (makenice) {
			loc = Locations.getNiceLocation(loc);
			context.getSource().getEntity().teleport(loc);
		}
		Lobby.getInstance().getDataManager().setWoolBattleNPCLocation(loc);
		context.getSource().sendFeedback(
				new CustomComponentBuilder("NPC neugesetzt!").color(ChatColor.GREEN).create(),
				true);
		return 0;
	}

	private static Vector2f rot(Location l) {
		return new Vector2f(l.getYaw(), l.getPitch());
	}

	private static Vector3d pos(Location l) {
		return new Vector3d(l.getX(), l.getY(), l.getZ());
	}
}

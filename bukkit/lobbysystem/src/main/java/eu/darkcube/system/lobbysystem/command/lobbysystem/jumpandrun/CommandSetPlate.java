/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.Vector2f;
import eu.darkcube.system.commandapi.v3.Vector3d;
import eu.darkcube.system.commandapi.v3.arguments.BooleanArgument;
import eu.darkcube.system.commandapi.v3.arguments.ILocationArgument;
import eu.darkcube.system.commandapi.v3.arguments.RotationArgument;
import eu.darkcube.system.commandapi.v3.arguments.Vec3Argument;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.parser.Locations;
import org.bukkit.Location;
import org.bukkit.Material;

public class CommandSetPlate extends LobbyCommandExecutor {

	public CommandSetPlate() {
		super("setPlate", b -> {
			b.then(Commands.argument("location", Vec3Argument.vec3()).executes(
									ctx -> setPlate(ctx, Vec3Argument.getLocation(ctx, "location"), null, false))
							.then(Commands.argument("rotation", RotationArgument.rotation()).executes(
											ctx -> setPlate(ctx, Vec3Argument.getLocation(ctx, "location"),
													RotationArgument.getRotation(ctx, "rotation"), false))
									.then(Commands.argument("makenice", BooleanArgument.booleanArgument())
											.executes(ctx -> setPlate(ctx,
													Vec3Argument.getLocation(ctx, "location"),
													RotationArgument.getRotation(ctx, "rotation"),
													BooleanArgument.getBoolean(ctx, "makenice")))))
							.then(Commands.argument("makenice", BooleanArgument.booleanArgument()).executes(
									ctx -> setPlate(ctx, Vec3Argument.getLocation(ctx, "location"), null,
											BooleanArgument.getBoolean(ctx, "makenice")))))
					.then(Commands.argument("makenice", BooleanArgument.booleanArgument()).executes(
							ctx -> setPlate(ctx, null, null,
									BooleanArgument.getBoolean(ctx, "makenice"))));
		});
	}

	private static int setPlate(CommandContext<CommandSource> context, ILocationArgument location,
			ILocationArgument rotation, boolean makenice) {
		Vector3d pos = location == null
				? pos(context.getSource().getEntity().getLocation())
				: location.getPosition(context.getSource());
		Vector2f rot = rotation == null
				? rot(context.getSource().getEntity().getLocation())
				: location.getRotation(context.getSource());
		Location loc =
				new Location(context.getSource().getEntity().getWorld(), pos.x, pos.y, pos.z, rot.x,
						rot.y);
		if (makenice) {
			loc = Locations.getNiceLocation(loc);
			context.getSource().getEntity().teleport(loc);
		}
		Material m = loc.getBlock().getType();
		if (m == Material.IRON_PLATE || m == Material.GOLD_PLATE || m == Material.WOOD_PLATE
				|| m == Material.STONE_PLATE) {
			Lobby.getInstance().getDataManager().setJumpAndRunPlate(loc.getBlock().getLocation());
			context.getSource()
					.sendMessage(Component.text("Platte neugesetzt!").color(NamedTextColor.GREEN));
			return 0;
		}
		context.getSource().sendMessage(
				Component.text("Dieser Block is keine Druckplatte!").color(NamedTextColor.RED));
		return 0;
	}

	private static Vector2f rot(Location l) {
		return new Vector2f(l.getYaw(), l.getPitch());
	}

	private static Vector3d pos(Location l) {
		return new Vector3d(l.getX(), l.getY(), l.getZ());
	}
}

/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.util.MaterialAndId;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EnumArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public class CommandSetIcon extends WBCommandExecutor {
    public CommandSetIcon() {
        super("setIcon", b -> b.then(
                Commands.argument("material", EnumArgument.enumArgument(Material.values()))
                        .executes(ctx -> setIcon(ctx,
                                EnumArgument.getEnumArgument(ctx, "material", Material.class), 0))
                        .then(Commands.argument("id", IntegerArgumentType.integer(0)).executes(
                                ctx -> setIcon(ctx, EnumArgument.getEnumArgument(ctx, "material",
                                                Material.class),
                                        IntegerArgumentType.getInteger(ctx, "id"))))));
    }

    private static int setIcon(CommandContext<CommandSource> ctx, Material material, int id) throws CommandSyntaxException {
        if (material == Material.AIR) {
            ctx.getSource().sendMessage(Component.text("Luft is unfair .-."));
            return 0;
        }
        Map map = MapArgument.getMap(ctx, "map");
        map.setIcon(new MaterialAndId(material, (byte) id));
        ctx.getSource().sendMessage(Component.text(
                "Die Map '" + map.getName() + "' hat nun das " + "Icon " + "'" + map.getIcon()
                        + "'.").color(NamedTextColor.GREEN));
        return 0;
    }
    //	public CommandSetIcon() {
    //		super(WoolBattle.getInstance(), "setIcon", new Command[0], "Setzt das Icon der Map", CommandArgument.ICON);
    //	}
    //
    //	@Override
    //	public List<String> onTabComplete(String[] args) {
    //		if (args.length == 1) {
    //			List<Material> mats = Arrays.asList(Material.values());
    //			mats.remove(Material.AIR);
    //			return Arrays.toSortedStringList(mats, args[0].toUpperCase());
    //		}
    //		return super.onTabComplete(args);
    //	}
    //
    //	@Override
    //	public boolean execute(CommandSender sender, String[] args) {
    //		if (args.length == 1) {
    //			Map map = WoolBattle.getInstance().getMapManager().getMap(getSpaced());
    //			if (map == null) {
    //				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + getSpaced() + "'gefunden werden.");
    //				return true;
    //			}
    //			MaterialAndId icon = MaterialAndId.fromString(args[0]);
    //			if (icon == null || args[0].startsWith("AIR")) {
    //				sender.sendMessage("§cDieses Icon ist nicht gültig");
    //				return true;
    //			}
    //			map.setIcon(icon);
    //			sender.sendMessage("§aDie Map '" + map.getName() + "' hat nun das Icon '" + icon + "'.");
    //			return true;
    //		}
    //		return false;
    //	}
}

/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CommandSetIcon extends WBCommand {
    public CommandSetIcon() {
        super("setIcon", b -> b.executes(CommandSetIcon::setIcon));
    }

    private static int setIcon(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ItemStack item = ctx.getSource().asPlayer().getItemInHand();
        Material material = item == null ? Material.AIR : item.getType();
        if (material == Material.AIR) {
            ctx.getSource().sendMessage(Component.text("Luft is unfair .-."));
            return 0;
        }
        Map map = MapArgument.getMap(ctx, "map");
        map.setIcon(item);
        ctx.getSource().sendMessage(Component.text("Die Map '" + map.getName() + "' hat nun das " + "Icon " + "'" + map.getIcon() + "'.").color(NamedTextColor.GREEN));
        return 0;
    }
}

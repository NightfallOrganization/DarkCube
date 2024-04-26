/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.troll;

import java.util.Collection;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.PerkArgument;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;

public class CommandSetPerkCooldown extends WBCommand {
    public CommandSetPerkCooldown(WoolBattleBukkit woolbattle) {
        super("setPerkCooldown", b -> b.then(Commands.argument("perks", PerkArgument.perkArgument(woolbattle)).then(Commands.argument("cooldown", IntegerArgumentType.integer(0)).executes(ctx -> {
            Collection<Perk> perks = PerkArgument.getPerkTypes(ctx, "perks");
            int cooldown = IntegerArgumentType.getInteger(ctx, "cooldown");
            for (Perk perk : perks) {
                perk.cooldown(new Perk.Cooldown(perk.cooldown().unit(), cooldown * 20));
                ctx.getSource().sendMessage(Message.PERK_COOLDOWN_SET, perk.perkName().getName().toLowerCase(), cooldown);
            }
            return 0;
        }))));
    }
}

/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.troll;

import java.util.Collection;
import java.util.List;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.PerkArgument;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.PlayerPerks;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.bukkit.commandapi.argument.EntitySelector;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.RequiredArgumentBuilder;
import org.bukkit.entity.Player;

public class CommandSetPerk extends WBCommand {
    public CommandSetPerk(WoolBattleBukkit woolbattle) {
        super("setPerk", b -> {
            RequiredArgumentBuilder<CommandSource, EntitySelector> b2 = Commands.argument("players", EntityArgument.players());
            for (ActivationType type : ActivationType.values()) {
                Perk[] perkArray = woolbattle.perkRegistry().perks(type);
                if (perkArray.length <= 1) {
                    continue;
                }
                List<Perk> perkList = Arrays.asList(perkArray);
                b2.then(Commands.literal(type.name().toLowerCase()).then(Commands.argument("perkSlot", IntegerArgumentType.integer(1, type.maxCount())).then(Commands.argument("perk", PerkArgument.singlePerkArgument(perkList::contains, woolbattle)).executes(ctx -> {
                    int perkSlot = IntegerArgumentType.getInteger(ctx, "perkSlot") - 1;
                    Perk perk = PerkArgument.getPerk(ctx, "perk");
                    Collection<Player> targetPlayers = EntityArgument.getPlayers(ctx, "players");
                    for (Player player : targetPlayers) {
                        WBUser user = WBUser.getUser(player);
                        PlayerPerks perks = user.perksStorage();
                        perks.perk(type, perkSlot, perk.perkName());
                        user.perksStorage(perks);
                        user.perks().reloadFromStorage();
                        ctx.getSource().sendMessage(Message.PERK_SET_FOR_PLAYER, type.name().toLowerCase(), perkSlot + 1, user.getTeamPlayerName(), perk.perkName().getName().toLowerCase());
                    }
                    return 0;
                }))));
            }
            b.then(b2.build());

        });
    }
}

/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.Collections;

public class CommandHeal extends EssentialsCommand {
    public CommandHeal() {
        super("heal", b -> b
                .executes(ctx -> heal(ctx.getSource(), Collections.singleton(ctx.getSource().asPlayer())))
                .then(Commands
                        .argument("players", EntityArgument.players())
                        .executes(ctx -> heal(ctx.getSource(), EntityArgument.getPlayers(ctx, "players")))));
    }

    private static int heal(CommandSource source, Collection<Player> targets) {
        for (Player target : targets) {
            target.setHealth(target.getMaxHealth());
            removeAllNegativePotions(target);
            UserAPI.instance().user(target.getUniqueId()).sendMessage(Message.YOU_WERE_HEALED);
        }
        if (!(source.getEntity() != null && 1 == targets.size() && source.getEntity() instanceof Player && targets.contains((Player) source.getEntity()))) {
            source.sendMessage(Message.YOU_HEALED_PLAYERS, targets.size());
        }
        return 0;
    }

    private static void removeAllNegativePotions(Player player) {
        for (PotionEffect effects : player.getActivePotionEffects()) {
            for (NegativeEffects bad : NegativeEffects.values()) {
                if (effects.getType().getName().equalsIgnoreCase(bad.name())) {
                    player.removePotionEffect(effects.getType());
                }
            }
        }
    }

    private enum NegativeEffects {
        CONFUSION, HARM, HUNGER, POISON, SLOW_DIGGING, SLOW, WEAKNESS, WITHER
    }

    //	public CommandHeal() {
    //		super(DarkEssentials.getInstance(), "heal", new Command[0], "Heilt den angegebenen Spieler vollständig.",
    //				new Argument("Spieler", "Der zu heilende Spieler", false));
    //		setAliases("d_heal");
    //	}
    //
    //	@Override
    //	public boolean execute(CommandSender sender, String[] args) {
    //		List<String> argsList = Arrays.asList(args);
    //		Set<Player> players = new HashSet<>();
    //		int count = 0;
    //		if (sender instanceof Player && args.length == 0) {
    //			players.add((Player) sender);
    //		} else if (args.length != 0) {
    //			if (argsList.contains("all") && args.length <= 1) {
    //				players.addAll(Bukkit.getOnlinePlayers());
    //			} else {
    //				for (String current : args) {
    //					if (Bukkit.getPlayer(current) != null) {
    //						players.add(Bukkit.getPlayer(current));
    //					}
    //				}
    //			}
    //		} else {
    //			DarkEssentials.sendMessagePlayernameRequired(sender);
    //			return true;
    //		}
    //		for (Player current : players) {
    //			removeAllNegativePotions(current);
    //			current.setHealth(current.getMaxHealth());
    //			DarkEssentials.getInstance().sendMessage(DarkEssentials.cConfirm() + "Du wurdest geheilt!", current);
    //			count++;
    //		}
    //		if (!(players.size() == 1 && players.contains(sender))) {
    //			DarkEssentials.getInstance().sendMessage(
    //					DarkEssentials.cValue() + count + DarkEssentials.cConfirm() + " Spieler geheilt!", sender);
    //		}
    //		return true;
    //	}
    //
    //	@Override
    //	public List<String> onTabComplete(String[] args) {
    //		List<String> list = new ArrayList<>();
    //		List<String> argsList = Arrays.asList(args);
    //		if (args.length != 0) {
    //			if (argsList.contains("all")) {
    //				return list;
    //			}
    //			if (args.length == 1 && "all".startsWith(args[0].toLowerCase())) {
    //				list.add("all");
    //			}
    //			if (!argsList.contains("all")) {
    //				list.addAll(DarkEssentials.getPlayersStartWith(args));
    //			}
    //		}
    //		return list;
    //	}
    //
    //	private static enum NegativeEffects {
    //		CONFUSION, HARM, HUNGER, POISON, SLOW_DIGGING, SLOW, WEAKNESS, WITHER;
    //	}
    //
    //	private void removeAllNegativePotions(Player player) {
    //		for (PotionEffect effects : player.getActivePotionEffects()) {
    //			for (NegativeEffects bad : NegativeEffects.values()) {
    //				if (effects.getType().getName().equalsIgnoreCase(bad.name())) {
    //					player.removePotionEffect(effects.getType());
    //				}
    //			}
    //		}
    //	}
}

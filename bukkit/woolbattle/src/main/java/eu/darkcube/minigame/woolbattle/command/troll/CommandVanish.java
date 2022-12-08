/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.troll;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.system.commandapi.Command;

public class CommandVanish extends Command {

	private Set<Player> vanished = new HashSet<>();
	
	public CommandVanish() {
		super(WoolBattle.getInstance(), "vanish", new Command[0], "Vanish", CommandArgument.PLAYER_OPTIONAL);
		WoolBattle.registerListeners(new Listener<PlayerQuitEvent>() {
			@Override
			public void handle(PlayerQuitEvent e) {
				vanished.remove(e.getPlayer());
			}
		});
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player vanishing = null;
		if(args.length == 1) {
			vanishing = Bukkit.getPlayer(args[0]);
		} else if(args.length== 0){
			if(sender instanceof Player) {
				vanishing = (Player)sender;
			}
		}
		if(vanishing == null) {
			WoolBattle.getInstance().sendMessage("§cUngültiger Spieler: " + args[0], sender);
			return true;
		}
		if(vanished.contains(vanishing)) {
			vanished.remove(vanishing);
		} else {
			vanished.add(vanishing);
		}
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(vanished.contains(vanishing)) {
				p.hidePlayer(vanishing);
			} else {
				p.showPlayer(vanishing);
			}
		}
		if(vanished.contains(vanishing)) {
			WoolBattle.getInstance().sendMessage("§aVanished " + vanishing.getName(), sender);
		} else {
			WoolBattle.getInstance().sendMessage("§cUnvanished " + vanishing.getName(), sender);
		}
		return true;
	}
}

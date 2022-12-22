/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;

public class CommandPWeather extends Command {

	public CommandPWeather() {
		super(DarkEssentials.getInstance(), "pweather", new Command[0], "Setzt das Wetter nur für dich selbst.",
				new Argument("Wetter", "Das Wetter, welches für dich gesetzt wird."));
		setAliases("d_pweather");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du musst ein Spieler sein, um diesen Command auszuführen!",
					sender);
			return true;
		}
		if (args.length == 1) {
			WeatherType weather;
			try {
				switch (args[0].toLowerCase()) {
				case "clear":
					weather = WeatherType.CLEAR;
					break;
				case "rain":
				case "downfall":
					weather = WeatherType.DOWNFALL;
					break;
				case "reset":
					((Player) sender).resetPlayerWeather();
					DarkEssentials.getInstance().sendMessage(DarkEssentials.cConfirm() + "Wetter zurückgesetzt!", sender);
					return true;
				default:
					throw new IllegalArgumentException();
				}
			} catch (Exception ex) {
				DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du musst ein Wetter angeben!", sender);
				return true;
			}
			((Player) sender).setPlayerWeather(weather);
			switch (weather) {
			case CLEAR:
				DarkEssentials.getInstance().sendMessage(DarkEssentials.cConfirm() + "Die Sonne scheint (angeblich)", sender);
				break;
			case DOWNFALL:
				DarkEssentials.getInstance().sendMessage(DarkEssentials.cConfirm() + "Der Regen fällt (angeblich)", sender);
				break;
			}
			return true;
		}
		return false;
	}

}

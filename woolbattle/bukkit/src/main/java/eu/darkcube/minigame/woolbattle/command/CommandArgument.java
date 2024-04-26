/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.system.bukkit.commandapi.deprecated.Argument;

public class CommandArgument {

	public static final Argument ARGUMENT_MAKE_NICE =
			new Argument("true|false", "Rückt die Location gerade", false);
	public static final Argument MAP = new Argument("Map", "Die Map");
	public static final Argument WOOL_COLOR = new Argument("Wollfarbe", "Die Wollfarbe");
	public static final Argument NAME_COLOR = new Argument("Namenfarbe", "Die Farbe des Namens");
	public static final Argument ICON = new Argument("Icon", "Das Icon");
	public static final Argument PERK = new Argument("Perk", "Das Perk");
	public static final Argument COOLDOWN = new Argument("Cooldown", "Der Cooldown");
	public static final Argument COST = new Argument("Kosten", "Der Kosten");
	public static final Argument PLAYER_OPTIONAL = new Argument("Spieler", "Der Spieler");
	public static final Argument DEATHHEIGHT = new Argument("DeathHeight", "Die Todeshöhe");

}

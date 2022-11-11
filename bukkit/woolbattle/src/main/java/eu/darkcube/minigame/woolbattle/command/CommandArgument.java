package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.system.commandapi.Argument;

public class CommandArgument {

	public static final Argument ARGUMENT_MAKE_NICE = new Argument("true|false", "Rückt die Location gerade", false);

	public static final Argument MAP = new Argument("Map", "Die Map");

	public static final Argument TEAM = new Argument("Team", "Das Team");

	public static final Argument WEIGHT = new Argument("Sortierung", "Je größer desto tiefer");

	public static final Argument WOOL_COLOR = new Argument("Wollfarbe", "Die Wollfarbe");

	public static final Argument NAME_COLOR = new Argument("Namenfarbe", "Die Farbe des Namens");

	public static final Argument MAX_PLAYERS = new Argument("Maximale Spieleranzahl", "Maximale Spieleranzahl");

	public static final Argument ICON = new Argument("Icon", "Das Icon");

	public static final Argument TIMER = new Argument("Timer", "Der neue Timer");

	public static final Argument LIFES = new Argument("Lifes", "Die neuen Leben");

	public static final Argument PERK = new Argument("Perk", "Das Perk");

	public static final Argument COOLDOWN = new Argument("Cooldown", "Der Cooldown");

	public static final Argument COST = new Argument("Kosten", "Der Kosten");

	public static final Argument PERK_SLOT = new Argument("PerkSlot", "Der PerkSlot");

	public static final Argument PLAYER_OPTIONAL = new Argument("Spieler", "Der Spieler");

	public static final Argument PLAYER = new Argument("Player", "Der Spieler");

	public static final Argument DEATHHEIGHT = new Argument("DeathHeight", "Die Todeshöhe");

	public static final Argument WORLD = new Argument("World", "Die Welt");

}

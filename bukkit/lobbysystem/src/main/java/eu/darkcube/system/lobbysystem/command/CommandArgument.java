package eu.darkcube.system.lobbysystem.command;

import eu.darkcube.system.commandapi.Argument;

public class CommandArgument {

	public static final Argument SPAWN = new Argument("spawn", "Der Spawn");
	public static final Argument MAKE_NICE_LOCATION = new Argument("true|false", "Ob die Location geradegerückt werden soll");
	public static final Argument RADIUS = new Argument("radius", "Der Radius");
	public static final Argument SHAPE = new Argument("shape", "Die Form");
	public static final Argument CLOUD_TASK= new Argument("task", "Die Cloud Task");
	
}

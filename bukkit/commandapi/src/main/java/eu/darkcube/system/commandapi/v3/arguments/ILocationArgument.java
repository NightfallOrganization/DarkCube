package eu.darkcube.system.commandapi.v3.arguments;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Vector2f;
import eu.darkcube.system.commandapi.v3.Vector3d;

public interface ILocationArgument {
	
	Vector3d getPosition(CommandSource source);

	Vector2f getRotation(CommandSource source);

	boolean isXRelative();

	boolean isYRelative();

	boolean isZRelative();
}

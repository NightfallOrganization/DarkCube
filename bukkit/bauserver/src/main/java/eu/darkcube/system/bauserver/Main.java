package eu.darkcube.system.bauserver;

import eu.darkcube.system.*;
import eu.darkcube.system.bauserver.command.*;
import eu.darkcube.system.commandapi.*;

public class Main extends Plugin {

	@Override
	public void onEnable() {
		CommandAPI.enable(this, new CommandSpeed());
	}
	
	public static Main getInstance() {
		return Main.getPlugin(Main.class);
	}

	@Override
	public String getCommandPrefix() {
		return "BauServer";
	}

}

package eu.darkcube.system.pserver.plugin.listener;

import eu.darkcube.system.pserver.plugin.util.SingleInstance;

public class SingleInstanceBaseListener extends SingleInstance implements BaseListener {

	public SingleInstanceBaseListener() {
		register();
	}
}

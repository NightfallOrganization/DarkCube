package eu.darkcube.system.pserver.wrapper.event;

import de.dytanic.cloudnet.driver.event.Event;
import eu.darkcube.system.pserver.common.PServer;

public abstract class PServerEvent extends Event {

	private final PServer pserver;

	public PServerEvent(PServer pserver) {
		super();
		this.pserver = pserver;
	}

	public PServer getPServer() {
		return pserver;
	}
}

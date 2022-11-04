package eu.darkcube.system.pserver.wrapper.event;

import eu.darkcube.system.pserver.common.PServer;

public class PServerUpdateEvent extends PServerEvent {

	public PServerUpdateEvent(PServer pserver) {
		super(pserver);
	}
}

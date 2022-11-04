package eu.darkcube.system.pserver.wrapper.event;

import eu.darkcube.system.pserver.common.PServer;

public class PServerRemoveEvent extends PServerEvent {

	public PServerRemoveEvent(PServer pserver) {
		super(pserver);
	}
}

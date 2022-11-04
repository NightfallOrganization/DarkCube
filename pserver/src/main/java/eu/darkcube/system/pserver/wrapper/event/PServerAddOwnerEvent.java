package eu.darkcube.system.pserver.wrapper.event;

import java.util.UUID;

import eu.darkcube.system.pserver.common.PServer;

public class PServerAddOwnerEvent extends PServerEvent {

	private UUID owner;

	public PServerAddOwnerEvent(PServer pserver, UUID owner) {
		super(pserver);
		this.owner = owner;
	}

	public UUID getOwner() {
		return owner;
	}

}

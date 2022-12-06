package eu.darkcube.system.pserver.bukkit.event;

import java.util.UUID;

import eu.darkcube.system.pserver.common.PServer;

public class PServerRemoveOwnerEvent extends PServerEvent {

	private UUID owner;

	public PServerRemoveOwnerEvent(PServer pserver, UUID owner) {
		super(pserver);
		this.owner = owner;
	}

	public UUID getOwner() {
		return owner;
	}

}

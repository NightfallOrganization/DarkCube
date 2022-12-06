package eu.darkcube.system.pserver.bukkit;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.UniqueIdProvider;
import eu.darkcube.system.pserver.common.packet.packets.PacketNodeWrapperUniqueId;
import eu.darkcube.system.pserver.common.packet.packets.PacketWrapperNodeNewUniqueId;

public class WrapperUniqueIdProvider extends UniqueIdProvider {

	@Override
	public boolean isAvailable(UniqueId id) {
		return false;
	}

	@Override
	public UniqueId newUniqueId() {
		return new PacketWrapperNodeNewUniqueId().sendQuery().cast(PacketNodeWrapperUniqueId.class).getUniqueId();
	}
	
	public static void init() {
		
	}
	
	static {
		new WrapperUniqueIdProvider();
	}
}

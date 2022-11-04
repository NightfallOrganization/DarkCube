package eu.darkcube.system.pserver.node;

import java.util.UUID;

import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.UniqueIdProvider;
import eu.darkcube.system.pserver.node.database.DatabaseProvider;
import eu.darkcube.system.pserver.node.database.PServerDatabase;

public class NodeUniqueIdProvider extends UniqueIdProvider {

	@Override
	public UniqueId newUniqueId() {
		UniqueId id;
		do {
			UUID uuid = UUID.randomUUID();
			id = new UniqueId(uuid.toString());
		} while (!isAvailable(id));
		return id;
	}

	@Override
	public boolean isAvailable(UniqueId id) {
		return !DatabaseProvider.get("pserver").cast(PServerDatabase.class).contains(id);
	}

	public static void init() {
		
	}
	
	static {
		new NodeUniqueIdProvider();
	}
}

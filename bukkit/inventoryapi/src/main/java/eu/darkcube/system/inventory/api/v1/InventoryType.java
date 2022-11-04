package eu.darkcube.system.inventory.api.v1;

import java.util.HashMap;
import java.util.Map;

public final class InventoryType {

	private final String id;

	private static final Map<String, InventoryType> TYPES = new HashMap<>();

	private InventoryType(String id) {
		this.id = id;
	}

	public static final InventoryType of(String id) {
		if (!TYPES.containsKey(id)) {
			TYPES.put(id, new InventoryType(id));
		}
		return TYPES.get(id);
	}

	@Override
	public String toString() {
		return this.id;
	}
}

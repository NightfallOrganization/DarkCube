package eu.darkcube.system.lobbysystem.gadget;

public enum Gadget {

	HOOK_ARROW, GRAPPLING_HOOK;

	public static Gadget fromString(String gadget) {
		for (Gadget g : Gadget.values()) {
			if (g.name().equalsIgnoreCase(gadget)) {
				return g;
			}
		}
		return HOOK_ARROW;
	}
}

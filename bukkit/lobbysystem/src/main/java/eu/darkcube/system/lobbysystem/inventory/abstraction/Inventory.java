package eu.darkcube.system.lobbysystem.inventory.abstraction;

import eu.darkcube.system.lobbysystem.user.User;

public abstract class Inventory {
	protected static int dist(int c, int s) {
		int[] cs = s(c);
		int[] ss = s(s);
		return Math.abs(cs[0] - ss[0]) + Math.abs(cs[1] - ss[1]);
	}

	protected static int[] s(int s) {
		int r = s / 9 + 1;
		s %= 9;
		s++;
		return new int[] {
				r, s
		};
	}

	protected static int s(int r, int i) {
		return (r - 1) * 9 + i - 1;
	}

	protected static int s0(int i, int r) {
		return (r - 1) * 9 + i - 1;
	}

	protected org.bukkit.inventory.Inventory handle;
	protected InventoryType type;

	public Inventory(org.bukkit.inventory.Inventory handle, InventoryType type) {
		this.handle = handle;
		this.type = type;
	}

	public InventoryType getType() {
		return type;
	}

	public org.bukkit.inventory.Inventory getHandle() {
		return handle;
	}

	public abstract void playAnimation(User user);

	public abstract void skipAnimation(User user);
}

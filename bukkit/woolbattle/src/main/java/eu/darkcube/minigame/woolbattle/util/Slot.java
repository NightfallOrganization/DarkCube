package eu.darkcube.minigame.woolbattle.util;

public final class Slot {

	public static final class Inventory {
		public static final int SLOT_1 = 9;
		public static final int SLOT_2 = 10;
		public static final int SLOT_3 = 11;
		public static final int SLOT_4 = 12;
		public static final int SLOT_5 = 13;
		public static final int SLOT_6 = 14;
		public static final int SLOT_7 = 15;
		public static final int SLOT_8 = 16;
		public static final int SLOT_9 = 17;
		public static final int SLOT_10 = 18;
		public static final int SLOT_11 = 19;
		public static final int SLOT_12 = 20;
		public static final int SLOT_13 = 21;
		public static final int SLOT_14 = 22;
		public static final int SLOT_15 = 23;
		public static final int SLOT_16 = 24;
		public static final int SLOT_17 = 25;
		public static final int SLOT_18 = 26;
		public static final int SLOT_19 = 27;
		public static final int SLOT_20 = 28;
		public static final int SLOT_21 = 29;
		public static final int SLOT_22 = 30;
		public static final int SLOT_23 = 31;
		public static final int SLOT_24 = 32;
		public static final int SLOT_25 = 33;
		public static final int SLOT_26 = 34;
		public static final int SLOT_27 = 35;
	}

	public static final class Hotbar {
		public static final int SLOT_1 = 36;
		public static final int SLOT_2 = 37;
		public static final int SLOT_3 = 38;
		public static final int SLOT_4 = 39;
		public static final int SLOT_5 = 40;
		public static final int SLOT_6 = 41;
		public static final int SLOT_7 = 42;
		public static final int SLOT_8 = 43;
		public static final int SLOT_9 = 44;
	}

	public static final class Armor {
		public static final int HEAD = 5;
		public static final int CHEST = 6;
		public static final int PANTS = 7;
		public static final int BOOTS = 8;
	}

	public static final class CraftingField {
		public static final int SLOT_1 = 1;
		public static final int SLOT_2 = 2;
		public static final int SLOT_3 = 3;
		public static final int SLOT_4 = 4;
		public static final int PRODUCT = 0;
	}

//	private static final int convert(int slot, boolean hotbar, boolean armor, boolean crafting) {
//		if ((hotbar && armor) || (armor && crafting) || (crafting && hotbar)) {
//			throw new IllegalArgumentException("Only hotbar, armor OR crafting is allowed, but not multiple");
//		}
//		if (hotbar) {
//			return slot + 27;
//		}
//		if (armor) {
//			return slot + 5;
//		}
//		if (crafting) {
//			return slot;
//		}
//		return slot - 9;
//	}
}

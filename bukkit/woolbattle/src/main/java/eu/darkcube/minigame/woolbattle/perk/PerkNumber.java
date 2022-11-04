package eu.darkcube.minigame.woolbattle.perk;

import eu.darkcube.minigame.woolbattle.user.User;

public enum PerkNumber {

	ACTIVE_1, ACTIVE_2, PASSIVE, DISPLAY, ENDER_PEARL

	;

	public int getRawSlot(User user) {
		switch (this) {
		case ACTIVE_1:
			return user.getData().getPerks().getSlotActivePerk1();
		case ACTIVE_2:
			return user.getData().getPerks().getSlotActivePerk2();
		case PASSIVE:
			return user.getData().getPerks().getSlotPassivePerk();
		case DISPLAY:
			return -1;
		case ENDER_PEARL:
			return user.getData().getPerks().getSlotPearl();
		}
		return -1;
	}

	public void setRawSlot(User user, int newSlot) {
		switch (this) {
		case ACTIVE_1:
			user.getData().getPerks().setSlotActivePerk1(newSlot);
			break;
		case ACTIVE_2:
			user.getData().getPerks().setSlotActivePerk2(newSlot);
			break;
		case PASSIVE:
			user.getData().getPerks().setSlotPassivePerk(newSlot);
		case DISPLAY:
			return;
		case ENDER_PEARL:
			user.getData().getPerks().setSlotPearl(newSlot);
			break;
		}
//		MySQL.saOveUserData(user);
	}

	public static PerkNumber getPerkNumberFromPerk(User owner, PerkType perk) {
		PlayerPerks pp = owner.getData().getPerks();
		if (pp.getActivePerk1().equals(perk.getPerkName())) {
			return ACTIVE_1;
		} else if (pp.getActivePerk2().equals(perk.getPerkName())) {
			return ACTIVE_1;
		} else if (pp.getPassivePerk().equals(perk.getPerkName())) {
			return PASSIVE;
		}
		return DISPLAY;
	}
}

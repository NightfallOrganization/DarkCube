/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.other;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.passive.ListenerDoubleJump;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;

public class DoubleJumpPerk extends Perk {
	public static final PerkName DOUBLE_JUMP = new PerkName("DOUBLE_JUMP");

	public DoubleJumpPerk() {
		super(ActivationType.DOUBLE_JUMP, DOUBLE_JUMP, new Cooldown(Unit.TICKS, 63), 5, null,
				DoubleJumpUserPerk::new);
		addListener(new ListenerDoubleJump(this));
	}

	public static class DoubleJumpUserPerk extends DefaultUserPerk {

		public DoubleJumpUserPerk(WBUser owner, Perk perk, int id, int perkSlot) {
			super(owner, perk, id, perkSlot);
		}

		@Override
		public void cooldown(int cooldown) {
			float max = perk().cooldown().cooldown();
			owner().getBukkitEntity()
					.setFoodLevel(7 + (Math.round((max - (float) cooldown) / max * 13)));
			if (cooldown <= 0 && cooldown() > 0) {
				owner().getBukkitEntity().setAllowFlight(true);
			}
			super.cooldown(cooldown);
		}
	}
}

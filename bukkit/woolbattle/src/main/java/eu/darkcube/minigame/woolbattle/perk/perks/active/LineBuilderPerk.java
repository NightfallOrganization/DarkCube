/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.listener.ingame.perk.active.ListenerLineBuilder;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;

public class LineBuilderPerk extends Perk {
	public static final PerkName LINE_BUILDER = new PerkName("LINE_BUILDER");

	public LineBuilderPerk() {
		super(ActivationType.ACTIVE, LINE_BUILDER, new Cooldown(TimeUnit.SECOND, 10), true, 2,
				CostType.PER_BLOCK, Item.PERK_LINE_BUILDER,
				(user, perk, id, perkSlot) -> new LineBuilderUserPerk(user, id, perkSlot, perk));
		addListener(new ListenerLineBuilder(this));
	}

	public static class LineBuilderUserPerk extends CooldownUserPerk {

		private boolean useCooldownItem = false;

		public LineBuilderUserPerk(WBUser owner, int id, int perkSlot, Perk perk) {
			super(owner, id, perkSlot, perk, Item.PERK_LINE_BUILDER_COOLDOWN);
		}

		@Override
		public boolean useCooldownItem() {
			return useCooldownItem;
		}

		@Override
		public void cooldown(int cooldown) {
			super.cooldown(cooldown);
			if (cooldown() >= perk().cooldown().ticks()) {
				useCooldownItem = true;
			} else if (cooldown == 0) {
				useCooldownItem = false;
			}
		}
	}
}

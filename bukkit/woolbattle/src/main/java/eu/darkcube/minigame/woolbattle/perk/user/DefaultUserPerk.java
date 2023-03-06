/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.user;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.user.PlayerPerks;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;

public class DefaultUserPerk implements UserPerk {
	private final int id;
	private final Perk perk;
	private final WBUser owner;
	private final int perkSlot;
	private int slot;
	private int cooldown;

	public DefaultUserPerk(final WBUser owner, final Perk perk, final int id, final int perkSlot) {
		this(owner, id, perkSlot, perk);
	}

	public DefaultUserPerk(final WBUser owner, final int id, final int perkSlot, final Perk perk) {
		this.id = id;
		this.perkSlot = perkSlot;
		this.perk = perk;
		this.slot = owner.perksStorage().perkInvSlot(perk.activationType(), this.perkSlot);
		this.cooldown = 0;
		this.owner = owner;
	}

	protected Item currentItem() {
		return perk.defaultItem();
	}

	/**
	 * @return the perk slot for the perk type
	 * <br>Example: for {@link eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType#ACTIVE}
	 * slot 1
	 */
	public int perkSlot() {
		return perkSlot;
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public Perk perk() {
		return perk;
	}

	@Override
	public PerkItem currentPerkItem() {
		return new PerkItem(this::currentItem, this);
	}

	@Override
	public void slot(int slot) {
		int oldValue = this.slot;
		slotSilent(slot);
		if (WoolBattle.getInstance().getIngame().enabled()) {
			if (oldValue == 100) {
				owner().getBukkitEntity().getOpenInventory().setCursor(null);
			} else if (oldValue != -1 /* -1 for no slot set */) {
				owner().getBukkitEntity().getOpenInventory().setItem(oldValue, null);
			}
			currentPerkItem().setItem();
		}
	}

	@Override
	public int slot() {
		return slot;
	}

	@Override
	public int cooldown() {
		return this.cooldown;
	}

	@Override
	public void cooldown(int cooldown) {
		this.cooldown = Math.min(cooldown, perk.cooldown().ticks());
		if (WoolBattle.getInstance().getIngame().enabled()) {
			currentPerkItem().setItem();
		}
	}

	@Override
	public void slotSilent(int slot) {
		this.slot = slot;
		PlayerPerks pp = owner().perksStorage();
		pp.perkInvSlot(perk().activationType(), perkSlot(), slot);
		owner().perksStorage(pp);
	}

	@Override
	public WBUser owner() {
		return owner;
	}
}

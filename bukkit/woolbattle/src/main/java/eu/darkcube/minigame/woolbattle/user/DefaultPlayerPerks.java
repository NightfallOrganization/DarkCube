/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.user;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.active.CapsulePerk;
import eu.darkcube.minigame.woolbattle.perk.perks.active.SwitcherPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.other.ArrowPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.other.BowPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.other.ShearsPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.RocketJumpPerk;
import eu.darkcube.minigame.woolbattle.util.Slot.Hotbar;
import eu.darkcube.minigame.woolbattle.util.Slot.Inventory;

import java.util.*;

public class DefaultPlayerPerks implements PlayerPerks {

	private final Map<ActivationType, PerkName[]> perks;
	private final Map<ActivationType, int[]> perkSlots;

	public DefaultPlayerPerks() {
		Map<ActivationType, PerkName[]> perks = new HashMap<>();
		Map<ActivationType, int[]> perkSlots = new HashMap<>();
		for (ActivationType type : ActivationType.values()) {
			perks.put(type, new PerkName[type.maxCount()]);
			perkSlots.put(type, new int[type.maxCount()]);
		}
		this.perks = Collections.unmodifiableMap(perks);
		this.perkSlots = Collections.unmodifiableMap(perkSlots);
		reset();
	}

	public DefaultPlayerPerks(Map<ActivationType, PerkName[]> perks,
			Map<ActivationType, int[]> perkSlots) {
		this();
		perks(perks);
		for (ActivationType type : perkSlots.keySet()) {
			System.arraycopy(perkSlots.get(type), 0, this.perkSlots.get(type), 0,
					Math.min(perkSlots.get(type).length, this.perkSlots.get(type).length));
		}
	}

	@Override
	public PlayerPerks clone() {
		return new DefaultPlayerPerks(perks, perkSlots);
	}

	@Override
	public PerkName[] perks(ActivationType type) {
		return Arrays.copyOf(perks.get(type), perks.get(type).length);
	}

	@Override
	public PerkName perk(ActivationType type, int perkSlot) {
		return perks(type)[perkSlot];
	}

	@Override
	public void perk(ActivationType type, int perkSlot, PerkName perk) {
		perks.get(type)[perkSlot] = perk;
	}

	@Override
	public int[] perkInvSlots(ActivationType type) {
		return Arrays.copyOf(perkSlots.get(type), perkSlots.get(type).length);
	}

	@Override
	public int perkInvSlot(ActivationType type, int perkSlot) {
		return perkInvSlots(type)[perkSlot];
	}

	@Override
	public void perkInvSlot(ActivationType type, int perkSlot, int slot) {
		perkSlots.get(type)[perkSlot] = slot;
	}

	@Override
	public Map<ActivationType, PerkName[]> perks() {
		return perks;
	}

	@Override
	public void perks(Map<ActivationType, PerkName[]> perks) {
		for (ActivationType type : perks.keySet()) {
			System.arraycopy(perks.get(type), 0, this.perks.get(type), 0,
					Math.min(perks.get(type).length, this.perks.get(type).length));
		}
	}

	@Override
	public void reset() {
		List<ActivationType> list = Arrays.asList(ActivationType.values());
		list.sort(null);
		Set<Integer> slotsUsed = new HashSet<>();
		Deque<Integer> slotsQueryFrom = new ArrayDeque<>();
		slotsQueryFrom.offer(Hotbar.SLOT_1);
		slotsQueryFrom.offer(Hotbar.SLOT_2);
		slotsQueryFrom.offer(Hotbar.SLOT_3);
		slotsQueryFrom.offer(Hotbar.SLOT_4);
		slotsQueryFrom.offer(Hotbar.SLOT_5);
		slotsQueryFrom.offer(Hotbar.SLOT_9);
		// For arrow
		slotsQueryFrom.offer(Inventory.SLOT_9);

		// For illegal perks. Should never be used
		slotsQueryFrom.addAll(Arrays.asList(convert(Hotbar.SLOTS)));
		slotsQueryFrom.addAll(Arrays.asList(convert(Inventory.SLOTS)));
		List<PerkName> perksUsed = new ArrayList<>();

		// The items will be filled in the ActivationType's order
		for (ActivationType type : list) {
			for (int i = 0; i < perkSlots.get(type).length; i++) {
				int slot;
				do {
					slot = slotsQueryFrom.removeFirst();
				} while (slotsUsed.contains(slot));
				slotsUsed.add(slot);
				perkSlots.get(type)[i] = slot;

				if (type == ActivationType.PRIMARY_WEAPON && i == 0) {
					perks.get(type)[i] = BowPerk.BOW;
				} else if (type == ActivationType.SECONDARY_WEAPON && i == 0) {
					perks.get(type)[i] = ShearsPerk.SHEARS;
				} else if (type == ActivationType.ARROW && i == 0) {
					perks.get(type)[i] = ArrowPerk.ARROW;
				} else if (type == ActivationType.ACTIVE && i == 0) {
					perks.get(type)[i] = CapsulePerk.CAPSULE;
				} else if (type == ActivationType.ACTIVE && i == 1) {
					perks.get(type)[i] = SwitcherPerk.SWITCHER;
				} else if (type == ActivationType.PASSIVE && i == 0) {
					perks.get(type)[i] = RocketJumpPerk.ROCKET_JUMP;
				} else { // Fallback

					PerkName name = null;
					for (Map.Entry<PerkName, Perk> e : WoolBattle.getInstance().perkRegistry()
							.perks().entrySet()) {
						if (e.getValue().activationType() == type) {
							name = e.getKey();
							if (perksUsed.contains(name))
								continue;
							perksUsed.add(name);
							break;
						}
					}
					if (name == null)
						throw new NullPointerException();
					perks.get(type)[i] = name;
				}
			}
		}
	}

	private Integer[] convert(int[] ints) {
		Integer[] a = new Integer[ints.length];
		for (int i = 0; i < a.length; i++) {
			a[i] = ints[i];
		}
		return a;
	}
}

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.user;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.user.PlayerPerks;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Arrays;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UserPerks {
	private static final AtomicInteger id = new AtomicInteger();

	private final WBUser user;
	private final Map<Integer, UserPerk> perks = new HashMap<>();

	public UserPerks(WBUser user) {
		this.user = user;
		reloadFromStorage();
	}

	public void reloadFromStorage() {
		perks.clear();
		PlayerPerks p = user.perksStorage();
		for (ActivationType type : ActivationType.values()) {
			PerkName[] perks = p.perks(type);
			for (int i = 0; i < perks.length; i++) {
				Perk perk = WoolBattle.getInstance().perkRegistry().perks().get(perks[i]);
				if (perk == null) {
					List<PerkName> listPerks = Arrays.asList(perks);
					perk = java.util.Arrays.stream(
									WoolBattle.getInstance().perkRegistry().perks(type))
							.filter(pe -> !listPerks.contains(pe.perkName())).findAny()
							.orElseThrow(Error::new);
					p.perk(type, i, perk.perkName());
					user.perksStorage(p);
					System.out.println("Fixing perk: " + perks[i] + " -> " + perk.perkName());
				}
				perk(perk, i);
			}
		}
	}

	public void perk(Perk perk, int perkSlot) {
		int id = UserPerks.id.getAndIncrement();
		UserPerk up;
		perks.put(id, up = perk.perkCreator().create(user, perk, id, perkSlot));
		if (WoolBattle.getInstance().getIngame().isEnabled()) {
			up.currentPerkItem().setItem();
		}
	}

	public int count(PerkName perkName) {
		return (int) perks.values().stream().filter(p -> p.perk().perkName().equals(perkName))
				.count();
	}

	public Collection<UserPerk> perks() {
		return perks.values();
	}

	public Collection<UserPerk> perks(ActivationType type) {
		return perks.values().stream().filter(p -> p.perk().activationType() == type)
				.collect(Collectors.toList());
	}

	public Collection<UserPerk> perks(PerkName perkName) {
		return perks.values().stream().filter(p -> p.perk().perkName().equals(perkName))
				.collect(Collectors.toList());
	}

	public UserPerk perk(int id) {
		return perks.get(id);
	}

	public void remove(int id) {
		perks.remove(id);
	}

	public WBUser user() {
		return user;
	}
}

/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.user;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.user.PlayerPerks;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Arrays;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class UserPerks {
    private static final AtomicInteger id = new AtomicInteger();

    private final WBUser user;
    private final Map<Integer, UserPerk> perks = new HashMap<>();
    private final Map<ActivationType, Collection<UserPerk>> byType = new HashMap<>();
    private final Map<PerkName, Collection<UserPerk>> byName = new HashMap<>();

    public UserPerks(WBUser user) {
        this.user = user;
    }

    public void reloadFromStorage() {
        perks.clear();
        byType.clear();
        byName.clear();
        PlayerPerks p = user.perksStorage();
        for (ActivationType type : ActivationType.values()) {
            PerkName[] perks = p.perks(type);
            for (int i = 0; i < perks.length; i++) {
                Perk perk = WoolBattleBukkit.instance().perkRegistry().perks().get(perks[i]);
                if (perk == null) {
                    List<PerkName> listPerks = Arrays.asList(perks);
                    perk = java.util.Arrays.stream(WoolBattleBukkit.instance().perkRegistry().perks(type))
                            .filter(pe -> !listPerks.contains(pe.perkName())).findAny()
                            .orElseThrow(Error::new);
                    p.perk(type, i, perk.perkName());
                    user.perksStorage(p);
                    System.out.println("Fixing perk: " + perks[i] + " -> " + perk.perkName());
                }
                perk(perk, i);
            }
        }
        for (Entry<ActivationType, Collection<UserPerk>> entry : new ArrayList<>(
                byType.entrySet())) {
            byType.put(entry.getKey(), Collections.unmodifiableCollection(entry.getValue()));
        }
        for (Entry<PerkName, Collection<UserPerk>> entry : new ArrayList<>(byName.entrySet())) {
            byName.put(entry.getKey(), Collections.unmodifiableCollection(entry.getValue()));
        }
    }

    private void perk(Perk perk, int perkSlot) {
        int id = UserPerks.id.getAndIncrement();
        UserPerk up;
        perks.put(id, up = perk.perkCreator().create(user, perk, id, perkSlot));
        byType.computeIfAbsent(perk.activationType(), (a) -> new ArrayList<>());
        byName.computeIfAbsent(perk.perkName(), (a) -> new ArrayList<>());
        byType.get(perk.activationType()).add(up);
        byName.get(perk.perkName()).add(up);
        if (WoolBattleBukkit.instance().ingame().enabled()) {
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
        return byType.getOrDefault(type, Collections.emptyList());
    }

    public Collection<UserPerk> perks(PerkName perkName) {
        return byName.getOrDefault(perkName, Collections.emptyList());
    }

    public UserPerk perk(int id) {
        return perks.get(id);
    }

    public WBUser user() {
        return user;
    }
}

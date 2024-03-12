/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.perk.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.game.ingame.Ingame;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerks;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;

public class CommonUserPerks implements UserPerks {
    private static final AtomicInteger id = new AtomicInteger();

    private final Game game;
    private final WBUser user;
    private final Map<Integer, UserPerk> perks = new HashMap<>();
    private final Map<ActivationType, Collection<UserPerk>> byType = new HashMap<>();
    private final Map<PerkName, Collection<UserPerk>> byName = new HashMap<>();

    public CommonUserPerks(WBUser user) {
        this.user = user;
        this.game = user.game();
    }

    @Override public void reloadFromStorage() {
        perks.clear();
        byType.clear();
        byName.clear();
        var p = user.perksStorage();
        for (var type : ActivationType.values()) {
            var perks = p.perks(type);
            for (var i = 0; i < perks.length; i++) {
                var perk = game.perkRegistry().perks().get(perks[i]);
                if (perk == null) {
                    var listPerks = Arrays.asList(perks);
                    perk = java.util.Arrays.stream(game.perkRegistry().perks(type)).filter(pe -> !listPerks.contains(pe.perkName())).findAny().orElseThrow(Error::new);
                    p.perk(type, i, perk.perkName());
                    user.perksStorage(p);
                    System.out.println("Fixing perk: " + perks[i] + " -> " + perk.perkName());
                }
                perk(perk, i);
            }
        }
        for (var entry : new ArrayList<>(byType.entrySet())) {
            byType.put(entry.getKey(), Collections.unmodifiableCollection(entry.getValue()));
        }
        for (var entry : new ArrayList<>(byName.entrySet())) {
            byName.put(entry.getKey(), Collections.unmodifiableCollection(entry.getValue()));
        }
    }

    private void perk(Perk perk, int perkSlot) {
        var id = CommonUserPerks.id.getAndIncrement();
        UserPerk up;
        perks.put(id, up = perk.perkCreator().create(user, perk, id, perkSlot, game));
        byType.computeIfAbsent(perk.activationType(), (a) -> new ArrayList<>());
        byName.computeIfAbsent(perk.perkName(), (a) -> new ArrayList<>());
        byType.get(perk.activationType()).add(up);
        byName.get(perk.perkName()).add(up);
        if (game.phase() instanceof Ingame) {
            up.currentPerkItem().setItem();
        }
    }

    @Override public int count(PerkName perkName) {
        return (int) perks.values().stream().filter(p -> p.perk().perkName().equals(perkName)).count();
    }

    @Override public Collection<UserPerk> perks() {
        return perks.values();
    }

    @Override public Collection<UserPerk> perks(ActivationType type) {
        return byType.getOrDefault(type, Collections.emptyList());
    }

    @Override public Collection<UserPerk> perks(PerkName perkName) {
        return byName.getOrDefault(perkName, Collections.emptyList());
    }

    @Override public UserPerk perk(int id) {
        return perks.get(id);
    }

    @Override public WBUser user() {
        return user;
    }
}

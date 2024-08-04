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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import eu.darkcube.minigame.woolbattle.api.game.ingame.Ingame;
import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.Perk;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.api.perk.user.UserPerks;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;

public class CommonUserPerks implements UserPerks {
    private static final AtomicInteger id = new AtomicInteger();

    private final CommonGame game;
    private final CommonWBUser user;
    private final Map<Integer, UserPerk> perks = new HashMap<>();
    private final Map<ActivationType, List<UserPerk>> byType = new HashMap<>();
    private final Map<PerkName, List<UserPerk>> byName = new HashMap<>();

    public CommonUserPerks(CommonWBUser user) {
        this.user = user;
        this.game = user.game();
    }

    @Override
    public void reloadFromStorage() {
        perks.clear();
        byType.clear();
        byName.clear();
        var p = user.perksStorage();
        var modifiedStorage = false;
        for (var type : ActivationType.values()) {
            var perks = p.perks(type);
            for (var i = 0; i < perks.length; i++) {
                var perk = game.perkRegistry().perks().get(perks[i]);
                if (perk == null) {
                    var listPerks = Arrays.asList(perks);
                    var perkOptional = Arrays.stream(game.perkRegistry().perks(type)).filter(pe -> !listPerks.contains(pe.perkName())).findAny();
                    if (perkOptional.isEmpty()) break;

                    perk = perkOptional.get();

                    p.perk(type, i, perk.perkName());
                    modifiedStorage = true;
                    game.woolbattle().woolbattle().logger().warn("Fixing perk for {}: {} -> {}", user.playerName(), perks[i], perk.perkName());
                }
                perk(perk, i);
            }
        }
        if (modifiedStorage) {
            user.perksStorage(p);
        }
        for (var entry : new ArrayList<>(byType.entrySet())) {
            byType.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        for (var entry : new ArrayList<>(byName.entrySet())) {
            byName.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
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

    @Override
    public int count(PerkName perkName) {
        return (int) perks.values().stream().filter(p -> p.perk().perkName().equals(perkName)).count();
    }

    @Override
    public Collection<UserPerk> perks() {
        return perks.values();
    }

    @Override
    public List<UserPerk> perks(ActivationType type) {
        return byType.getOrDefault(type, Collections.emptyList());
    }

    @Override
    public List<UserPerk> perks(PerkName perkName) {
        return byName.getOrDefault(perkName, Collections.emptyList());
    }

    @Override
    public UserPerk perk(int id) {
        return perks.get(id);
    }

    @Override
    public WBUser user() {
        return user;
    }
}

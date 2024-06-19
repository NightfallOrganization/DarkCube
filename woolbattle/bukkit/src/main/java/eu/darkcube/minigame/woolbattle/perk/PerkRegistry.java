/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.perks.active.*;
import eu.darkcube.minigame.woolbattle.perk.perks.other.*;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PerkRegistry {

    private final Map<PerkName, Perk> perks = new HashMap<>();
    private final Map<ActivationType, Perk[]> cache1 = new HashMap<>();

    public PerkRegistry(WoolBattleBukkit woolbattle) {
        register(new SlimePlatformPerk(woolbattle));
        register(new CapsulePerk(woolbattle));
        register(new SwitcherPerk(woolbattle));
        register(new LineBuilderPerk(woolbattle));
        register(new WoolBombPerk(woolbattle));
        register(new RonjasToiletFlushPerk(woolbattle));
        register(new SafetyPlatformPerk(woolbattle));
        register(new WallGeneratorPerk(woolbattle));
        register(new BlinkPerk(woolbattle));
        register(new GrandpasClockPerk(woolbattle));
        register(new GhostPerk(woolbattle));
        register(new GrabberPerk(woolbattle));
        register(new MinigunPerk(woolbattle));
        register(new BoosterPerk(woolbattle));
        register(new GrapplingHookPerk(woolbattle));
        register(new RopePerk(woolbattle));
        register(new FreezerPerk(woolbattle));
        register(new ExtraWoolPerk());
        register(new RocketJumpPerk());
        register(new LongJumpPerk());
        register(new ArrowRainPerk());
        register(new FastArrowPerk());
        register(new TntArrowPerk(woolbattle));
        register(new EnderPearlPerk(woolbattle));
        register(new BowPerk(woolbattle));
        register(new ArrowPerk(woolbattle));
        register(new ShearsPerk(woolbattle));
        register(new ArrowBombPerk(woolbattle));
        register(new ProtectiveShieldPerk(woolbattle));
        register(new DoubleJumpPerk(woolbattle));
        register(new MinePerk(woolbattle));
        register(new ElevatorPerk());
        register(new ReflectorPerk(woolbattle));
        register(new ScampPerk());
        register(new SpiderPerk(woolbattle));
        register(new HookArrowPerk(woolbattle));
        register(new DrawArrowPerk(woolbattle));
        register(new PiercingArrowPerk(woolbattle));
//        register(new KnockbackArrowPerk(woolbattle));
        register(new FreezeArrowPerk(woolbattle));
        register(new BerserkerPerk(woolbattle));
        register(new StomperPerk(woolbattle));
    }

    public void register(Perk perk) {
        perks.put(perk.perkName(), perk);
        cache1.remove(perk.activationType());
    }

    public Perk[] perks(ActivationType activationType) {
        return cache1
                .computeIfAbsent(activationType, n -> perks.values().stream().filter(p -> p.activationType() == n).toArray(Perk[]::new))
                .clone();
    }

    public Map<PerkName, Perk> perks() {
        return Collections.unmodifiableMap(perks);
    }
}

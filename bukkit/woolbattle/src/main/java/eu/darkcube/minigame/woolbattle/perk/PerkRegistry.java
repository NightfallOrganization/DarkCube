/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk;

import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.perks.active.*;
import eu.darkcube.minigame.woolbattle.perk.perks.other.ArrowPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.other.BowPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.other.EnderPearlPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.other.ShearsPerk;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PerkRegistry {

	private final Map<PerkName, Perk> perks = new HashMap<>();
	private final Map<ActivationType, Perk[]> cache1 = new HashMap<>();

	public PerkRegistry() {
		register(new SlimePlatformPerk());
		register(new CapsulePerk());
		register(new SwitcherPerk());
		register(new LineBuilderPerk());
		register(new WoolBombPerk());
		register(new RonjasToiletFlushPerk());
		register(new SafetyPlatformPerk());
		register(new WallGeneratorPerk());
		register(new BlinkPerk());
		register(new GrandpasClockPerk());
		register(new GhostPerk());
		register(new GrabberPerk());
		register(new MinigunPerk());
		register(new BoosterPerk());
		register(new GrapplingHookPerk());
		register(new RopePerk());
		register(new FreezerPerk());
		register(new ExtraWoolPerk());
		register(new RocketJumpPerk());
		register(new LongJumpPerk());
		register(new ArrowRainPerk());
		register(new FastArrowPerk());
		register(new TntArrowPerk());
		register(new EnderPearlPerk());
		register(new BowPerk());
		register(new ArrowPerk());
		register(new ShearsPerk());
	}

	public void register(Perk perk) {
		perks.put(perk.perkName(), perk);
	}

	public Perk[] perks(ActivationType activationType) {
		return cache1.computeIfAbsent(activationType,
				n -> perks.values().stream().filter(p -> p.activationType() == n)
						.toArray(Perk[]::new));
	}

	public Map<PerkName, Perk> perks() {
		return Collections.unmodifiableMap(perks);
	}
}

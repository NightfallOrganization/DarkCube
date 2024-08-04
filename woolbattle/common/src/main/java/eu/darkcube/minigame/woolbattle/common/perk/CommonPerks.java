/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.perk;

import eu.darkcube.minigame.woolbattle.api.event.perk.RegisterPerksEvent;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.ArrowBombPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.ArrowBombPerk2;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.BlinkPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.BoosterPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.CapsulePerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.FreezerPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.GhostPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.GrabberPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.GrandpasClockPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.GrapplingHookPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.LineBuilderPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.MinePerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.MinigunPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.PodPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.ProtectiveShieldPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.RonjasToiletFlushPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.RopePerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.SafetyPlatformPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.SlimePlatformPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.SwitcherPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.WallGeneratorPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.WoolBombPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.other.ArrowPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.other.BowPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.other.DoubleJumpPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.other.EnderPearlPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.other.ShearsPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.ArrowRainPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.BerserkerPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.DrawArrowPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.ElevatorPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.ExtraWoolPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.FastArrowPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.FreezeArrowPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.HookArrowPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.KnockbackArrowPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.LongJumpPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.PiercingArrowPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.ReflectorPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.RocketJumpPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.ScampPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.SpiderPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.StomperPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.TntArrowPerk;

public class CommonPerks {
    public static void register(CommonGame game) {
        var perkRegistry = game.perkRegistry();
        perkRegistry.register(new ArrowBombPerk(game));
        perkRegistry.register(new ArrowPerk(game));

        perkRegistry.register(new ArrowBombPerk2(game)); // TODO REMOVE THIS AGAIN

        // TODO
        perkRegistry.register(new SlimePlatformPerk(game));
        perkRegistry.register(new CapsulePerk(game));
        perkRegistry.register(new SwitcherPerk(game));
        perkRegistry.register(new LineBuilderPerk(game));
        perkRegistry.register(new WoolBombPerk(game));
        perkRegistry.register(new RonjasToiletFlushPerk(game));
        perkRegistry.register(new SafetyPlatformPerk(game));
        perkRegistry.register(new WallGeneratorPerk(game));
        perkRegistry.register(new BlinkPerk(game));
        perkRegistry.register(new GrandpasClockPerk(game));
        perkRegistry.register(new GhostPerk(game));
        perkRegistry.register(new GrabberPerk(game));
        perkRegistry.register(new MinigunPerk(game));
        perkRegistry.register(new BoosterPerk(game));
        perkRegistry.register(new GrapplingHookPerk(game));
        perkRegistry.register(new RopePerk(game));
        perkRegistry.register(new FreezerPerk(game));
        perkRegistry.register(new MinePerk(game));
        perkRegistry.register(new PodPerk(game));
        perkRegistry.register(new ProtectiveShieldPerk(game));

        perkRegistry.register(new ExtraWoolPerk(game));
        perkRegistry.register(new RocketJumpPerk(game));
        perkRegistry.register(new LongJumpPerk(game));
        perkRegistry.register(new ArrowRainPerk(game));
        perkRegistry.register(new FastArrowPerk(game));
        perkRegistry.register(new TntArrowPerk(game));
        perkRegistry.register(new StomperPerk(game));
        perkRegistry.register(new BerserkerPerk(game));
        perkRegistry.register(new ElevatorPerk(game));
        perkRegistry.register(new ReflectorPerk(game));
        perkRegistry.register(new ScampPerk(game));
        perkRegistry.register(new SpiderPerk(game));
        perkRegistry.register(new HookArrowPerk(game));
        perkRegistry.register(new DrawArrowPerk(game));
        perkRegistry.register(new PiercingArrowPerk(game));
        perkRegistry.register(new KnockbackArrowPerk(game));
        perkRegistry.register(new FreezeArrowPerk(game));

        perkRegistry.register(new EnderPearlPerk(game));
        perkRegistry.register(new BowPerk(game));
        perkRegistry.register(new ShearsPerk(game));
        perkRegistry.register(new DoubleJumpPerk(game));

        game.woolbattle().eventManager().call(new RegisterPerksEvent(game, perkRegistry));
    }
}

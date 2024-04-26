/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.effect;

import java.util.Random;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum MinersEffect {

    HASTE_1(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 0, true, false)),
    HASTE_2(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 1, true, false)),
    SPEED_1(new PotionEffect(PotionEffectType.SPEED, 600, 0, true, false)),
    SPEED_2(new PotionEffect(PotionEffectType.SPEED, 600, 1, true, false)),
    RESISTANCE_1(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 0, true, false)),
    REGENERATION_1(new PotionEffect(PotionEffectType.REGENERATION, 600, 0, true, false)),
    REGENERATION_2(new PotionEffect(PotionEffectType.REGENERATION, 600, 1, true, false)),
    MINING_FATIGUE_1(new PotionEffect(PotionEffectType.SLOW_DIGGING, 600, 0, true, false)),
    SLOWNESS_1(new PotionEffect(PotionEffectType.SLOW, 600, 0, true, false)),
    POISON_1(new PotionEffect(PotionEffectType.POISON, 600, 0, true, false));

    final PotionEffect effect;

    MinersEffect(PotionEffect effect) {
        this.effect = effect;
    }

    public PotionEffect getEffect() {
        return effect;
    }

    private final static Random RANDOM = new Random();

    public static MinersEffect getRandomEffect() {
        return values()[RANDOM.nextInt(values().length)];
    }

}

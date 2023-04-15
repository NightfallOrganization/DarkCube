/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.effect;

import org.bukkit.potion.PotionEffectType;

public enum PotionEffect {

	SPEED(PotionEffectType.SPEED, "speed"),
	SLOW(PotionEffectType.SLOW, "slowness"),
	FAST_DIGGING(PotionEffectType.FAST_DIGGING, "haste"),
	SLOW_DIGGING(PotionEffectType.SLOW_DIGGING, "mining_fatigue"),
	INCREASE_DAMAGE(PotionEffectType.INCREASE_DAMAGE, "strength"),
	HEAL(PotionEffectType.HEAL, "instant_health"),
	HARM(PotionEffectType.HARM, "instant_damage"),
	JUMP(PotionEffectType.JUMP, "jump_boost"),
	CONFUSION(PotionEffectType.CONFUSION, "nausea"),
	REGENERATION(PotionEffectType.REGENERATION, "regeneration"),
	DAMAGE_RESISTANCE(PotionEffectType.DAMAGE_RESISTANCE, "resistance"),
	FIRE_RESISTANCE(PotionEffectType.FIRE_RESISTANCE, "fire_resistance"),
	WATER_BREATHING(PotionEffectType.WATER_BREATHING, "water_breathing"),
	INVISIBILITY(PotionEffectType.INVISIBILITY, "invisibility"),
	BLINDNESS(PotionEffectType.BLINDNESS, "blindness"),
	NIGHT_VISION(PotionEffectType.NIGHT_VISION, "night_vision"),
	HUNGER(PotionEffectType.HUNGER, "hunger"),
	WEAKNESS(PotionEffectType.WEAKNESS, "weakness"),
	POISON(PotionEffectType.POISON, "poison"),
	WITHER(PotionEffectType.WITHER, "wither"),
	HEALTH_BOOST(PotionEffectType.HEALTH_BOOST, "health_boost"),
	ABSORPTION(PotionEffectType.ABSORPTION, "absorption"),
	SATURATION(PotionEffectType.SATURATION, "saturation"),

	;

	private final PotionEffectType type;
	private final String key;

	private PotionEffect(PotionEffectType type, String key) {
		this.type = type;
		this.key = key;
	}

	public PotionEffectType getType() {
		return type;
	}
	
	public String getKey() {
		return key;
	}
}

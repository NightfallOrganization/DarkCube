package eu.darkcube.system.miners.effect;

import java.util.Random;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum MinersEffect {

	HASTE_1(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 0, true, false))

	;

	PotionEffect effect;

	private MinersEffect(PotionEffect effect) {
		this.effect = effect;
	}

	public PotionEffect getEffect() {
		return effect;
	}

	private static Random rand = new Random();

	public static MinersEffect getRandomEffect() {
		return values()[rand.nextInt(values().length)];
	}

}

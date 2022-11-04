package de.pixel.bedwars.shop;

import static org.bukkit.enchantments.Enchantment.*;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import de.pixel.bedwars.util.I18n;
import de.pixel.bedwars.util.RomDecUtil;

public enum ShopEnchant {

	S_KNOCKBACK("S_KNOCKBACK", KNOCKBACK),
	S_SHARPNESS("S_SHARPNESS", DAMAGE_ALL),
	S_EFFICIENCY("S_EFFICIENCY", DIG_SPEED),

	;

	private final String id;
	private final Enchantment enchant;

	private ShopEnchant(String id, Enchantment enchant) {
		this.id = id;
		this.enchant = enchant;
	}

	public String getId() {
		return id;
	}
	
	public String getName(Player p) {
		return I18n.translate(I18n.getPlayerLanguage(p), id);
	}

	public Enchantment getEnchant() {
		return enchant;
	}
	
	public static String translateLevel(Player p, int level) {
		return RomDecUtil.dec2rom(level);
	}
}

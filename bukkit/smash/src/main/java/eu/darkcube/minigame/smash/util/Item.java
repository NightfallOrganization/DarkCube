package eu.darkcube.minigame.smash.util;

import static eu.darkcube.minigame.smash.util.ItemBuilder.*;
import static org.bukkit.Material.*;

import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.smash.user.User;

public enum Item {

	VOTING_LIFES(item(INK_SACK).setDurability((short) 1).addLore()),
	VOTING_LIFES_ITEM(item(INK_SACK).setDurability((short) 1).addLore()),
	VOTING_MAPS(item(PAPER).addLore()),

	UP_SMASH(item(DIAMOND_HOE).setDurability((short) 2)),

	DOWN_SMASH(item(DIAMOND_HOE).setDurability((short) 3)),

	FRONT_SMASH(item(DIAMOND_HOE).setDurability((short) 4)),

	BASE_SMASH(item(DIAMOND_HOE).setDurability((short) 5)),

	JUMP_SMASH(item(DIAMOND_HOE).setDurability((short) 6)),

	SHIELD_SMASH(item(DIAMOND_HOE).setDurability((short) 7)),

	;

	private final ItemBuilder builder;

	Item(ItemBuilder builder) {
		this.builder = builder;
	}

	public ItemBuilder getBuilder() {
		return new ItemBuilder(builder);
	}

	public String getDisplayName(User user) {
		return getDisplayName(user, new String[0]);
	}

	public String getItemId() {
		return ItemManager.getItemId(this);
	}

	public String getDisplayName(User user, String... replacements) {
		return ItemManager.getDisplayName(this, user, replacements);
	}

	public ItemStack getItem(User user) {
		return ItemManager.getItem(this, user);
	}

	public ItemStack getItem(User user, String... replacements) {
		return ItemManager.getItem(this, user, replacements);
	}

	public ItemStack getItem(User user, String[] replacements, String... loreReplacements) {
		return ItemManager.getItem(this, user, replacements, loreReplacements);
	}
}

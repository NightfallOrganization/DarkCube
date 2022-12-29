/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.perk;

import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;

public enum PerkType implements Comparable<PerkType> {

	SLIME_PLATFORM(Item.PERK_SLIME_PLATFORM, Item.PERK_SLIME_PLATFORM_COOLDOWN, 20, PerkName.SLIME_PLATFORM, false, 18),

	CAPSULE(Item.PERK_CAPSULE, Item.PERK_CAPSULE_COOLDOWN, 30, PerkName.CAPSULE, false, 24),

	SWITCHER(Item.PERK_SWITCHER, Item.PERK_SWITCHER_COOLDOWN, 7, PerkName.SWITCHER, false, 8),

	LINE_BUILDER(Item.PERK_LINE_BUILDER, Item.PERK_LINE_BUILDER_COOLDOWN, 10, PerkName.LINE_BUILDER, false, 2, true),

	WOOL_BOMB(Item.PERK_WOOL_BOMB, Item.PERK_WOOL_BOMB_COOLDOWN, 14, PerkName.WOOL_BOMB, false, 8),

	RONJAS_TOILET_SPLASH(Item.PERK_RONJAS_TOILET_SPLASH, Item.PERK_RONJAS_TOILET_SPLASH_COOLDOWN, 13,
			PerkName.RONJAS_TOILET_SPLASH, false, 12),

	SAFETY_PLATFORM(Item.PERK_SAFETY_PLATFORM, Item.PERK_SAFETY_PLATFORM_COOLDOWN, 25, PerkName.SAFETY_PLATFORM, false,
			24),

	WALL_GENERATOR(Item.PERK_WALL_GENERATOR, Item.PERK_WALL_GENERATOR_COOLDOWN, 9, PerkName.WALL_GENERATOR, false, 1,
			true),

	GRANDPAS_CLOCK(Item.PERK_GRANDPAS_CLOCK, Item.PERK_GRANDPAS_CLOCK_COOLDOWN, 16, PerkName.GRANDPAS_CLOCK, false, 18),

	GHOST(Item.PERK_GHOST, Item.PERK_GHOST_COOLDOWN, 30, PerkName.GHOST, false, 20),

	BLINK(Item.PERK_BLINK, Item.PERK_BLINK_COOLDOWN, 15, PerkName.BLINK, false, 12),

	MINIGUN(Item.PERK_MINIGUN, Item.PERK_MINIGUN_COOLDOWN, 10, PerkName.MINIGUN, false, 1),

	GRABBER(Item.PERK_GRABBER, Item.PERK_GRABBER_COOLDOWN, 7, PerkName.GRABBER, false, 10),

	BOOSTER(Item.PERK_BOOSTER, Item.PERK_BOOSTER_COOLDOWN, 18, PerkName.BOOSTER, false, 12),

	GRAPPLING_HOOK(Item.PERK_GRAPPLING_HOOK, Item.PERK_GRAPPLING_HOOK_COOLDOWN, 12, PerkName.GRAPPLING_HOOK, false, 12),

	ROPE(Item.PERK_ROPE, Item.PERK_ROPE_COOLDOWN, 12, PerkName.ROPE, false, 12),

	EXTRA_WOOL(Item.PERK_EXTRA_WOOL, Item.PERK_EXTRA_WOOL, 0, PerkName.EXTRA_WOOL, true, 0),

//	BACKPACK(Item.PERK_BACKPACK, Item.PERK_BACKPACK, 0, PerkName.BACKPACK, true,
//					0),

	LONGJUMP(Item.PERK_LONGJUMP, Item.PERK_LONGJUMP, 0, PerkName.LONGJUMP, true, 0),
	ROCKETJUMP(Item.PERK_ROCKETJUMP, Item.PERK_ROCKETJUMP, 0, PerkName.ROCKETJUMP, true, 0),

	ARROW_RAIN(Item.PERK_ARROW_RAIN, Item.PERK_ARROW_RAIN_COOLDOWN, 6, PerkName.ARROW_RAIN, true, 10),

	FAST_ARROW(Item.PERK_FAST_ARROW, Item.PERK_FAST_ARROW, 0, PerkName.FAST_ARROW, true, 0),

	TNT_ARROW(Item.PERK_TNT_ARROW, Item.PERK_TNT_ARROW_COOLDOWN, 7, PerkName.TNT_ARROW, true, 16),

//	ELEVATOR(Item.PERK_ELEVATOR, Item.PERK_ELEVATOR_COOLDOWN, 5, PerkName.ELEVATOR, true, 8),

	;

	private final Item item;

	private final Item cooldownItem;

	private final PerkName perkName;

	private int cooldown;

	private int cost;

	private final int defaultCost;

	private final int defaultCooldown;

	private final boolean passive;

	private final boolean isCostPerBlock;

	PerkType(Item item, Item cooldownItem, int cooldown, PerkName perkName, boolean passive, int cost,
			boolean costPerBlock) {
		this.cost = cost;
		this.defaultCost = cost;
		this.passive = passive;
		this.cooldownItem = cooldownItem;
		this.item = item;
		this.perkName = perkName;
		this.defaultCooldown = cooldown;
		this.cooldown = cooldown;
		this.isCostPerBlock = costPerBlock;

		this.item.setPerk(this);
		this.cooldownItem.setPerk(this);
	}

	PerkType(Item item, Item cooldownItem, int cooldown, PerkName perkName, boolean passive, int cost) {
		this(item, cooldownItem, cooldown, perkName, passive, cost, false);
	}

	public boolean isCostPerBlock() {
		return this.isCostPerBlock;
	}

	public int getDefaultCost() {
		return this.defaultCost;
	}

	public int getDefaultCooldown() {
		return this.defaultCooldown;
	}

	public Perk newPerkTypePerk(User owner, PerkNumber perkNumber) {
		return new PerkTypePerk(this, owner, perkNumber);
	}

	public PerkName getPerkName() {
		return this.perkName;
	}

	public Item getItem() {
		return this.item;
	}

	public int getCost() {
		return this.cost;
	}

	public boolean isPassive() {
		return this.passive;
	}

	public Item getCooldownItem() {
		return this.cooldownItem;
	}

	public boolean hasCooldown() {
		return this.getCooldown() != 0;
	}

	public int getCooldown() {
		return this.cooldown;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public static PerkType valueOf(PerkName name) {
		for (PerkType type : PerkType.values()) {
			if (type.getPerkName().equals(name))
				return type;
		}
		return null;
	}

}

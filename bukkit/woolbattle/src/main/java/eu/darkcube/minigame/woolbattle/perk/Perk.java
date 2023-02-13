/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk;

import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;

public class Perk {

	private final ActivationType activationType;
	private final PerkName perkName;
	private final int defaultCooldown;
	private final int defaultCost;
	private final CostType costType;
	private final Item defaultItem;
	private final UserPerkCreator perkCreator;
	private int cooldown;
	private int cost;

	public Perk(ActivationType activationType, PerkName perkName, int cooldown, int cost,
			Item defaultItem, UserPerkCreator perkCreator) {
		this(activationType, perkName, cooldown, cost, CostType.PER_ACTIVATION, defaultItem,
				perkCreator);
	}

	public Perk(ActivationType activationType, PerkName perkName, int cooldown, int cost,
			CostType costType, Item defaultItem, UserPerkCreator perkCreator) {
		this.activationType = activationType;
		this.perkName = perkName;
		this.defaultCooldown = cooldown;
		this.defaultCost = cost;
		this.costType = costType;
		this.cost = cost;
		this.cooldown = cooldown;
		this.defaultItem = defaultItem;
		this.perkCreator = perkCreator;
	}

	public UserPerkCreator perkCreator() {
		return perkCreator;
	}

	public ActivationType activationType() {
		return activationType;
	}

	public Item defaultItem() {
		return defaultItem;
	}

	public PerkName perkName() {
		return perkName;
	}

	public CostType costType() {
		return costType;
	}

	public int cost() {
		return cost;
	}

	public int cooldown() {
		return cooldown;
	}

	public void cooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	public void cost(int cost) {
		this.cost = cost;
	}

	public int defaultCooldown() {
		return defaultCooldown;
	}

	public int defaultCost() {
		return defaultCost;
	}

	public enum CostType {
		PER_BLOCK, PER_SHOT, PER_ACTIVATION
	}

	/**
	 * This is the class for all perk types. May need renaming in the future, {@code ActivationType}
	 * is not quite accurate.<br><br>
	 * <p>
	 * {@link ActivationType#PRIMARY_WEAPON PRIMARY_WEAPON},
	 * {@link ActivationType#SECONDARY_WEAPON SECONDARY_WEAPON} and
	 * {@link ActivationType#ARROW ARROW} are perks internally, makes it easier to track with
	 * existing system and helps with code reuse. This may also help for the future to make them
	 * changeable
	 */
	public enum ActivationType implements Comparable<ActivationType> {
		// Order is important here, as the inventory will be filled in that order for new players
		PRIMARY_WEAPON("primaryWeapon", 1, Item.DEFAULT_BOW),
		SECONDARY_WEAPON("secondaryWeapon", 1, Item.DEFAULT_SHEARS),

		ACTIVE("active", 2, Item.PERKS_ACTIVE),
		MISC("misc", 1, Item.PERKS_MISC),
		PASSIVE("passive", 1, Item.PERKS_PASSIVE),

		ARROW("arrow", 1, Item.DEFAULT_ARROW);
		private final String type;
		private final int maxCount;
		private final Item displayItem;

		ActivationType(String type, int maxCount, Item displayItem) {
			this.type = type;
			this.maxCount = maxCount;
			this.displayItem = displayItem;
		}

		public Item displayItem() {
			return displayItem;
		}

		public String type() {
			return type;
		}

		public int maxCount() {
			return maxCount;
		}

		@Override
		public String toString() {
			return type;
		}

	}

	/**
	 * Functional interface for creating new instances of {@link UserPerk}s. Every {@link Perk perk}
	 * must have one.
	 */
	public interface UserPerkCreator {
		UserPerk create(WBUser user, Perk perk, int id, int perkSlot);
	}
}

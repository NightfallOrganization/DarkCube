/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.inventoryapi.item.attribute;

import eu.darkcube.system.util.data.Key;

import java.util.Objects;

@SuppressWarnings("StaticInitializerReferencesSubClass")
public class Attribute {

	public static final Attribute GENERIC_MAX_HEALTH =
			new VersionedAttribute("generic" + ".max_health"), GENERIC_FOLLOW_RANGE =
			new VersionedAttribute("generic.follow_range"), GENERIC_KNOCKBACK_RESISTANCE =
			new VersionedAttribute("generic.knockback_resistance"), GENERIC_MOVEMENT_SPEED =
			new VersionedAttribute("generic.movement_speed"), GENERIC_ATTACK_DAMAGE =
			new VersionedAttribute("generic.attack_damage"), GENERIC_ARMOR =
			new VersionedAttribute("generic.armor"), GENERIC_ARMOR_TOUGHNESS =
			new VersionedAttribute("generic.armor_toughness"), GENERIC_ATTACK_KNOCKBACK =
			new VersionedAttribute("generic.attack_knockback"), GENERIC_ATTACK_SPEED =
			new VersionedAttribute("generic.attack_speed"), GENERIC_LUCK =
			new VersionedAttribute("generic.luck"), HORSE_JUMP_STRENGTH =
			new VersionedAttribute("horse.jump_strength"), GENERIC_FLYING_SPEED =
			new VersionedAttribute("generic.flying_speed"), ZOMBIE_SPAWN_REINFORCEMENTS =
			new VersionedAttribute("zombie.spawn_reinforcements");

	private final Key attribute;

	public Attribute(Key attribute) {
		this.attribute = attribute;
	}

	public Key attribute() {
		return attribute;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Attribute))
			return false;
		Attribute attribute1 = (Attribute) o;
		return Objects.equals(attribute, attribute1.attribute);
	}

	@Override
	public String toString() {
		return attribute.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(attribute);
	}

	public static class VersionedAttribute extends Attribute {

		public VersionedAttribute(String minecraft) {
			this(new Key("minecraft", minecraft));
		}

		public VersionedAttribute(Key newestVersion) {
			super(convert(newestVersion));
		}

		public static Key convert(Key newestVersion) {
			return newestVersion;
		}
	}
}

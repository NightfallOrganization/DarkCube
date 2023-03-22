/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.inventoryapi.item.attribute;

import eu.darkcube.system.inventoryapi.item.EquipmentSlot;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class AttributeModifier {

	private final UUID uuid;
	private final String name;
	private final double amount;
	private final Operation operation;
	private final EquipmentSlot equipmentSlot;

	public AttributeModifier(@NotNull String name, double amount, @NotNull Operation operation) {
		this(UUID.randomUUID(), name, amount, operation);
	}

	public AttributeModifier(@NotNull UUID uuid, @NotNull String name, double amount,
			@NotNull Operation operation) {
		this(uuid, name, amount, operation, null);
	}

	public AttributeModifier(@NotNull UUID uuid, @NotNull String name, double amount,
			@NotNull Operation operation, @Nullable EquipmentSlot equipmentSlot) {
		this.uuid = uuid;
		this.name = name;
		this.amount = amount;
		this.operation = operation;
		this.equipmentSlot = equipmentSlot;
	}

	public UUID uniqueId() {
		return uuid;
	}

	public String name() {
		return name;
	}

	public double amount() {
		return amount;
	}

	public Operation operation() {
		return operation;
	}

	public EquipmentSlot equipmentSlot() {
		return equipmentSlot;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AttributeModifier that = (AttributeModifier) o;
		return Double.compare(that.amount, amount) == 0 && uuid.equals(that.uuid) && name.equals(
				that.name) && operation == that.operation && equipmentSlot == that.equipmentSlot;
	}

	@Override
	public String toString() {
		return "AttributeModifier{" + "uuid=" + uuid + ", name='" + name + '\'' + ", amount="
				+ amount + ", operation=" + operation + ", equipmentSlot=" + equipmentSlot + '}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, name, amount, operation, equipmentSlot);
	}
}

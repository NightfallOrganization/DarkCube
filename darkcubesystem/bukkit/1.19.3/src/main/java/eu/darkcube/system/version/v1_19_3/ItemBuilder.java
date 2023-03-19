/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version.v1_19_3;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.inventoryapi.item.AbstractItemBuilder;
import eu.darkcube.system.inventoryapi.item.meta.BuilderMeta;
import eu.darkcube.system.inventoryapi.item.meta.FireworkBuilderMeta;
import eu.darkcube.system.inventoryapi.item.meta.LeatherArmorBuilderMeta;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta.UserProfile;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta.UserProfile.Texture;
import eu.darkcube.system.util.data.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class ItemBuilder extends AbstractItemBuilder {
	private static final Logger LOGGER = Logger.getLogger("ItemBuilder");
	private static final NamespacedKey persistentDataKey =
			new NamespacedKey(DarkCubePlugin.systemPlugin(), "persistentDataStorage");

	public ItemBuilder() {
	}

	public ItemBuilder(ItemStack item) {
		material(item.getType());
		amount(item.getAmount());
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			unbreakable(meta.isUnbreakable());
			if (meta.hasDisplayName())
				displayname(AdventureUtils.convert(meta.displayName()));
			for (Map.Entry<Enchantment, Integer> e : meta.getEnchants().entrySet()) {
				enchant(e.getKey(), e.getValue());
			}

			if (meta.getAttributeModifiers() != null)
				meta.getAttributeModifiers().forEach((attribute, attributeModifier) -> {
					eu.darkcube.system.inventoryapi.item.attribute.Attribute sa =
							new eu.darkcube.system.inventoryapi.item.attribute.Attribute(
									new Key(attribute.key().namespace(), attribute.key().value()));
					eu.darkcube.system.inventoryapi.item.attribute.Operation so =
							eu.darkcube.system.inventoryapi.item.attribute.Operation.values()[attributeModifier.getOperation()
									.ordinal()];
					eu.darkcube.system.inventoryapi.item.EquipmentSlot ses =
							attributeModifier.getSlot() == null
									? null
									: eu.darkcube.system.inventoryapi.item.EquipmentSlot.valueOf(
											attributeModifier.getSlot().name());
					attributeModifier(sa,
							new eu.darkcube.system.inventoryapi.item.attribute.AttributeModifier(
									attributeModifier.getUniqueId(), attributeModifier.getName(),
									attributeModifier.getAmount(), so, ses));
				});

			setFlags(meta.getItemFlags());
			if (meta.lore() != null)
				lore(AdventureUtils.convert2(Objects.requireNonNull(meta.lore())));
			if (meta instanceof Damageable dmeta)
				damage(dmeta.getDamage());
			if (meta instanceof FireworkEffectMeta fmeta)
				meta(FireworkBuilderMeta.class).setFireworkEffect(fmeta.getEffect());
			if (meta instanceof SkullMeta smeta) {
				PlayerProfile pp = smeta.getPlayerProfile();
				if (pp != null) {
					ProfileProperty prop =
							pp.getProperties().stream().filter(p -> p.getName().equals("textures"))
									.findFirst().orElse(null);
					Texture texture =
							prop == null ? null : new Texture(prop.getValue(),
									prop.getSignature());
					UserProfile up = new UserProfile(pp.getName(), pp.getId(), texture);
					meta(SkullBuilderMeta.class).setOwningPlayer(up);
				}
			}
			if (meta instanceof LeatherArmorMeta lmeta)
				meta(LeatherArmorBuilderMeta.class).setColor(lmeta.getColor());
			if (meta.getPersistentDataContainer().has(persistentDataKey)) {
				storage.getData().append(JsonDocument.newDocument(meta.getPersistentDataContainer()
						.get(persistentDataKey, PersistentDataType.STRING)));
			}
		}
	}

	@Override
	public ItemStack build() {
		ItemStack item = new ItemStack(material);
		item.setAmount(amount);
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.setUnbreakable(unbreakable);
			Component prefix = Component.empty().decoration(TextDecoration.ITALIC, false);
			if (displayname != null) {
				meta.displayName(prefix.append(AdventureUtils.convert(displayname)));
			}
			for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
				meta.addEnchant(e.getKey(), e.getValue(), true);
			}

			meta.addItemFlags(flags.toArray(new ItemFlag[0]));
			meta.lore(lore.stream().map(AdventureUtils::convert).map(prefix::append).toList());
			if (glow) {
				if (enchantments.isEmpty()) {
					meta.addEnchant(item.getType() == Material.BOW
									// Intellij inspection is dumb...
									? Enchantment.PROTECTION_ENVIRONMENTAL :
									Enchantment.ARROW_INFINITE, 1,
							true);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
			}
			attributeModifiers.forEach((attribute, modifier) -> {
				Attribute ba = null;
				for (Attribute value : Attribute.values()) {
					if (value.key().toString().equals(attribute.attribute().toString())) {
						ba = value;
						break;
					}
				}
				if (ba == null) {
					LOGGER.warning("Unknown attribute: " + attribute.attribute().toString());
				} else {
					Operation operation = Operation.values()[modifier.operation().ordinal()];
					EquipmentSlot equipmentSlot = modifier.equipmentSlot() == null
							? null
							: EquipmentSlot.valueOf(modifier.equipmentSlot().name());
					AttributeModifier bam =
							new AttributeModifier(modifier.uniqueId(), modifier.name(),
									modifier.amount(), operation, equipmentSlot);
					meta.addAttributeModifier(ba, bam);
				}
			});
			if (meta instanceof Damageable dmeta) {
				dmeta.setDamage(damage);
			}
			for (BuilderMeta builderMeta : metas) {
				if (builderMeta instanceof FireworkBuilderMeta) {
					((FireworkEffectMeta) meta).setEffect(
							((FireworkBuilderMeta) builderMeta).getFireworkEffect());
				} else if (builderMeta instanceof SkullBuilderMeta bmeta) {
					SkullMeta smeta = (SkullMeta) meta;
					UserProfile owner = bmeta.getOwningPlayer();
					Texture texture = owner.getTexture();
					PlayerProfile profile = Bukkit.getServer().createProfileExact(
							owner.getUniqueId() == null ? UUID.randomUUID() : owner.getUniqueId(),
							owner.getName());
					if (texture != null) {
						profile.clearProperties();
						profile.setProperty(new ProfileProperty("textures", texture.getValue(),
								texture.getSignature()));
					}
					smeta.setPlayerProfile(profile);
				} else if (builderMeta instanceof LeatherArmorBuilderMeta) {
					((LeatherArmorMeta) meta).setColor(
							((LeatherArmorBuilderMeta) builderMeta).getColor());
				} else {
					throw new UnsupportedOperationException(
							"Meta not supported for this mc version: " + builderMeta);
				}
			}
			meta.getPersistentDataContainer()
					.set(persistentDataKey, PersistentDataType.STRING, storage.getData().toJson());
			item.setItemMeta(meta);
		} else {
			throw new IllegalArgumentException("Item without Meta: " + material);
		}
		return item;
	}

	@Override
	public AbstractItemBuilder clone() {
		AbstractItemBuilder builder =
				new ItemBuilder().amount(amount).damage(damage).displayname(displayname)
						.enchantments(enchantments).flag(flags).glow(glow).lore(lore)
						.material(material).unbreakable(unbreakable).metas(metas)
						.attributeModifiers(attributeModifiers);
		builder.persistentDataStorage().getData().append(storage.getData());
		return builder;
	}
}

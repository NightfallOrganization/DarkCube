/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.inventoryapi.item.AbstractItemBuilder;
import eu.darkcube.system.inventoryapi.item.meta.BuilderMeta;
import eu.darkcube.system.inventoryapi.item.meta.FireworkBuilderMeta;
import eu.darkcube.system.inventoryapi.item.meta.LeatherArmorBuilderMeta;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta.UserProfile;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta.UserProfile.Texture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ItemBuilder_1_19_2 extends AbstractItemBuilder {

	private static final NamespacedKey persistentDataKey =
			new NamespacedKey(DarkCubeSystem.getInstance(), "persistentDataStorage");

	public ItemBuilder_1_19_2() {
	}

	public ItemBuilder_1_19_2(ItemStack item) {
		material(item.getType());
		amount(item.getAmount());
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			unbreakable(meta.isUnbreakable());
			if (meta.hasDisplayName())
				displayname(AdventureUtils_1_19_2.convert(meta.displayName()));
			for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
				enchant(e.getKey(), e.getValue());
			}
			setFlags(meta.getItemFlags());
			if (meta.lore() != null)
				lore(AdventureUtils_1_19_2.convert2(Objects.requireNonNull(meta.lore())));
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
							prop == null ? null : new Texture(prop.getValue(), prop.getSignature());
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
			if (displayname != null) {
				meta.displayName(Component.empty().decoration(TextDecoration.ITALIC, false)
						.append(AdventureUtils_1_19_2.convert(displayname)));
			}
			for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
				meta.addEnchant(e.getKey(), e.getValue(), true);
			}
			meta.addItemFlags(flags.toArray(new ItemFlag[0]));
			meta.lore(AdventureUtils_1_19_2.convert1(lore));
			if (glow) {
				if (enchantments.isEmpty()) {
					meta.addEnchant(material == Material.BOW
									// Intellij inspection is dumb...
									? Enchantment.PROTECTION_ENVIRONMENTAL : Enchantment.ARROW_INFINITE, 1,
							true);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
			}
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
					profile.clearProperties();
					profile.setProperty(new ProfileProperty("textures", texture.getValue(),
							texture.getSignature()));
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
				new ItemBuilder_1_19_2().amount(amount).damage(damage).displayname(displayname)
						.enchantments(enchantments).flag(flags).glow(glow).lore(lore)
						.material(material).unbreakable(unbreakable).metas(metas);
		builder.persistentDataStorage().getData().append(storage.getData());
		return builder;
	}
}

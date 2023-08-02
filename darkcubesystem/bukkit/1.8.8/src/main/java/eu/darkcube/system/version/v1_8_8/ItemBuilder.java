/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version.v1_8_8;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.inventoryapi.item.AbstractItemBuilder;
import eu.darkcube.system.inventoryapi.item.meta.*;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta.UserProfile;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta.UserProfile.Texture;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.util.ReflectionUtils;
import eu.darkcube.system.util.ReflectionUtils.PackageType;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemBuilder extends AbstractItemBuilder {

	private static final Field CraftMetaSkull$profile =
			ReflectionUtils.getField("CraftMetaSkull", PackageType.CRAFTBUKKIT_INVENTORY, true,
					"profile");

	public ItemBuilder() {
	}

	public ItemBuilder(ItemStack item) {
		material(item.getType());
		amount(item.getAmount());
		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			unbreakable(meta.spigot().isUnbreakable());
			String displayname = meta.getDisplayName();
			if (displayname != null)
				displayname(LegacyComponentSerializer.legacySection().deserialize(displayname));
			for (Map.Entry<Enchantment, Integer> e : meta.getEnchants().entrySet()) {
				enchant(e.getKey(), e.getValue());
			}
			setFlags(meta.getItemFlags());
			lore(meta.hasLore() ? meta.getLore().stream()
					.map(LegacyComponentSerializer.legacySection()::deserialize)
					.collect(Collectors.toList()) : new ArrayList<>());
			damage(item.getDurability());
			if (meta instanceof FireworkEffectMeta)
				meta(FireworkBuilderMeta.class).fireworkEffect(
						((FireworkEffectMeta) meta).getEffect());
			if (meta instanceof SkullMeta) {
				GameProfile pp =
						(GameProfile) ReflectionUtils.getValue(meta, CraftMetaSkull$profile);
				if (pp != null) {
					Property prop = pp.getProperties().containsKey("textures") ? pp.getProperties()
							.get("textures").stream().findFirst().orElse(null) : null;
					Texture texture =
							prop == null ? null : new Texture(prop.getValue(),
									prop.getSignature());
					UserProfile up = new UserProfile(pp.getName(), pp.getId(), texture);
					meta(SkullBuilderMeta.class).owningPlayer(up);
				}
			}
			if (meta instanceof LeatherArmorMeta)
				meta(LeatherArmorBuilderMeta.class).color(((LeatherArmorMeta) meta).getColor());
			net.minecraft.server.v1_8_R3.ItemStack mci = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tag = mci.getTag();
			if (tag != null) {
				if (tag.hasKey("System:persistentDataStorage")) {
					String s = tag.getString("System:persistentDataStorage");
					tag.remove("System:persistentDataStorage");
					tag.setString("system:persistent_data_storage", s);
				}
				if (tag.hasKey("system:persistent_data_storage")) {
					storage.loadFromJsonDocument(JsonDocument.newDocument(
							tag.getString("system:persistent_data_storage")));
				}

				if (item.getType() == Material.MONSTER_EGG && tag.hasKey("EntityTag")) {
					meta(SpawnEggBuilderMeta.class).entityTag(
							tag.getCompound("EntityTag").toString());
				}
			}
		}
	}

	@Override
	public int repairCost() {
		throw new UnsupportedOperationException();
	}

	@Override
	public AbstractItemBuilder repairCost(int repairCost) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AbstractItemBuilder clone() {
		return new ItemBuilder(build());
	}

	@Override
	public boolean canBeRepairedBy(eu.darkcube.system.inventoryapi.item.ItemBuilder item) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ItemStack build() {
		ItemStack item = new ItemStack(material);
		item.setAmount(amount);
		item.setDurability((short) damage);
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.spigot().setUnbreakable(unbreakable);
			if (displayname != null)
				meta.setDisplayName(
						LegacyComponentSerializer.legacySection().serialize(displayname));
			for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
				meta.addEnchant(e.getKey(), e.getValue(), true);
			}
			meta.addItemFlags(flags.toArray(new ItemFlag[0]));
			List<String> lore = new ArrayList<>();
			for (Component loreComponent : this.lore) {

				String l = LegacyComponentSerializer.legacySection().serialize(loreComponent);
				String last = null;
				for (String line : l.split("\\R")) {
					if (last != null)
						line = last + line;
					last = ChatColor.getLastColors(line);
					lore.add(line);
				}
			}
			meta.setLore(lore);
			if (glow) {
				if (enchantments.isEmpty()) {
					meta.addEnchant(material == Material.BOW
							? Enchantment.PROTECTION_ENVIRONMENTAL
							: Enchantment.ARROW_INFINITE, 1, true);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
			}
			for (BuilderMeta builderMeta : metas) {
				if (builderMeta instanceof FireworkBuilderMeta) {
					((FireworkEffectMeta) meta).setEffect(
							((FireworkBuilderMeta) builderMeta).fireworkEffect());
				} else if (builderMeta instanceof SkullBuilderMeta) {
					SkullBuilderMeta bmeta = (SkullBuilderMeta) builderMeta;
					UserProfile owner = bmeta.owningPlayer();
					Texture texture = owner.texture();
					GameProfile profile = new GameProfile(
							owner.uniqueId() == null ? UUID.randomUUID() : owner.uniqueId(),
							owner.name());
					if (texture != null)
						profile.getProperties().put("textures",
								new Property("textures", texture.value(), texture.signature()));
					ReflectionUtils.setValue(meta, CraftMetaSkull$profile, profile);
				} else if (builderMeta instanceof LeatherArmorBuilderMeta) {
					((LeatherArmorMeta) meta).setColor(
							((LeatherArmorBuilderMeta) builderMeta).color());
				} else if (builderMeta instanceof SpawnEggBuilderMeta) {
					if (((SpawnEggBuilderMeta) builderMeta).entityTag() != null) {
						item.setItemMeta(meta);
						net.minecraft.server.v1_8_R3.ItemStack mci =
								CraftItemStack.asNMSCopy(item);
						NBTTagCompound tag =
								mci.getTag() == null ? new NBTTagCompound() : mci.getTag();
						try {
							tag.set("EntityTag", MojangsonParser.parse(
									((SpawnEggBuilderMeta) builderMeta).entityTag()));
						} catch (MojangsonParseException e) {
							throw new RuntimeException(e);
						}
						mci.setTag(tag);
						item = CraftItemStack.asBukkitCopy(mci);
						meta = item.getItemMeta();
					}
				} else {
					throw new UnsupportedOperationException(
							"Meta not supported for this mc version: " + builderMeta);
				}
			}
			item.setItemMeta(meta);
			net.minecraft.server.v1_8_R3.ItemStack mci = CraftItemStack.asNMSCopy(item);
			NBTTagCompound tag = mci.getTag() == null ? new NBTTagCompound() : mci.getTag();
			tag.setString("System:persistentDataStorage", storage.storeToJsonDocument().toJson());
			mci.setTag(tag);
			item = CraftItemStack.asBukkitCopy(mci);
		} else {
			throw new IllegalArgumentException("Item without Meta: " + material);
		}
		return item;
	}
}

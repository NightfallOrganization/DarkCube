/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version.v1_20_1;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.inventoryapi.item.AbstractItemBuilder;
import eu.darkcube.system.inventoryapi.item.meta.*;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta.UserProfile;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta.UserProfile.Texture;
import eu.darkcube.system.libs.com.google.gson.*;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;
import eu.darkcube.system.libs.com.google.gson.stream.JsonToken;
import eu.darkcube.system.libs.com.google.gson.stream.JsonWriter;
import eu.darkcube.system.util.data.Key;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class ItemBuilder1_20_1 extends AbstractItemBuilder {
    private static final Logger LOGGER = Logger.getLogger("ItemBuilder");
    private static final NamespacedKey persistentDataKey = new NamespacedKey(DarkCubePlugin.systemPlugin(), "persistentDataStorage");
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(ItemStack.class, new TypeAdapter<ItemStack>() {
        @Override public void write(JsonWriter writer, ItemStack value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(value);
            CompoundTag nbt = new CompoundTag();
            nms.save(nbt);
            writer.value(nbt.toString());
        }

        @Override public ItemStack read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String tag = reader.nextString();
            CompoundTag nbt;
            try {
                nbt = TagParser.parseTag(tag);
            } catch (CommandSyntaxException e) {
                throw new RuntimeException(e);
            }
            net.minecraft.world.item.ItemStack nbtItem = net.minecraft.world.item.ItemStack.of(nbt);
            return CraftItemStack.asBukkitCopy(nbtItem);
        }
    }).create();
    private final ItemStack item;

    public ItemBuilder1_20_1() {
        this.item = null;
    }

    public ItemBuilder1_20_1(ItemStack item) {
        this.item = item.clone();
        item = item.clone();
        item.setItemMeta(item.getItemMeta());
        this.item.setAmount(1);
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        material(item.getType());
        amount(item.getAmount());

        repairCost(nms.getBaseRepairCost());

        ItemMeta meta = this.item.getItemMeta();

        if (meta != null) {
            unbreakable(meta.isUnbreakable());
            meta.setUnbreakable(false);
            if (meta.hasDisplayName()) {
                displayname = AdventureUtils.convert(meta.displayName());
                meta.displayName(null);
            }
            for (Map.Entry<Enchantment, Integer> e : new ArrayList<>(meta.getEnchants().entrySet())) {
                enchant(e.getKey(), e.getValue());
                meta.removeEnchant(e.getKey());
            }

            if (meta.getAttributeModifiers() != null) {
                meta.getAttributeModifiers().forEach((attribute, attributeModifier) -> {
                    eu.darkcube.system.inventoryapi.item.attribute.Attribute sa = new eu.darkcube.system.inventoryapi.item.attribute.Attribute(new Key(attribute
                            .key()
                            .namespace(), attribute.key().value()));
                    eu.darkcube.system.inventoryapi.item.attribute.Operation so = eu.darkcube.system.inventoryapi.item.attribute.Operation.values()[attributeModifier
                            .getOperation()
                            .ordinal()];
                    eu.darkcube.system.inventoryapi.item.EquipmentSlot ses = attributeModifier.getSlot() == null ? null : eu.darkcube.system.inventoryapi.item.EquipmentSlot.valueOf(attributeModifier
                            .getSlot()
                            .name());
                    attributeModifier(sa, new eu.darkcube.system.inventoryapi.item.attribute.AttributeModifier(attributeModifier.getUniqueId(), attributeModifier.getName(), attributeModifier.getAmount(), so, ses));
                });
                meta.setAttributeModifiers(null);
            }

            setFlags(meta.getItemFlags());
            meta.removeItemFlags(ItemFlag.values());
            if (meta.lore() != null) {
                lore.addAll(AdventureUtils.convert2(Objects.requireNonNull(meta.lore())));
                meta.lore(new ArrayList<>());
            }
            if (meta instanceof Damageable dmeta) {
                damage(dmeta.getDamage());
                dmeta.setDamage(0);
            }
            if (meta instanceof FireworkEffectMeta fmeta) {
                meta(FireworkBuilderMeta.class).fireworkEffect(fmeta.getEffect());
                fmeta.setEffect(null);
            }
            if (meta instanceof EnchantmentStorageMeta emeta) {
                meta(EnchantmentStorageBuilderMeta.class).enchantments(emeta.getStoredEnchants());
                for (Enchantment enchantment : emeta.getStoredEnchants().keySet()) {
                    emeta.removeStoredEnchant(enchantment);
                }
            }
            if (meta instanceof SkullMeta smeta) {
                PlayerProfile pp = smeta.getPlayerProfile();
                if (pp != null) {
                    ProfileProperty prop = pp.getProperties().stream().filter(p -> p.getName().equals("textures")).findFirst().orElse(null);
                    Texture texture = prop == null ? null : new Texture(prop.getValue(), prop.getSignature());
                    UserProfile up = new UserProfile(pp.getName(), pp.getId(), texture);
                    meta(SkullBuilderMeta.class).owningPlayer(up);
                }
                smeta.setOwningPlayer(null);
            }
            if (meta instanceof LeatherArmorMeta lmeta) {
                meta(LeatherArmorBuilderMeta.class).color(lmeta.getColor());
                lmeta.setColor(null);
            }
            if (meta.getPersistentDataContainer().has(persistentDataKey)) {
                storage.loadFromJsonDocument(JsonDocument.newDocument(meta
                        .getPersistentDataContainer()
                        .get(persistentDataKey, PersistentDataType.STRING)));
                meta.getPersistentDataContainer().remove(persistentDataKey);
            }
            this.item.setItemMeta(meta);
        }
        ItemStack b = build();
        if (!item.equals(b)) {
            LOGGER.severe("Failed to clone item correctly: ");
            LOGGER.severe(" - " + CraftItemStack.asNMSCopy(item).save(new CompoundTag()));
            LOGGER.severe(" - " + net.minecraft.world.item.ItemStack
                    .of(CraftItemStack.asNMSCopy(item).save(new CompoundTag()))
                    .save(new CompoundTag()));
            LOGGER.severe(" - " + CraftItemStack.asNMSCopy(b).save(new CompoundTag()));
        }
    }

    public static ItemBuilder1_20_1 deserialize(JsonElement json) {
        return new ItemBuilder1_20_1(gson.fromJson(json, ItemStack.class));
    }

    @Override public boolean canBeRepairedBy(eu.darkcube.system.inventoryapi.item.ItemBuilder ingredient) {
        ItemStack item = build();
        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        return nms.getItem().isValidRepairItem(nms, CraftItemStack.asNMSCopy(ingredient.build()));
    }

    @Override public ItemStack build() {
        //		ItemStack item = new ItemStack(material);
        ItemStack item = this.item == null ? new ItemStack(material) : this.item.clone();

        if (material != item.getType()) item.setType(material);

        net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
        nms.setRepairCost(repairCost);
        item = nms.asBukkitMirror();

        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
            if (displayname != null) {
                meta.displayName(AdventureUtils.convert(displayname));
            }
            for (Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
                meta.addEnchant(e.getKey(), e.getValue(), true);
            }

            meta.addItemFlags(flags.toArray(new ItemFlag[0]));
            if (!lore.isEmpty()) meta.lore(lore.stream().map(AdventureUtils::convert).toList());
            if (glow) {
                if (enchantments.isEmpty()) {
                    meta.addEnchant(item.getType() == Material.BOW
                            // Intellij inspection is dumb...
                            ? Enchantment.PROTECTION_ENVIRONMENTAL : Enchantment.ARROW_INFINITE, 1, true);
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
                    EquipmentSlot equipmentSlot = modifier.equipmentSlot() == null ? null : EquipmentSlot.valueOf(modifier
                            .equipmentSlot()
                            .name());
                    AttributeModifier bam = new AttributeModifier(modifier.uniqueId(), modifier.name(), modifier.amount(), operation, equipmentSlot);
                    meta.addAttributeModifier(ba, bam);
                }
            });
            if (meta instanceof Damageable dmeta) {
                dmeta.setDamage(damage);
            }
            for (BuilderMeta builderMeta : metas) {
                if (builderMeta instanceof FireworkBuilderMeta) {
                    ((FireworkEffectMeta) meta).setEffect(((FireworkBuilderMeta) builderMeta).fireworkEffect());
                } else if (builderMeta instanceof SkullBuilderMeta bmeta) {
                    SkullMeta smeta = (SkullMeta) meta;
                    UserProfile owner = bmeta.owningPlayer();
                    Texture texture = owner.texture();
                    PlayerProfile profile = Bukkit
                            .getServer()
                            .createProfileExact(owner.uniqueId() == null ? UUID.randomUUID() : owner.uniqueId(), owner.name());
                    if (texture != null) {
                        profile.clearProperties();
                        profile.setProperty(new ProfileProperty("textures", texture.value(), texture.signature()));
                    }
                    smeta.setPlayerProfile(profile);
                } else if (builderMeta instanceof LeatherArmorBuilderMeta) {
                    ((LeatherArmorMeta) meta).setColor(((LeatherArmorBuilderMeta) builderMeta).color());
                } else if (builderMeta instanceof EnchantmentStorageBuilderMeta emeta) {
                    for (Entry<Enchantment, Integer> entry : emeta.enchantments().entrySet()) {
                        ((EnchantmentStorageMeta) meta).addStoredEnchant(entry.getKey(), entry.getValue(), true);
                    }
                } else {
                    throw new UnsupportedOperationException("Meta not supported for this mc version: " + builderMeta);
                }
            }
            if (!storage.storeToJsonDocument().isEmpty())
                meta.getPersistentDataContainer().set(persistentDataKey, PersistentDataType.STRING, storage.storeToJsonDocument().toJson());
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override public AbstractItemBuilder clone() {
        return new ItemBuilder1_20_1(build());
    }

    @Override public JsonElement serialize() {
        return gson.toJsonTree(build());
    }
}

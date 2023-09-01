/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.bukkit.inventoryapi.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import eu.darkcube.system.bukkit.inventoryapi.item.attribute.Attribute;
import eu.darkcube.system.bukkit.inventoryapi.item.meta.BuilderMeta;
import eu.darkcube.system.bukkit.inventoryapi.item.attribute.AttributeModifier;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class AbstractItemBuilder implements ItemBuilder {

    protected Material material = Material.AIR;
    protected int amount = 1;
    protected int damage = 0;
    protected Set<BuilderMeta> metas = new HashSet<>();
    protected HashMap<Enchantment, Integer> enchantments = new HashMap<>();
    protected Component displayname = null;
    protected ArrayList<Component> lore = new ArrayList<>();
    protected ArrayList<ItemFlag> flags = new ArrayList<>();
    protected boolean unbreakable = false;
    protected boolean glow = false;
    protected int repairCost = 0;
    protected LinkedHashMultimap<Attribute, AttributeModifier> attributeModifiers = LinkedHashMultimap.create();

    protected BasicItemPersistentDataStorage storage = new BasicItemPersistentDataStorage(this);

    @Override public Material material() {
        return material;
    }

    @Override public AbstractItemBuilder material(Material material) {
        this.material = material;
        return this;
    }

    @Override public AbstractItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    @Override public Multimap<Attribute, AttributeModifier> attributeModifiers() {
        return Multimaps.unmodifiableMultimap(attributeModifiers);
    }

    @Override public Multimap<Attribute, AttributeModifier> attributeModifiers(EquipmentSlot slot) {
        SetMultimap<Attribute, AttributeModifier> result = LinkedHashMultimap.create();
        for (Map.Entry<Attribute, AttributeModifier> entry : this.attributeModifiers.entries()) {
            if (entry.getValue().equipmentSlot() == null || entry.getValue().equipmentSlot() == slot) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    @Override public Collection<AttributeModifier> attributeModifiers(Attribute attribute) {
        return Collections.unmodifiableCollection(new ArrayList<>(attributeModifiers.get(attribute)));
    }

    @Override public AbstractItemBuilder attributeModifier(Attribute attribute, AttributeModifier modifier) {
        for (Map.Entry<Attribute, AttributeModifier> entry : this.attributeModifiers.entries()) {
            Preconditions.checkArgument(!entry
                    .getValue()
                    .uniqueId()
                    .equals(modifier.uniqueId()), "Cannot register AttributeModifier. Modifier is already applied! %s", modifier);
        }
        attributeModifiers.put(attribute, modifier);
        return this;
    }

    @Override public AbstractItemBuilder attributeModifiers(Multimap<Attribute, AttributeModifier> attributeModifiers) {
        this.attributeModifiers.clear();
        this.attributeModifiers.putAll(attributeModifiers);
        return this;
    }

    @Override public AbstractItemBuilder removeAttributeModifiers(EquipmentSlot slot) {
        int removed = 0;
        Iterator<Map.Entry<Attribute, AttributeModifier>> iter = this.attributeModifiers.entries().iterator();

        while (iter.hasNext()) {
            Map.Entry<Attribute, AttributeModifier> entry = iter.next();
            // Explicitly match against null because (as of MC 1.13) AttributeModifiers without a -
            // set slot are active in any slot.
            if (entry.getValue().equipmentSlot() == null || entry.getValue().equipmentSlot() == slot) {
                iter.remove();
                ++removed;
            }
        }
        return this;
    }

    @Override public AbstractItemBuilder removeAttributeModifiers(Attribute attribute) {
        attributeModifiers.removeAll(attribute);
        return this;
    }

    @Override public AbstractItemBuilder removeAttributeModifiers(Attribute attribute, AttributeModifier modifier) {
        int removed = 0;
        Iterator<Map.Entry<Attribute, AttributeModifier>> iter = this.attributeModifiers.entries().iterator();
        while (iter.hasNext()) {
            Map.Entry<Attribute, AttributeModifier> entry = iter.next();
            if (entry.getKey() == null || entry.getValue() == null) {
                iter.remove();
                ++removed;
                continue; // remove all null values while we are here
            }
            if (Objects.equals(entry.getKey(), attribute) && entry.getValue().uniqueId().equals(modifier.uniqueId())) {
                iter.remove();
                ++removed;
            }
        }
        return this;
    }

    @Override public int amount() {
        return amount;
    }

    @Override public AbstractItemBuilder damage(int damage) {
        this.damage = damage;
        return this;
    }

    @Override public int damage() {
        return damage;
    }

    @Override public AbstractItemBuilder enchant(Enchantment enchant, int level) {
        enchantments.put(enchant, level);
        return this;
    }

    @Override public AbstractItemBuilder enchant(Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
        return this;
    }

    @Override public AbstractItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments.clear();
        this.enchantments.putAll(enchantments);
        return this;
    }

    @Override public Map<Enchantment, Integer> enchantments() {
        return Collections.unmodifiableMap(enchantments);
    }

    @Override @Deprecated public AbstractItemBuilder displayname(String displayname) {
        if (displayname == null) {
            return displayname((Component) null);
        }
        return displayname(LegacyComponentSerializer.legacySection().deserialize(displayname));
    }

    @Override public AbstractItemBuilder displayname(Component displayname) {
        this.displayname = displayname == null ? null : Component.empty().decoration(TextDecoration.ITALIC, false).append(displayname);
        return this;
    }

    @Override public AbstractItemBuilder displaynameRaw(Component displayname) {
        this.displayname = displayname;
        return this;
    }

    @Override public Component displayname() {
        return displayname;
    }

    @Override public AbstractItemBuilder lore(Component line) {
        lore.add(Component.empty().decoration(TextDecoration.ITALIC, false).append(line));
        return this;
    }

    @Override public AbstractItemBuilder lore(Collection<Component> lore) {
        for (Component component : lore) {
            lore(component);
        }
        return this;
    }

    @Override public AbstractItemBuilder lore(Component... lines) {
        return lore(Arrays.asList(lines));
    }

    @Override public AbstractItemBuilder lore(Component line, int index) {
        lore.add(index, Component.empty().decoration(TextDecoration.ITALIC, false).append(line));
        return this;
    }

    @Override @Deprecated public AbstractItemBuilder lore(String... lines) {
        for (String line : lines) {
            lore(LegacyComponentSerializer.legacySection().deserialize(line));
        }
        return this;
    }

    @Override @Deprecated public AbstractItemBuilder lores(Collection<String> lines) {
        lore.clear();
        lore(lines.toArray(new String[0]));
        return this;
    }

    @Override public AbstractItemBuilder setLore(Collection<Component> lore) {
        this.lore.clear();
        lore(lore);
        return this;
    }

    @Override public List<Component> lore() {
        return Collections.unmodifiableList(lore);
    }

    @Override public AbstractItemBuilder flag(ItemFlag flag) {
        flags.add(flag);
        return this;
    }

    @Override public AbstractItemBuilder flag(Collection<ItemFlag> flags) {
        this.flags.addAll(flags);
        return this;
    }

    @Override public AbstractItemBuilder flag(ItemFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    @Override public AbstractItemBuilder setFlags(Collection<ItemFlag> flags) {
        this.flags.clear();
        flag(flags);
        return this;
    }

    @Override public List<ItemFlag> flags() {
        return Collections.unmodifiableList(flags);
    }

    @Override public AbstractItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    @Override public boolean unbreakable() {
        return unbreakable;
    }

    @Override public AbstractItemBuilder glow(boolean glow) {
        this.glow = glow;
        return this;
    }

    @Override public boolean glow() {
        return glow;
    }

    @Override public BasicItemPersistentDataStorage persistentDataStorage() {
        return storage;
    }

    @Override public <T extends BuilderMeta> T meta(Class<T> clazz) {
        if (clazz.getSuperclass() == Object.class) {
            for (BuilderMeta existing : metas) {
                if (clazz.equals(existing.getClass())) {
                    return clazz.cast(existing);
                }
            }
            try {
                Constructor<T> constructor = clazz.getConstructor();
                constructor.setAccessible(true);
                T t = constructor.newInstance();
                metas.add(t);
                return t;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Invalid BuilderMeta", e);
            }
        }
        throw new ClassCastException("Invalid BuilderMeta");
    }

    @Override public AbstractItemBuilder meta(BuilderMeta meta) {
        metas.removeIf(m -> m.getClass().equals(meta.getClass()));
        metas.add(meta);
        return this;
    }

    @Override public Set<BuilderMeta> metas() {
        return Collections.unmodifiableSet(metas);
    }

    @Override public AbstractItemBuilder metas(Set<BuilderMeta> metas) {
        this.metas.clear();
        metas.stream().map(BuilderMeta::clone).forEach(this::meta);
        return this;
    }

    @Override public int repairCost() {
        return repairCost;
    }

    @Override public AbstractItemBuilder repairCost(int repairCost) {
        this.repairCost = repairCost;
        return this;
    }

    @Override public abstract ItemBuilder clone();

    public void setLore(ArrayList<Component> lore) {
        this.lore = lore;
    }
}

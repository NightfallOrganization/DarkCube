/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.inventoryapi.item;

import eu.darkcube.system.inventoryapi.item.meta.BuilderMeta;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

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
	protected BasicItemPersistentDataStorage storage = new BasicItemPersistentDataStorage(this);

	@Override
	public Material material() {
		return material;
	}

	@Override
	public AbstractItemBuilder material(Material material) {
		this.material = material;
		return this;
	}

	@Override
	public AbstractItemBuilder amount(int amount) {
		this.amount = amount;
		return this;
	}

	@Override
	public int amount() {
		return amount;
	}

	@Override
	public AbstractItemBuilder damage(int damage) {
		this.damage = damage;
		return this;
	}

	@Override
	public int damage() {
		return damage;
	}

	@Override
	public AbstractItemBuilder enchant(Enchantment enchant, int level) {
		enchantments.put(enchant, level);
		return this;
	}

	@Override
	public AbstractItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
		this.enchantments.putAll(enchantments);
		return this;
	}

	@Override
	public Map<Enchantment, Integer> enchantments() {
		return Collections.unmodifiableMap(enchantments);
	}

	@Override
	@Deprecated
	public AbstractItemBuilder displayname(String displayname) {
		if (displayname == null) {
			return displayname((Component) null);
		}
		return displayname(LegacyComponentSerializer.legacySection().deserialize(displayname));
	}

	@Override
	public AbstractItemBuilder displayname(Component displayname) {
		this.displayname = displayname;
		return this;
	}

	@Override
	public Component displayname() {
		return displayname;
	}

	@Override
	public AbstractItemBuilder lore(Component line) {
		lore.add(line);
		return this;
	}

	@Override
	public AbstractItemBuilder lore(Collection<Component> lore) {
		this.lore.addAll(lore);
		return this;
	}

	@Override
	public AbstractItemBuilder lore(Component... lines) {
		lore.addAll(Arrays.asList(lines));
		return this;
	}

	@Override
	public AbstractItemBuilder lore(Component line, int index) {
		lore.add(index, line);
		return this;
	}

	@Override
	@Deprecated
	public AbstractItemBuilder lore(String... lines) {
		for (String line : lines) {
			lore(LegacyComponentSerializer.legacySection().deserialize(line));
		}
		return this;
	}

	@Override
	@Deprecated
	public AbstractItemBuilder lores(Collection<String> lines) {
		lore.clear();
		lore(lines.toArray(new String[0]));
		return this;
	}

	@Override
	public AbstractItemBuilder setLore(Collection<Component> lore) {
		this.lore.clear();
		lore(lore);
		return this;
	}

	@Override
	public List<Component> lore() {
		return Collections.unmodifiableList(lore);
	}

	@Override
	public AbstractItemBuilder flag(ItemFlag flag) {
		flags.add(flag);
		return this;
	}

	@Override
	public AbstractItemBuilder flag(Collection<ItemFlag> flags) {
		this.flags.addAll(flags);
		return this;
	}

	@Override
	public AbstractItemBuilder flag(ItemFlag... flags) {
		this.flags.addAll(Arrays.asList(flags));
		return this;
	}

	@Override
	public AbstractItemBuilder setFlags(Collection<ItemFlag> flags) {
		this.flags.clear();
		flag(flags);
		return this;
	}

	@Override
	public List<ItemFlag> flags() {
		return Collections.unmodifiableList(flags);
	}

	@Override
	public AbstractItemBuilder unbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	@Override
	public boolean unbreakable() {
		return unbreakable;
	}

	@Override
	public AbstractItemBuilder glow(boolean glow) {
		this.glow = glow;
		return this;
	}

	@Override
	public boolean glow() {
		return glow;
	}

	@Override
	public BasicItemPersistentDataStorage persistentDataStorage() {
		return storage;
	}

	@Override
	public <T extends BuilderMeta> T meta(Class<T> clazz) {
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
			} catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
					InvocationTargetException e) {
				throw new RuntimeException("Invalid BuilderMeta", e);
			}
		}
		throw new ClassCastException("Invalid BuilderMeta");
	}

	@Override
	public AbstractItemBuilder meta(BuilderMeta meta) {
		metas.removeIf(m -> m.getClass().equals(meta.getClass()));
		metas.add(meta);
		return this;
	}

	@Override
	public Set<BuilderMeta> metas() {
		return Collections.unmodifiableSet(metas);
	}

	@Override
	public AbstractItemBuilder metas(Set<BuilderMeta> metas) {
		this.metas.clear();
		metas.addAll(metas.stream().map(BuilderMeta::clone).collect(Collectors.toSet()));
		return this;
	}

	@Override
	public abstract ItemBuilder clone();

	public void setLore(ArrayList<Component> lore) {
		this.lore = lore;
	}
}

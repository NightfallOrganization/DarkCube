/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.inventory.api.util;

import com.google.gson.Gson;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ItemBuilder - API Class to create a {@link org.bukkit.inventory.ItemStack} with just one line of
 * Code
 *
 * @author Acquized
 * @version 1.8
 * @contributor Kev575
 * @contributor DasBabyPixel
 */

public class ItemBuilder {

	private String skullOwner;
	private ItemStack item;

	private ItemMeta meta;

	private Material material;

	private int amount = 1;

	private MaterialData data;

	private short damage = 0;

	private Map<Enchantment, Integer> enchantments = new HashMap<>();

	private String displayname;

	private final List<String> lore = new ArrayList<>();

	private final List<ItemFlag> flags = new ArrayList<>();

	private FireworkEffect fireworkEffect = null;

	private boolean andSymbol = true;

	private boolean unsafeStackSize = false;

	private boolean unbreakable = false;

	/** Initalizes the ItemBuilder with {@link org.bukkit.Material} */
	public ItemBuilder(Material material) {
		if (material == null)
			material = Material.AIR;
		this.item = new ItemStack(material);
		this.material = material;
	}

	/** Initalizes the ItemBuilder with {@link org.bukkit.Material} and Amount */
	public ItemBuilder(Material material, int amount) {
		if (material == null)
			material = Material.AIR;
		if (amount > material.getMaxStackSize() || amount <= 0)
			amount = 1;
		this.amount = amount;
		this.item = new ItemStack(material, amount);
		this.material = material;
	}

	/**
	 * Initalizes the ItemBuilder with {@link org.bukkit.Material}, Amount and Displayname
	 */
	public ItemBuilder(Material material, int amount, String displayname) {
		if (material == null)
			material = Material.AIR;
		Validate.notNull(displayname, "The Displayname is null.");
		this.item = new ItemStack(material, amount);
		this.material = material;
		if (amount > material.getMaxStackSize() || amount <= 0)
			amount = 1;
		this.amount = amount;
		this.displayname = displayname;
	}

	/**
	 * Initalizes the ItemBuilder with {@link org.bukkit.Material} and Displayname
	 */
	public ItemBuilder(Material material, String displayname) {
		if (material == null)
			material = Material.AIR;
		Validate.notNull(displayname, "The Displayname is null.");
		this.item = new ItemStack(material);
		this.material = material;
		this.displayname = displayname;
	}

	/** Initalizes the ItemBuilder with a {@link org.bukkit.inventory.ItemStack} */
	public ItemBuilder(ItemStack item) {
		Validate.notNull(item, "The Item is null.");
		this.item = item;
		this.material = item.getType();
		this.amount = item.getAmount();
		this.data = item.getData();
		this.damage = item.getDurability();
		this.enchantments = item.getEnchantments();
		if (item.hasItemMeta()) {
			this.meta = item.getItemMeta();
			this.unbreakable = this.meta.spigot().isUnbreakable();
			this.displayname = this.meta.getDisplayName();
			List<String> mlore = this.meta.getLore();
			if (mlore != null)
				this.lore.addAll(mlore);
			this.flags.addAll(this.meta.getItemFlags());
			if (this.meta instanceof FireworkEffectMeta) {
				if (((FireworkEffectMeta) this.meta).hasEffect()) {
					this.fireworkEffect = ((FireworkEffectMeta) this.meta).getEffect();
				}
			}
		}
	}

	/**
	 * Same as {@link #ItemBuilder(Material)}
	 *
	 * @param material The Material
	 * @return {@link #ItemBuilder(Material)}
	 */
	public static ItemBuilder item(Material material) {
		return new ItemBuilder(material);
	}

	/**
	 * Initalizes the ItemBuilder with a {@link org.bukkit.configuration.file.FileConfiguration}
	 * ItemStack in Path
	 */
	public ItemBuilder(FileConfiguration cfg, String path) {
		this(cfg.getItemStack(path));
	}

	/**
	 * Initalizes the ItemBuilder with an already existing {@link ItemBuilder}
	 *
	 * @deprecated Use the already initalized {@code ItemBuilder} Instance to improve performance
	 */
	@Deprecated
	public ItemBuilder(ItemBuilder builder) {
		Validate.notNull(builder, "The ItemBuilder is null.");
		this.item = builder.item;
		this.meta = builder.meta;
		this.unbreakable = builder.unbreakable;
		this.material = builder.material;
		this.amount = builder.amount;
		this.damage = builder.damage;
		this.data = builder.data;
		this.andSymbol = builder.andSymbol;
		this.unsafeStackSize = builder.unsafeStackSize;
		this.enchantments = builder.enchantments;
		this.displayname = builder.displayname;
		this.fireworkEffect = builder.fireworkEffect;
		this.skullOwner = builder.skullOwner;
		this.lore.addAll(builder.lore);
		this.flags.addAll(builder.flags);
	}

	/**
	 * Sets the Amount of the ItemStack
	 *
	 * @param amount Amount for the ItemStack
	 */
	public ItemBuilder amount(int amount) {
		if (((amount > this.material.getMaxStackSize()) || (amount <= 0))
				&& (!this.unsafeStackSize))
			amount = 1;
		this.amount = amount;
		return this;
	}

	/**
	 * Sets the {@link org.bukkit.material.MaterialData} of the ItemStack
	 *
	 * @param data MaterialData for the ItemStack
	 */
	public ItemBuilder data(MaterialData data) {
		Validate.notNull(data, "The Data is null.");
		this.data = data;
		return this;
	}

	/**
	 * Sets the Damage of the ItemStack
	 *
	 * @param damage Damage for the ItemStack
	 * @deprecated Use {@code ItemBuilder#durability}
	 */
	@Deprecated
	public ItemBuilder damage(short damage) {
		this.damage = damage;
		return this;
	}

	/**
	 * Sets the Durability (Damage) of the ItemStack
	 *
	 * @param damage Damage for the ItemStack
	 */
	public ItemBuilder durability(int damage) {
		this.damage = (short) damage;
		return this;
	}

	/**
	 * Sets the {@link org.bukkit.Material} of the ItemStack
	 *
	 * @param material Material for the ItemStack
	 */
	public ItemBuilder material(Material material) {
		Validate.notNull(material, "The Material is null.");
		this.material = material;
		return this;
	}

	/**
	 * Sets the {@link org.bukkit.inventory.meta.ItemMeta} of the ItemStack
	 *
	 * @param meta Meta for the ItemStack
	 */
	public ItemBuilder meta(ItemMeta meta) {
		Validate.notNull(meta, "The Meta is null.");
		this.meta = meta;
		this.unbreakable = meta.spigot().isUnbreakable();
		this.item.setItemMeta(meta);
		return this;
	}

	/**
	 * Adds a {@link org.bukkit.enchantments.Enchantment} to the ItemStack
	 *
	 * @param enchant Enchantment for the ItemStack
	 * @param level Level of the Enchantment
	 */
	public ItemBuilder enchant(Enchantment enchant, int level) {
		Validate.notNull(enchant, "The Enchantment is null.");
		this.enchantments.put(enchant, level);
		return this;
	}

	/**
	 * Adds a list of {@link org.bukkit.enchantments.Enchantment} to the ItemStack
	 *
	 * @param enchantments Map containing Enchantment and Level for the ItemStack
	 */
	public ItemBuilder enchant(Map<Enchantment, Integer> enchantments) {
		Validate.notNull(enchantments, "The Enchantments are null.");
		this.enchantments = enchantments;
		return this;
	}

	/**
	 * Sets the Displayname of the ItemStack
	 *
	 * @param displayname Displayname for the ItemStack
	 */
	public ItemBuilder displayname(String displayname) {
		Validate.notNull(displayname, "The Displayname is null.");
		this.displayname = this.andSymbol
				? ChatColor.translateAlternateColorCodes('&', displayname)
				: displayname;
		return this;
	}

	/**
	 * Adds a Line to the Lore of the ItemStack
	 *
	 * @param line Line of the Lore for the ItemStack
	 */
	public ItemBuilder lore(String line) {
		Validate.notNull(line, "The Line is null.");
		this.lore.add(this.andSymbol ? ChatColor.translateAlternateColorCodes('&', line) : line);
		return this;
	}

	/**
	 * Sets the Lore of the ItemStack
	 *
	 * @param lore List containing String as Lines for the ItemStack Lore
	 */
	public ItemBuilder lore(List<String> lore) {
		Validate.notNull(lore, "The Lores are null.");
		this.lore.clear();
		this.lore.addAll(lore);
		return this;
	}

	/**
	 * Adds one or more Lines to the Lore of the ItemStack
	 *
	 * @param lines One or more Strings for the ItemStack Lore
	 * @deprecated Use {@code ItemBuilder#lore}
	 */
	@Deprecated
	public ItemBuilder lores(String... lines) {
		Validate.notNull(lines, "The Lines are null.");
		for (String line : lines) {
			this.lore(this.andSymbol ? ChatColor.translateAlternateColorCodes('&', line) : line);
		}
		return this;
	}

	/**
	 * Adds one or more Lines to the Lore of the ItemStack
	 *
	 * @param lines One or more Strings for the ItemStack Lore
	 */
	public ItemBuilder lore(String... lines) {
		Validate.notNull(lines, "The Lines are null.");
		for (String line : lines) {
			this.lore(this.andSymbol ? ChatColor.translateAlternateColorCodes('&', line) : line);
		}
		return this;
	}

	/**
	 * Adds a String at a specified position in the Lore of the ItemStack
	 *
	 * @param line Line of the Lore for the ItemStack
	 * @param index Position in the Lore for the ItemStack
	 */
	public ItemBuilder lore(String line, int index) {
		Validate.notNull(line, "The Line is null.");
		this.lore.set(index,
				this.andSymbol ? ChatColor.translateAlternateColorCodes('&', line) : line);
		return this;
	}

	/**
	 * Adds a {@link org.bukkit.inventory.ItemFlag} to the ItemStack
	 *
	 * @param flag ItemFlag for the ItemStack
	 */
	public ItemBuilder flag(ItemFlag flag) {
		Validate.notNull(flag, "The Flag is null.");
		this.flags.add(flag);
		return this;
	}

	/**
	 * Adds more than one {@link org.bukkit.inventory.ItemFlag} to the ItemStack
	 *
	 * @param flags List containing all ItemFlags
	 */
	public ItemBuilder flag(List<ItemFlag> flags) {
		Validate.notNull(flags, "The Flags are null.");
		this.flags.clear();
		this.flags.addAll(flags);
		return this;
	}

	/**
	 * Makes or removes the Unbreakable Flag from the ItemStack
	 *
	 * @param unbreakable If it should be unbreakable
	 */
	public ItemBuilder unbreakable(boolean unbreakable) {
		//		this.meta.spigot().setUnbreakable(unbreakable);
		this.unbreakable = unbreakable;
		return this;
	}

	/**
	 * @return if this item is unbreakable
	 */
	public boolean isUnbreakable() {
		return this.unbreakable;
	}

	/** Makes the ItemStack Glow like it had a Enchantment */
	public ItemBuilder glow() {
		this.enchant(this.material != Material.BOW ? Enchantment.ARROW_INFINITE : Enchantment.LUCK,
				10);
		this.flag(ItemFlag.HIDE_ENCHANTS);
		return this;
	}

	/**
	 * Sets the {@link FireworkEffect} for the ItemStack
	 *
	 * @param effect FireworkEffect for the ItemStack
	 */
	public ItemBuilder fireworkEffect(FireworkEffect effect) {
		this.fireworkEffect = effect;
		return this;
	}

	/**
	 * Sets the Skin for the Skull
	 *
	 * @param owner Username of the Skull
	 */
	public ItemBuilder owner(String owner) {
		Validate.notNull(owner, "The Username is null.");
		this.skullOwner = owner;
		return this;
	}

	/**
	 * @return the owner of this skull
	 */
	public String owner() {
		return skullOwner;
	}

	/** Returns the Unsafe Class containing NBT Methods */
	public Unsafe unsafe() {
		return new Unsafe(this);
	}

	public Unsafe getUnsafe() {
		return new Unsafe(this);
	}

	/**
	 * Toggles replacement of the '&' Characters in Strings
	 *
	 * @deprecated Use {@code ItemBuilder#toggleReplaceAndSymbol}
	 */
	@Deprecated
	public ItemBuilder replaceAndSymbol() {
		this.replaceAndSymbol(!this.andSymbol);
		return this;
	}

	/**
	 * Enables / Disables replacement of the '&' Character in Strings
	 *
	 * @param replace Determinates if it should be replaced or not
	 */
	public ItemBuilder replaceAndSymbol(boolean replace) {
		this.andSymbol = replace;
		return this;
	}

	/** Toggles replacement of the '&' Character in Strings */
	public ItemBuilder toggleReplaceAndSymbol() {
		this.replaceAndSymbol(!this.andSymbol);
		return this;
	}

	/**
	 * Allows / Disallows Stack Sizes under 1 and above 64
	 *
	 * @param allow Determinates if it should be allowed or not
	 */
	public ItemBuilder unsafeStackSize(boolean allow) {
		this.unsafeStackSize = allow;
		return this;
	}

	/** Toggles allowment of stack sizes under 1 and above 64 */
	public ItemBuilder toggleUnsafeStackSize() {
		this.unsafeStackSize(!this.unsafeStackSize);
		return this;
	}

	/** Returns the Displayname */
	public String getDisplayname() {
		return this.displayname;
	}

	/** Returns the Amount */
	public int getAmount() {
		return this.amount;
	}

	/** Returns all Enchantments */
	public Map<Enchantment, Integer> getEnchantments() {
		return this.enchantments;
	}

	/**
	 * Returns the Damage
	 *
	 * @deprecated Use {@code ItemBuilder#getDurability}
	 */
	@Deprecated
	public short getDamage() {
		return this.damage;
	}

	/** Returns the Durability */
	public short getDurability() {
		return this.damage;
	}

	/** Returns the Lores */
	public List<String> getLores() {
		return this.lore;
	}

	/** Returns if the '&' Character will be replaced */
	public boolean getAndSymbol() {
		return this.andSymbol;
	}

	/** Returns all ItemFlags */
	public List<ItemFlag> getFlags() {
		return this.flags;
	}

	/** Returns the Material */
	public Material getMaterial() {
		return this.material;
	}

	/** Returns the ItemMeta */
	public ItemMeta getMeta() {
		return this.meta;
	}

	/** Returns the FireworkEffects */
	public FireworkEffect getFireworkEffects() {
		return this.fireworkEffect;
	}

	/** Returns the MaterialData */
	public MaterialData getData() {
		return this.data;
	}

	/**
	 * Returns all Lores
	 *
	 * @deprecated Use {@code ItemBuilder#getLores}
	 */
	@Deprecated
	public List<String> getLore() {
		return this.lore;
	}

	/**
	 * Converts the Item to a ConfigStack and writes it to path
	 *
	 * @param cfg Configuration File to which it should be writed
	 * @param path Path to which the ConfigStack should be writed
	 */
	public ItemBuilder toConfig(FileConfiguration cfg, String path) {
		cfg.set(path, this.build());
		return this;
	}

	/**
	 * Converts back the ConfigStack to a ItemBuilder
	 *
	 * @param cfg Configuration File from which it should be read
	 * @param path Path from which the ConfigStack should be read
	 */
	public ItemBuilder fromConfig(FileConfiguration cfg, String path) {
		return new ItemBuilder(cfg, path);
	}

	/**
	 * Converts the Item to a ConfigStack and writes it to path
	 *
	 * @param cfg Configuration File to which it should be writed
	 * @param path Path to which the ConfigStack should be writed
	 * @param builder Which ItemBuilder should be writed
	 */
	public static void toConfig(FileConfiguration cfg, String path, ItemBuilder builder) {
		cfg.set(path, builder.build());
	}

	/**
	 * Converts the ItemBuilder to a JsonItemBuilder
	 *
	 * @return The ItemBuilder as JSON String
	 */
	public String toJson() {
		return new Gson().toJson(this);
	}

	/**
	 * Converts the ItemBuilder to a JsonItemBuilder
	 *
	 * @param builder Which ItemBuilder should be converted
	 * @return The ItemBuilder as JSON String
	 */
	public static String toJson(ItemBuilder builder) {
		return new Gson().toJson(builder);
	}

	/**
	 * Converts the JsonItemBuilder back to a ItemBuilder
	 *
	 * @param json Which JsonItemBuilder should be converted
	 */
	public static ItemBuilder fromJson(String json) {
		return new Gson().fromJson(json, ItemBuilder.class);
	}

	/**
	 * Applies the currently ItemBuilder to the JSONItemBuilder
	 *
	 * @param json Already existing JsonItemBuilder
	 * @param overwrite Should the JsonItemBuilder used now
	 */
	public ItemBuilder applyJson(String json, boolean overwrite) {
		ItemBuilder b = new Gson().fromJson(json, ItemBuilder.class);
		if (overwrite)
			return b;
		if (b.displayname != null)
			this.displayname = b.displayname;
		if (b.data != null)
			this.data = b.data;
		if (b.material != null)
			this.material = b.material;
		this.lore.clear();
		this.lore.addAll(b.lore);
		if (b.enchantments != null)
			this.enchantments = b.enchantments;
		if (b.item != null)
			this.item = b.item;
		this.skullOwner = b.skullOwner;
		this.fireworkEffect = b.fireworkEffect;
		this.flags.clear();
		this.flags.addAll(b.flags);
		this.damage = b.damage;
		this.amount = b.amount;
		return this;
	}

	/** Converts the ItemBuilder to a {@link org.bukkit.inventory.ItemStack} */
	public ItemStack build() {
		this.item.setType(this.material);
		this.item.setAmount(this.amount);
		this.item.setDurability(this.damage);
		if (this.data != null) {
			this.item.setData(this.data);
		}
		if (this.enchantments.size() > 0) {
			this.item.addUnsafeEnchantments(this.enchantments);
		}
		if (meta == null) {
			this.meta = this.item.getItemMeta();
		}
		if (this.displayname != null) {
			this.meta.setDisplayName(this.displayname);
		}
		if (this.lore.size() > 0) {
			this.meta.setLore(this.lore);
		}
		this.meta.spigot().setUnbreakable(this.unbreakable);
		if (this.flags.size() > 0) {
			for (ItemFlag f : this.flags) {
				this.meta.addItemFlags(f);
			}
		}
		if (skullOwner != null) {
			((SkullMeta) meta).setOwner(skullOwner);
		}
		if (fireworkEffect != null) {
			((FireworkEffectMeta) meta).setEffect(this.fireworkEffect);
		}
		this.item.setItemMeta(this.meta);
		return this.item;
	}

	/** Contains NBT Tags Methods */
	public static class Unsafe {

		/** Do not access using this Field */
		protected final ReflectionUtils utils = new ReflectionUtils();

		/** Do not access using this Field */
		protected final ItemBuilder builder;

		/** Initalizes the Unsafe Class with a ItemBuilder */
		public Unsafe(ItemBuilder builder) {
			this.builder = builder;
		}

		/**
		 * Sets a NBT Tag {@code String} into the NBT Tag Compound of the Item
		 *
		 * @param key The Name on which the NBT Tag should be saved
		 * @param value The Value that should be saved
		 */
		public Unsafe setString(String key, String value) {
			this.builder.item = this.utils.setString(this.builder.item, key, value);
			this.builder.meta = this.builder.item.getItemMeta();
			return this;
		}

		/** Returns the String that is saved under the key */
		public String getString(String key) {
			return this.utils.getString(this.builder.item, key);
		}

		/**
		 * Sets a NBT Tag {@code Integer} into the NBT Tag Compound of the Item
		 *
		 * @param key The Name on which the NBT Tag should be savbed
		 * @param value The Value that should be saved
		 */
		public Unsafe setInt(String key, int value) {
			this.builder.item = this.utils.setInt(this.builder.item, key, value);
			this.builder.meta = this.builder.item.getItemMeta();
			return this;
		}

		/** Returns the Integer that is saved under the key */
		public int getInt(String key) {
			return this.utils.getInt(this.builder.item, key);
		}

		/**
		 * Sets a NBT Tag {@code Double} into the NBT Tag Compound of the Item
		 *
		 * @param key The Name on which the NBT Tag should be savbed
		 * @param value The Value that should be saved
		 */
		public Unsafe setDouble(String key, double value) {
			this.builder.item = this.utils.setDouble(this.builder.item, key, value);
			this.builder.meta = this.builder.item.getItemMeta();
			return this;
		}

		/** Returns the Double that is saved under the key */
		public double getDouble(String key) {
			return this.utils.getDouble(this.builder.item, key);
		}

		/**
		 * Sets a NBT Tag {@code Boolean} into the NBT Tag Compound of the Item
		 *
		 * @param key The Name on which the NBT Tag should be savbed
		 * @param value The Value that should be saved
		 */
		public Unsafe setBoolean(String key, boolean value) {
			this.builder.item = this.utils.setBoolean(this.builder.item, key, value);
			this.builder.meta = this.builder.item.getItemMeta();
			return this;
		}

		/** Returns the Boolean that is saved under the key */
		public boolean getBoolean(String key) {
			return this.utils.getBoolean(this.builder.item, key);
		}

		/** Returns a Boolean if the Item contains the NBT Tag named key */
		public boolean containsKey(String key) {
			return this.utils.hasKey(this.builder.item, key);
		}

		/** Accesses back the ItemBuilder and exists the Unsafe Class */
		public ItemBuilder builder() {
			return this.builder;
		}

		/**
		 * This Class contains highly sensitive NMS Code that should not be touched unless you want
		 * to break the ItemBuilder
		 */
		private static class ReflectionUtils {

			public String getString(ItemStack item, String key) {
				Object compound = this.getNBTTagCompound(this.getItemAsNMSStack(item));
				if (compound == null) {
					return null;
				}
				try {
					return (String) compound.getClass().getMethod("getString", String.class)
							.invoke(compound, key);
				} catch (IllegalAccessException | InvocationTargetException |
						NoSuchMethodException ex) {
					ex.printStackTrace();
				}
				return null;
			}

			public ItemStack setString(ItemStack item, String key, String value) {
				Object nmsItem = this.getItemAsNMSStack(item);
				Object compound = this.getNBTTagCompound(nmsItem);
				if (compound == null) {
					compound = this.getNewNBTTagCompound();
				}
				try {
					compound.getClass().getMethod("setString", String.class, String.class)
							.invoke(compound, key, value);
					this.setNBTTag(compound, nmsItem);
				} catch (IllegalAccessException | InvocationTargetException |
						NoSuchMethodException ex) {
					ex.printStackTrace();
				}
				return this.getItemAsBukkitStack(nmsItem);
			}

			public int getInt(ItemStack item, String key) {
				Object compound = this.getNBTTagCompound(this.getItemAsNMSStack(item));
				if (compound == null) {
					return 0;
				}
				try {
					return (Integer) compound.getClass().getMethod("getInt", String.class)
							.invoke(compound, key);
				} catch (IllegalAccessException | InvocationTargetException |
						NoSuchMethodException ex) {
					ex.printStackTrace();
				}
				return -1;
			}

			public ItemStack setInt(ItemStack item, String key, int value) {
				Object nmsItem = this.getItemAsNMSStack(item);
				Object compound = this.getNBTTagCompound(nmsItem);
				if (compound == null) {
					compound = this.getNewNBTTagCompound();
				}
				try {
					compound.getClass().getMethod("setInt", String.class, int.class)
							.invoke(compound, key, value);
					this.setNBTTag(compound, nmsItem);
				} catch (IllegalAccessException | InvocationTargetException |
						NoSuchMethodException ex) {
					ex.printStackTrace();
				}
				return this.getItemAsBukkitStack(nmsItem);
			}

			public double getDouble(ItemStack item, String key) {
				Object compound = this.getNBTTagCompound(this.getItemAsNMSStack(item));
				if (compound == null) {
					return 0;
				}
				try {
					return (Double) compound.getClass().getMethod("getDouble", String.class)
							.invoke(compound, key);
				} catch (IllegalAccessException | InvocationTargetException |
						NoSuchMethodException ex) {
					ex.printStackTrace();
				}
				return Double.NaN;
			}

			public ItemStack setDouble(ItemStack item, String key, double value) {
				Object nmsItem = this.getItemAsNMSStack(item);
				Object compound = this.getNBTTagCompound(nmsItem);
				if (compound == null) {
					compound = this.getNewNBTTagCompound();
				}
				try {
					compound.getClass().getMethod("setDouble", String.class, double.class)
							.invoke(compound, key, value);
					this.setNBTTag(compound, nmsItem);
				} catch (IllegalAccessException | InvocationTargetException |
						NoSuchMethodException ex) {
					ex.printStackTrace();
				}
				return this.getItemAsBukkitStack(nmsItem);
			}

			public boolean getBoolean(ItemStack item, String key) {
				Object compound = this.getNBTTagCompound(this.getItemAsNMSStack(item));
				if (compound == null) {
					return false;
				}
				try {
					return (Boolean) compound.getClass().getMethod("getBoolean", String.class)
							.invoke(compound, key);
				} catch (IllegalAccessException | InvocationTargetException |
						NoSuchMethodException ex) {
					ex.printStackTrace();
				}
				return false;
			}

			public ItemStack setBoolean(ItemStack item, String key, boolean value) {
				Object nmsItem = this.getItemAsNMSStack(item);
				Object compound = this.getNBTTagCompound(nmsItem);
				if (compound == null) {
					compound = this.getNewNBTTagCompound();
				}
				try {
					compound.getClass().getMethod("setBoolean", String.class, boolean.class)
							.invoke(compound, key, value);
					this.setNBTTag(compound, nmsItem);
				} catch (IllegalAccessException | InvocationTargetException |
						NoSuchMethodException ex) {
					ex.printStackTrace();
				}
				return this.getItemAsBukkitStack(nmsItem);
			}

			public boolean hasKey(ItemStack item, String key) {
				Object compound = this.getNBTTagCompound(this.getItemAsNMSStack(item));
				if (compound == null) {
					return false;
				}
				try {
					return (Boolean) compound.getClass().getMethod("hasKey", String.class)
							.invoke(compound, key);
				} catch (IllegalAccessException | InvocationTargetException |
						NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			}

			public Object getNewNBTTagCompound() {
				String ver = Bukkit.getServer().getClass().getName().split("\\.")[3];
				try {
					return Class.forName("net.minecraft.server." + ver + ".NBTTagCompound")
							.getConstructor().newInstance();
				} catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
						InvocationTargetException | NoSuchMethodException ex) {
					throw new RuntimeException(ex);
				}
			}

			public void setNBTTag(Object tag, Object item) {
				try {
					item.getClass().getMethod("setTag", tag.getClass()).invoke(item, tag);
				} catch (IllegalAccessException | InvocationTargetException |
						NoSuchMethodException ex) {
					throw new RuntimeException(ex);
				}
			}

			public Object getNBTTagCompound(Object nmsStack) {
				try {
					if (nmsStack == null) {
						return null;
					}
					return nmsStack.getClass().getMethod("getTag").invoke(nmsStack);
				} catch (IllegalAccessException | InvocationTargetException |
						NoSuchMethodException ex) {
					ex.printStackTrace();
				}
				return null;
			}

			public Object getItemAsNMSStack(ItemStack item) {
				try {
					Method m =
							this.getCraftItemStackClass().getMethod("asNMSCopy", ItemStack.class);
					return m.invoke(null, item);
				} catch (NoSuchMethodException | IllegalAccessException |
						InvocationTargetException ex) {
					throw new RuntimeException(ex);
				}
			}

			public ItemStack getItemAsBukkitStack(Object nmsStack) {
				try {
					Method m = this.getCraftItemStackClass()
							.getMethod("asBukkitCopy", nmsStack.getClass());
					return (ItemStack) m.invoke(null, nmsStack);
				} catch (NoSuchMethodException | InvocationTargetException |
						IllegalAccessException ex) {
					ex.printStackTrace();
				}
				return null;
			}

			public Class<?> getCraftItemStackClass() {
				String ver = Bukkit.getServer().getClass().getName().split("\\.")[3];
				try {
					return Class.forName(
							"org.bukkit.craftbukkit." + ver + ".inventory.CraftItemStack");
				} catch (ClassNotFoundException ex) {
					throw new RuntimeException(ex);
				}
			}

		}

	}

}

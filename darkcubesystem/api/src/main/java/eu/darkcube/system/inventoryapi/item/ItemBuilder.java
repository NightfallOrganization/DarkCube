/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.inventoryapi.item;

import com.google.common.collect.Multimap;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.inventoryapi.item.attribute.Attribute;
import eu.darkcube.system.inventoryapi.item.attribute.AttributeModifier;
import eu.darkcube.system.inventoryapi.item.meta.BuilderMeta;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.version.VersionSupport;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ItemBuilder - API to create an {@link ItemStack} with just one line of Code
 *
 * @author DasBabyPixel
 */
@Api public interface ItemBuilder {
    @Api static ItemBuilder item() {
        return item(Material.AIR);
    }

    @Api static ItemBuilder item(Material material) {
        return VersionSupport.version().itemProvider().item(material);
    }

    @Api static ItemBuilder item(ItemStack item) {
        return VersionSupport.version().itemProvider().item(item);
    }

    @Api static ItemBuilder spawner() {
        return VersionSupport.version().itemProvider().spawner();
    }

    @Api Material material();

    @Api ItemBuilder material(Material material);

    @Api ItemBuilder amount(int amount);

    @Api boolean canBeRepairedBy(ItemBuilder item);

    @Api Multimap<Attribute, AttributeModifier> attributeModifiers();

    @Api Multimap<Attribute, AttributeModifier> attributeModifiers(EquipmentSlot slot);

    @Api Collection<AttributeModifier> attributeModifiers(Attribute attribute);

    @Api ItemBuilder attributeModifier(Attribute attribute, AttributeModifier modifier);

    @Api ItemBuilder attributeModifiers(Multimap<Attribute, AttributeModifier> attributeModifiers);

    @Api ItemBuilder removeAttributeModifiers(EquipmentSlot slot);

    @Api ItemBuilder removeAttributeModifiers(Attribute attribute);

    @Api ItemBuilder removeAttributeModifiers(Attribute attribute, AttributeModifier modifier);

    @Api int amount();

    @Api ItemBuilder damage(int damage);

    @Api int damage();

    @Api ItemBuilder enchant(Enchantment enchant, int level);

    @Api ItemBuilder enchant(Map<Enchantment, Integer> enchantments);

    @Api ItemBuilder enchantments(Map<Enchantment, Integer> enchantments);

    @Api Map<Enchantment, Integer> enchantments();

    /**
     * @param displayname the display name
     * @return this builder
     * @deprecated Use {@link ItemBuilder#displayname(Component)}
     */
    @Api @Deprecated ItemBuilder displayname(String displayname);

    /**
     * Displayname via this method will not start italic
     *
     * @param displayname the displayname
     * @return this builder
     */
    @Api ItemBuilder displayname(Component displayname);

    /**
     * Displayname via this method will start italic
     *
     * @param displayname the displayname
     * @return this builder
     */
    @Api ItemBuilder displaynameRaw(Component displayname);

    @Api Component displayname();

    @Api ItemBuilder lore(Component line);

    @Api ItemBuilder lore(Collection<Component> lore);

    @Api ItemBuilder lore(Component... lines);

    @Api ItemBuilder lore(Component line, int index);

    /**
     * @param lines the lore
     * @return this builder
     * @deprecated Use {@link ItemBuilder#lore(Component...)}
     */
    @Api @Deprecated ItemBuilder lore(String... lines);

    /**
     * @param lines the lore
     * @return this builder
     * @deprecated Use {@link ItemBuilder#lore(Collection)}
     */

    @Api @Deprecated ItemBuilder lores(Collection<String> lines);

    @Api ItemBuilder setLore(Collection<Component> lore);

    @Api List<Component> lore();

    @Api ItemBuilder flag(ItemFlag flag);

    @Api ItemBuilder flag(Collection<ItemFlag> flags);

    @Api ItemBuilder flag(ItemFlag... flags);

    @Api ItemBuilder setFlags(Collection<ItemFlag> flags);

    @Api List<ItemFlag> flags();

    @Api ItemBuilder unbreakable(boolean unbreakable);

    @Api boolean unbreakable();

    @Api ItemBuilder glow(boolean glow);

    @Api boolean glow();

    @Api ItemPersistentDataStorage persistentDataStorage();

    @Api <T extends BuilderMeta> T meta(Class<T> clazz);

    @Api ItemBuilder meta(BuilderMeta meta);

    @Api Set<BuilderMeta> metas();

    @Api ItemBuilder metas(Set<BuilderMeta> metas);

    @Api ItemStack build();

    @Api ItemBuilder clone();

    @Api int repairCost();

    @Api ItemBuilder repairCost(int repairCost);

    //	/** Contains NBT Tags Methods */
    //	public static class Unsafe {
    //
    //		/** Do not access using this Field */
    //		protected final ReflectionUtils utils = new ReflectionUtils();
    //
    //		/** Do not access using this Field */
    //		protected final ItemBuilder builder;
    //
    //		/** Initalizes the Unsafe Class with a ItemBuilder */
    //		public Unsafe(ItemBuilder builder) {
    //			this.builder = builder;
    //		}
    //
    //		/**
    //		 * Sets a NBT Tag {@code String} into the NBT Tag Compound of the Item
    //		 *
    //		 * @param key   The Name on which the NBT Tag should be saved
    //		 * @param value The Value that should be saved
    //		 */
    //		public Unsafe setString(String key, String value) {
    //			this.builder.item = this.utils.setString(this.builder.item, key, value);
    //			this.builder.meta = this.builder.item.getItemMeta();
    //			return this;
    //		}
    //
    //		/** Returns the String that is saved under the key */
    //		public String getString(String key) {
    //			return this.utils.getString(this.builder.item, key);
    //		}
    //
    //		/**
    //		 * Sets a NBT Tag {@code Integer} into the NBT Tag Compound of the Item
    //		 *
    //		 * @param key   The Name on which the NBT Tag should be savbed
    //		 * @param value The Value that should be saved
    //		 */
    //		public Unsafe setInt(String key, int value) {
    //			this.builder.item = this.utils.setInt(this.builder.item, key, value);
    //			this.builder.meta = this.builder.item.getItemMeta();
    //			return this;
    //		}
    //
    //		/** Returns the Integer that is saved under the key */
    //		public int getInt(String key) {
    //			return this.utils.getInt(this.builder.item, key);
    //		}
    //
    //		/**
    //		 * Sets a NBT Tag {@code Double} into the NBT Tag Compound of the Item
    //		 *
    //		 * @param key   The Name on which the NBT Tag should be savbed
    //		 * @param value The Value that should be saved
    //		 */
    //		public Unsafe setDouble(String key, double value) {
    //			this.builder.item = this.utils.setDouble(this.builder.item, key, value);
    //			this.builder.meta = this.builder.item.getItemMeta();
    //			return this;
    //		}
    //
    //		/** Returns the Double that is saved under the key */
    //		public double getDouble(String key) {
    //			return this.utils.getDouble(this.builder.item, key);
    //		}
    //
    //		/**
    //		 * Sets a NBT Tag {@code Boolean} into the NBT Tag Compound of the Item
    //		 *
    //		 * @param key   The Name on which the NBT Tag should be savbed
    //		 * @param value The Value that should be saved
    //		 */
    //		public Unsafe setBoolean(String key, boolean value) {
    //			this.builder.item = this.utils.setBoolean(this.builder.item, key, value);
    //			this.builder.meta = this.builder.item.getItemMeta();
    //			return this;
    //		}
    //
    //		/** Returns the Boolean that is saved under the key */
    //		public boolean getBoolean(String key) {
    //			return this.utils.getBoolean(this.builder.item, key);
    //		}
    //
    //		/** Returns a Boolean if the Item contains the NBT Tag named key */
    //		public boolean containsKey(String key) {
    //			return this.utils.hasKey(this.builder.item, key);
    //		}
    //
    //		/** Accesses back the ItemBuilder and exists the Unsafe Class */
    //		public ItemBuilder builder() {
    //			return this.builder;
    //		}
    //
    //		/**
    //		 * This Class contains highly sensitive NMS Code that should not be touched unless you
    //		 want
    //		 * to break the ItemBuilder
    //		 */
    //		private static class ReflectionUtils {
    //
    //			public String getString(ItemStack item, String key) {
    //				Object compound = this.getNBTTagCompound(this.getItemAsNMSStack(item));
    //				if (compound == null) {
    //					return null;
    //				}
    //				try {
    //					return (String) compound.getClass().getMethod("getString", String.class)
    //							.invoke(compound, key);
    //				} catch (IllegalAccessException | InvocationTargetException |
    //						NoSuchMethodException ex) {
    //					ex.printStackTrace();
    //				}
    //				return null;
    //			}
    //
    //			public ItemStack setString(ItemStack item, String key, String value) {
    //				Object nmsItem = this.getItemAsNMSStack(item);
    //				Object compound = this.getNBTTagCompound(nmsItem);
    //				if (compound == null) {
    //					compound = this.getNewNBTTagCompound();
    //				}
    //				try {
    //					compound.getClass().getMethod("setString", String.class, String.class)
    //							.invoke(compound, key, value);
    //					this.setNBTTag(compound, nmsItem);
    //				} catch (IllegalAccessException | InvocationTargetException |
    //						NoSuchMethodException ex) {
    //					ex.printStackTrace();
    //				}
    //				return this.getItemAsBukkitStack(nmsItem);
    //			}
    //
    //			public int getInt(ItemStack item, String key) {
    //				Object compound = this.getNBTTagCompound(this.getItemAsNMSStack(item));
    //				if (compound == null) {
    //					return 0;
    //				}
    //				try {
    //					return (Integer) compound.getClass().getMethod("getInt", String.class)
    //							.invoke(compound, key);
    //				} catch (IllegalAccessException | InvocationTargetException |
    //						NoSuchMethodException ex) {
    //					ex.printStackTrace();
    //				}
    //				return -1;
    //			}
    //
    //			public ItemStack setInt(ItemStack item, String key, int value) {
    //				Object nmsItem = this.getItemAsNMSStack(item);
    //				Object compound = this.getNBTTagCompound(nmsItem);
    //				if (compound == null) {
    //					compound = this.getNewNBTTagCompound();
    //				}
    //				try {
    //					compound.getClass().getMethod("setInt", String.class, int.class)
    //							.invoke(compound, key, value);
    //					this.setNBTTag(compound, nmsItem);
    //				} catch (IllegalAccessException | InvocationTargetException |
    //						NoSuchMethodException ex) {
    //					ex.printStackTrace();
    //				}
    //				return this.getItemAsBukkitStack(nmsItem);
    //			}
    //
    //			public double getDouble(ItemStack item, String key) {
    //				Object compound = this.getNBTTagCompound(this.getItemAsNMSStack(item));
    //				if (compound == null) {
    //					return 0;
    //				}
    //				try {
    //					return (Double) compound.getClass().getMethod("getDouble", String.class)
    //							.invoke(compound, key);
    //				} catch (IllegalAccessException | InvocationTargetException |
    //						NoSuchMethodException ex) {
    //					ex.printStackTrace();
    //				}
    //				return Double.NaN;
    //			}
    //
    //			public ItemStack setDouble(ItemStack item, String key, double value) {
    //				Object nmsItem = this.getItemAsNMSStack(item);
    //				Object compound = this.getNBTTagCompound(nmsItem);
    //				if (compound == null) {
    //					compound = this.getNewNBTTagCompound();
    //				}
    //				try {
    //					compound.getClass().getMethod("setDouble", String.class, double.class)
    //							.invoke(compound, key, value);
    //					this.setNBTTag(compound, nmsItem);
    //				} catch (IllegalAccessException | InvocationTargetException |
    //						NoSuchMethodException ex) {
    //					ex.printStackTrace();
    //				}
    //				return this.getItemAsBukkitStack(nmsItem);
    //			}
    //
    //			public boolean getBoolean(ItemStack item, String key) {
    //				Object compound = this.getNBTTagCompound(this.getItemAsNMSStack(item));
    //				if (compound == null) {
    //					return false;
    //				}
    //				try {
    //					return (Boolean) compound.getClass().getMethod("getBoolean", String.class)
    //							.invoke(compound, key);
    //				} catch (IllegalAccessException | InvocationTargetException |
    //						NoSuchMethodException ex) {
    //					ex.printStackTrace();
    //				}
    //				return false;
    //			}
    //
    //			public ItemStack setBoolean(ItemStack item, String key, boolean value) {
    //				Object nmsItem = this.getItemAsNMSStack(item);
    //				Object compound = this.getNBTTagCompound(nmsItem);
    //				if (compound == null) {
    //					compound = this.getNewNBTTagCompound();
    //				}
    //				try {
    //					compound.getClass().getMethod("setBoolean", String.class, boolean.class)
    //							.invoke(compound, key, value);
    //					this.setNBTTag(compound, nmsItem);
    //				} catch (IllegalAccessException | InvocationTargetException |
    //						NoSuchMethodException ex) {
    //					ex.printStackTrace();
    //				}
    //				return this.getItemAsBukkitStack(nmsItem);
    //			}
    //
    //			public boolean hasKey(ItemStack item, String key) {
    //				Object compound = this.getNBTTagCompound(this.getItemAsNMSStack(item));
    //				if (compound == null) {
    //					return false;
    //				}
    //				try {
    //					return (Boolean) compound.getClass().getMethod("hasKey", String.class)
    //							.invoke(compound, key);
    //				} catch (IllegalAccessException | InvocationTargetException |
    //						NoSuchMethodException e) {
    //					throw new RuntimeException(e);
    //				}
    //			}
    //
    //			public Object getNewNBTTagCompound() {
    //				String ver = Bukkit.getServer().getClass().getName().split("\\.")[3];
    //				try {
    //					return Class.forName("net.minecraft.server." + ver + ".NBTTagCompound")
    //							.getConstructor().newInstance();
    //				} catch (ClassNotFoundException | IllegalAccessException |
    //				InstantiationException |
    //						InvocationTargetException | NoSuchMethodException ex) {
    //					throw new RuntimeException(ex);
    //				}
    //			}
    //
    //			public void setNBTTag(Object tag, Object item) {
    //				try {
    //					item.getClass().getMethod("setTag", tag.getClass()).invoke(item, tag);
    //				} catch (IllegalAccessException | InvocationTargetException |
    //						NoSuchMethodException ex) {
    //					throw new RuntimeException(ex);
    //				}
    //			}
    //
    //			public Object getNBTTagCompound(Object nmsStack) {
    //				try {
    //					if (nmsStack == null) {
    //						return null;
    //					}
    //					return nmsStack.getClass().getMethod("getTag").invoke(nmsStack);
    //				} catch (IllegalAccessException | InvocationTargetException |
    //						NoSuchMethodException ex) {
    //					ex.printStackTrace();
    //				}
    //				return null;
    //			}
    //
    //			public Object getItemAsNMSStack(ItemStack item) {
    //				try {
    //					Method m =
    //							this.getCraftItemStackClass().getMethod("asNMSCopy", ItemStack
    //							.class);
    //					return m.invoke(null, item);
    //				} catch (NoSuchMethodException | IllegalAccessException |
    //						InvocationTargetException ex) {
    //					throw new RuntimeException(ex);
    //				}
    //			}
    //
    //			public ItemStack getItemAsBukkitStack(Object nmsStack) {
    //				try {
    //					Method m = this.getCraftItemStackClass()
    //							.getMethod("asBukkitCopy", nmsStack.getClass());
    //					return (ItemStack) m.invoke(null, nmsStack);
    //				} catch (NoSuchMethodException | InvocationTargetException |
    //						IllegalAccessException ex) {
    //					ex.printStackTrace();
    //				}
    //				return null;
    //			}
    //
    //			public Class<?> getCraftItemStackClass() {
    //				String ver = Bukkit.getServer().getClass().getName().split("\\.")[3];
    //				try {
    //					return Class.forName(
    //							"org.bukkit.craftbukkit." + ver + ".inventory.CraftItemStack");
    //				} catch (ClassNotFoundException ex) {
    //					throw new RuntimeException(ex);
    //				}
    //			}
    //
    //		}
    //
    //	}

}

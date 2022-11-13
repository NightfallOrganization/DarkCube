package eu.darkcube.system.darkessentials.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.darkcube.system.ChatUtils;
import eu.darkcube.system.inventory.api.util.ItemBuilder;

public class NumbzUtils {
	public static boolean containsStringIgnoreCase(String string,
					Collection<String> list) {
		for (String s : list) {
			if (s.equalsIgnoreCase(string))
				return true;
		}
		return false;
	}

	public static boolean containsStringIgnoreCase(String string,
					String... list) {
		return containsStringIgnoreCase(string, Arrays.asList(list));
	}

	public static double getDistance(Location l1, Location l2) {
		if (l1 == null || l2 == null || !l1.getWorld().equals(l2.getWorld())) {
			return Double.MAX_VALUE;
		}
		double x = l1.getX() - l2.getX();
		double y = l1.getY() - l2.getY();
		double z = l1.getZ() - l2.getZ();
		return Math.sqrt(x * x + y * y + z * z);
	}

	public static double getDistance(Entity e1, Entity e2) {
		if (e1 == null || e2 == null)
			return Double.MAX_VALUE;
		return getDistance(e1.getLocation(), e2.getLocation());
	}

	public static Object getWeightedRandom(WeightedObject... objects) {
		return getWeightedRandom(objects);
	}

	public static Object getWeightedRandom(Collection<WeightedObject> objects) {
		List<Object> list = new ArrayList<>();
		for (WeightedObject ws : objects) {
			for (int i = 0; i < ws.getWeight(); i++) {
				list.add(ws.getObject());
			}
		}
		return list.get(new Random().nextInt(list.size()));
	}

	public static Set<WeightedObject>
					toWeightedObjectList(Collection<? extends Object> list) {
		Set<WeightedObject> weightedList = new HashSet<>();
		list.forEach(t -> weightedList.add(new WeightedObject(t, 1)));
		return weightedList;
	}

	public static ItemStack getNamedItemStack(ItemStack itemStack, String name,
					String... lore) {
		if (itemStack == null || name == null)
			return null;
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public static ItemStack getNamedItemStack(Material material, int amount,
					short damage, String name, String... lore) {
		if (material == null || name == null)
			return null;
		return getNamedItemStack(new ItemStack(material, amount,
						damage), name, lore);
	}

	public static ItemStack getNamedItemStack(Material material, int amount,
					String name, String... lore) {
		if (material == null || name == null)
			return null;
		return getNamedItemStack(new ItemStack(material, amount), name, lore);
	}

	public static ItemStack getNamedItemStack(Material material, String name,
					String... lore) {
		if (material == null || name == null)
			return null;
		return getNamedItemStack(new ItemStack(material), name, lore);
	}

	public static ItemStack setNBT(ItemStack itemStack, String key,
					String value) {
		if (itemStack == null)
			return null;
		if (key == null || value == null)
			return itemStack;
		return new ItemBuilder(
						itemStack).unsafe().setString(key, value).builder().build();
//		net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
//		NBTTagCompound tag = nmsItemStack.getTag();
//		if (tag == null)
//			tag = new NBTTagCompound();
//		tag.set(key, new NBTTagString(value));
//		nmsItemStack.setTag(tag);
//		return CraftItemStack.asBukkitCopy(nmsItemStack);
	}

	public static String getTagValue(ItemStack itemStack, String key) {
		if (itemStack == null || key == null)
			return "";
		String tag = new ItemBuilder(itemStack).unsafe().getString(key);
		return tag == null ? "" : tag;
//		net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
//		NBTTagCompound tag = nmsItemStack.getTag();
//		if (tag == null || tag.getString(key) == null)
//			return "";
//		return tag.getString(key);
	}

	public static ItemStack addEnchGlow(ItemStack itemStack) {
		if (itemStack == null)
			return null;
		ItemMeta meta = itemStack.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemStack.setItemMeta(meta);
		if (itemStack.getType().equals(Material.BOW)) {
			itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		} else {
			itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		}
		return itemStack;
	}

	public static ItemStack removeAllEnchantments(ItemStack itemStack) {
		if (itemStack == null)
			return null;
		for (Enchantment e : Enchantment.values()) {
			itemStack.removeEnchantment(e);
		}
		return itemStack;
	}

	public static void sendActionbar(String message, Player... players) {
		if (message == null)
			return;
		ChatUtils.ChatEntry.buildActionbar(new ChatUtils.ChatEntry.Builder().text(message).build()).sendPlayer(players);
	}

	public static void sendActionbarToAll(String message, Player... exclude) {
		if (message == null)
			return;
//		PacketPlayOutChat packet = new PacketPlayOutChat(
//						ChatSerializer.a("{\"text\":\"" + message + "\"}"),
//						(byte) 2);
		List<Player> excludeList = Arrays.asList(exclude);
		ChatUtils.ChatEntry.buildActionbar(new ChatUtils.ChatEntry.Builder().text(message).build()).sendPlayer(Bukkit.getOnlinePlayers().stream().filter(p -> !excludeList.contains(p)).collect(Collectors.toList()).toArray(new Player[0]));
	}

	public static String secondsToTime(int seconds, boolean showZeroHours,
					boolean showZeroMins, String color1, String color2) {
		if (color1 == null || color2 == null)
			return "Color null";
		if (seconds < 0)
			return "seconds<0";
		int minutes = 0;
		int hours = 0;
		StringBuilder returnString = new StringBuilder(color1);
		if (seconds >= 60) {
			minutes = (seconds - (seconds % 60)) / 60;
			seconds = seconds % 60;
		}
		if (minutes >= 60 || showZeroHours) {
			hours = (minutes - (minutes % 60)) / 60;
			minutes = minutes % 60;
			if (hours < 10) {
				if (hours != 0 || showZeroHours) {
					returnString.append(0).append(hours);
				}
			} else {
				returnString.append(hours);
			}
			returnString.append(color2).append(":").append(color1);
		}
		if (minutes < 10) {
			if (minutes != 0 || showZeroMins || showZeroHours || hours != 0) {
				returnString.append(0).append(minutes).append(color2).append(":").append(color1);
			}
		} else {
			returnString.append(minutes).append(color2).append(":").append(color1);
		}
		if (seconds < 10) {
			returnString.append(0).append(seconds);
		} else {
			returnString.append(seconds);
		}
		return returnString.toString();
	}

	public static String secondsToTime(int seconds, boolean showZeroHours,
					boolean showZeroMins) {
		return secondsToTime(seconds, showZeroHours, showZeroMins, "", "");
	}

}

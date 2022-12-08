/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.holograms.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import eu.darkcube.system.ReflectionUtils;
import eu.darkcube.system.ReflectionUtils.PackageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public final class Book {

	private List<TextComponent[]> pages = new ArrayList<>();

//	private static final Method M_a = Reflection.getMethod(
//			Reflection.getVersionClass(Reflection.MINECRAFT_PREFIX, "IChatBaseComponent.ChatSerializer"), "a",
//			String.class);
	private static final Method METHOD_a = ReflectionUtils.getMethod("IChatBaseComponent.ChatSerializer",
			PackageType.MINECRAFT_SERVER, "a", String.class);

	public Book() {
	}

	public Book addPage(TextComponent[] components) {
		this.pages.add(components);
		return this;
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	public ItemStack build() {
		ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);
		BookMeta meta = (BookMeta) book.getItemMeta();
		List list = null;
		try {
			Field f = meta.getClass().getDeclaredField("pages");
			f.setAccessible(true);
			list = (List<?>) f.get(meta);
			for (TextComponent[] page : this.pages) {
				list.add(ReflectionUtils.invokeMethod(null, Book.METHOD_a, ComponentSerializer.toString(page)));
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return book;
	}

}

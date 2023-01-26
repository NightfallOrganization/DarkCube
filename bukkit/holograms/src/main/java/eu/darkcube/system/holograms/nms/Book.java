/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.holograms.nms;

import eu.darkcube.system.util.ReflectionUtils;
import eu.darkcube.system.util.ReflectionUtils.PackageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class Book {

	private static final Method METHOD_a =
			ReflectionUtils.getMethod("IChatBaseComponent.ChatSerializer",
					PackageType.MINECRAFT_SERVER, "a", String.class);
	private List<TextComponent[]> pages = new ArrayList<>();

	public Book() {
	}

	public Book addPage(TextComponent[] components) {
		this.pages.add(components);
		return this;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public ItemStack build() {
		ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);
		BookMeta meta = (BookMeta) book.getItemMeta();
		List list;
		try {
			Field f = meta.getClass().getDeclaredField("pages");
			f.setAccessible(true);
			list = (List<?>) f.get(meta);
			for (TextComponent[] page : this.pages) {
				list.add(ReflectionUtils.invokeMethod(null, Book.METHOD_a,
						ComponentSerializer.toString(page)));
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return book;
	}

}

package eu.darkcube.system.holograms.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import eu.darkcube.system.Reflection;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public final class Book {

	private List<TextComponent[]> pages = new ArrayList<>();

	private static final Method M_a = Reflection.getMethod(
			Reflection.getVersionClass(Reflection.MINECRAFT_PREFIX, "IChatBaseComponent.ChatSerializer"), "a",
			String.class);

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
				list.add(Reflection.invokeMethod(Book.M_a, null, ComponentSerializer.toString(page)));
			}
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return book;
	}

}
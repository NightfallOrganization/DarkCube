package eu.darkcube.system.lobbysystem.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.Inventory;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.SkullUtils;

public class InventorySettings extends Inventory {

	private User user;

	public InventorySettings(User user) {
		super(Bukkit.createInventory(null, 6 * 9, Item.INVENTORY_SETTINGS.getDisplayName(user)),
				InventoryType.SETTINGS);
		this.user = user;
	}

	@Override
	public void playAnimation(User user) {
		this.animate(user == null ? this.user : user, false);
	}

	@Override
	public void skipAnimation(User user) {
		this.animate(user == null ? this.user : user, true);
	}

	private void animate(User user, boolean instant) {
		user.playSound(Sound.NOTE_STICKS, 1, 1);
		new BukkitRunnable() {

			private int count = 0;

			private final ItemStack i1 = Item.LIGHT_GRAY_GLASS_PANE.getItem(user);

			private final ItemStack i2 = Item.DARK_GRAY_GLASS_PANE.getItem(user);

			@Override
			public void run() {
				if (this.count >= 13 || user.getOpenInventory() != InventorySettings.this) {
					this.cancel();
					return;
				}
				if (!instant)
					user.playSound(Sound.NOTE_STICKS, 1, 1);
				switch (this.count) {
				case 0:
					InventorySettings.this.handle.setItem(0, this.i1);
					InventorySettings.this.handle.setItem(1, this.i1);
					InventorySettings.this.handle.setItem(9, this.i2);
					break;
				case 1:
					InventorySettings.this.handle.setItem(2, this.i1);
					InventorySettings.this.handle.setItem(10, this.i2);
					InventorySettings.this.handle.setItem(18, this.i1);
					break;
				case 2:
					InventorySettings.this.handle.setItem(3, this.i1);
					InventorySettings.this.handle.setItem(11, this.i2);
					InventorySettings.this.handle.setItem(19, this.i2);
					InventorySettings.this.handle.setItem(27, this.i2);
					break;
				case 3:
					InventorySettings.this.handle.setItem(4, Item.INVENTORY_SETTINGS.getItem(user));
					InventorySettings.this.handle.setItem(12, this.i1);
					InventorySettings.this.handle.setItem(20, this.i2);
					InventorySettings.this.handle.setItem(28, this.i1);
					InventorySettings.this.handle.setItem(36, this.i1);
					break;
				case 4:
					InventorySettings.this.handle.setItem(5, this.i1);
					InventorySettings.this.handle.setItem(13, this.i1);
					InventorySettings.this.handle.setItem(21, this.i2);
					if (user.isSounds()) {
						InventorySettings.this.handle.setItem(29, Item.INVENTORY_SETTINGS_SOUNDS_ON.getItem(user));
					} else {
						InventorySettings.this.handle.setItem(29, Item.INVENTORY_SETTINGS_SOUNDS_OFF.getItem(user));
					}
					InventorySettings.this.handle.setItem(37, this.i2);
					InventorySettings.this.handle.setItem(45, this.i1);
					break;
				case 5:
					InventorySettings.this.handle.setItem(6, this.i1);
					InventorySettings.this.handle.setItem(14, this.i1);
					InventorySettings.this.handle.setItem(22, this.i2);
					InventorySettings.this.handle.setItem(30, this.i1);
					InventorySettings.this.handle.setItem(38, this.i1);
					InventorySettings.this.handle.setItem(46, this.i1);
					break;
				case 6:
					InventorySettings.this.handle.setItem(7, this.i1);
					InventorySettings.this.handle.setItem(15, this.i2);
					InventorySettings.this.handle.setItem(23, this.i2);
					String textureId = "";
					String name = "";
					switch (user.getLanguage()) {
					case ENGLISH:
						textureId = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1"
								+ "cmUvYTE3MDFmMjE4MzVhODk4YjIwNzU5ZmIzMGE1ODNhMzhiOTk0YWJmNjBkMzkxMmFiNGNlOWYyMzExZTc0Zj"
								+ "cyIn19fQ==";
						name = "English";
						break;
					case GERMAN:
						textureId = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1"
								+ "cmUvNWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZmVjYzc3Yj"
								+ "NmIn19fQ==";
						name = "Deutsch";
						break;
					}
					ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName("§e" + name);
					item.setItemMeta(meta);
					item = new ItemBuilder(item).getUnsafe()
							.setString("language", user.getLanguage().name())
							.builder()
							.build();
					SkullUtils.setSkullTextureId(item, textureId);
					InventorySettings.this.handle.setItem(31, item);
//					handle.setItem(31, Item.INVENTORY_COMPASS_WOOLBATTLE.getItem(user));
					InventorySettings.this.handle.setItem(39, this.i2);
					InventorySettings.this.handle.setItem(47, this.i2);
					break;
				case 7:
					InventorySettings.this.handle.setItem(8, this.i1);
					InventorySettings.this.handle.setItem(16, this.i2);
					InventorySettings.this.handle.setItem(24, this.i2);
					InventorySettings.this.handle.setItem(32, this.i1);
					InventorySettings.this.handle.setItem(40, this.i1);
					InventorySettings.this.handle.setItem(48, this.i2);
					break;
				case 8:
					InventorySettings.this.handle.setItem(17, this.i2);
					InventorySettings.this.handle.setItem(25, this.i2);
					if (user.isAnimations()) {
						InventorySettings.this.handle.setItem(33, Item.INVENTORY_SETTINGS_ANIMATIONS_ON.getItem(user));
					} else {
						InventorySettings.this.handle.setItem(33, Item.INVENTORY_SETTINGS_ANIMATIONS_OFF.getItem(user));
					}
					InventorySettings.this.handle.setItem(41, this.i2);
					InventorySettings.this.handle.setItem(49, this.i2);
					break;
				case 9:
					InventorySettings.this.handle.setItem(26, this.i1);
					InventorySettings.this.handle.setItem(34, this.i1);
					InventorySettings.this.handle.setItem(42, this.i1);
					InventorySettings.this.handle.setItem(50, this.i2);
					break;
				case 10:
					InventorySettings.this.handle.setItem(35, this.i2);
					InventorySettings.this.handle.setItem(43, this.i2);
					InventorySettings.this.handle.setItem(51, this.i2);
					break;
				case 11:
					InventorySettings.this.handle.setItem(44, this.i1);
					InventorySettings.this.handle.setItem(52, this.i1);
					break;
				case 12:
					InventorySettings.this.handle.setItem(53, this.i1);
					break;
				}
				this.count++;
				if (instant)
					this.run();
			}

		}.runTaskTimer(Lobby.getInstance(), 1, 1);
	}

}

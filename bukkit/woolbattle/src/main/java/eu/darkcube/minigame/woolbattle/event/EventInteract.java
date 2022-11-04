package eu.darkcube.minigame.woolbattle.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.user.User;

public class EventInteract extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancel = false;
	private ItemStack item;
	private Inventory inventory;
	private ClickType click;
	private boolean isInteract;
	private User user;

	public EventInteract(Player who, ItemStack item, Inventory inv, ClickType click) {
		super(who);
		user = Main.getInstance().getUserWrapper().getUser(who.getUniqueId());
		this.inventory = inv;
		this.item = item;
		this.click = click;
		isInteract = false;
	}

	public EventInteract(Player who, ItemStack item, Inventory inv, ClickType click, boolean isInteract) {
		super(who);
		user = Main.getInstance().getUserWrapper().getUser(who.getUniqueId());
		this.isInteract = isInteract;
		this.inventory = inv;
		this.item = item;
		this.click = click;
	}

	public boolean isInteract() {
		return isInteract;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public ItemStack getItem() {
		return item;
	}

	public ClickType getClick() {
		return click;
	}

	public User getUser() {
		return user;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}

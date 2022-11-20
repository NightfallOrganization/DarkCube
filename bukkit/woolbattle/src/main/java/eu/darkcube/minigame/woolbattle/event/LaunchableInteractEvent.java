package eu.darkcube.minigame.woolbattle.event;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class LaunchableInteractEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private Projectile entity;
	private EntityType entityType = null;
	private ItemStack item;
	private boolean cancel = false;
	private Action action;

	public LaunchableInteractEvent(Player who, Projectile entity, ItemStack item) {
		super(who);
		this.item = item;
		this.entity = entity;
		if (entity != null)
			this.entityType = entity.getType();
		action = Action.RIGHT_CLICK_AIR;
	}

	public LaunchableInteractEvent(Player who, EntityType entity, ItemStack item, Action action) {
		super(who);
		this.entity = null;
		this.entityType = entity;
		this.item = item;
		this.action = action;
	}

	public Action getAction() {
		return this.action;
	}

	public ItemStack getItem() {
		return this.item;
	}

	public Projectile getEntity() {
		return this.entity;
	}

	public EntityType getEntityType() {
		return this.entityType;
	}

	@Override
	public HandlerList getHandlers() {
		return LaunchableInteractEvent.handlers;
	}

	public static HandlerList getHandlerList() {
		return LaunchableInteractEvent.handlers;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}

package eu.darkcube.minigame.woolbattle.event;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_8_R3.Entity;

public class LaunchableInteractEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private Projectile entity;
	private EntityType entityType = null;
	private ItemStack item;
	private boolean cancel = false;

	public LaunchableInteractEvent(Player who, Projectile entity, ItemStack item) {
		super(who);
		this.item = item;
		this.entity = entity;
		if (entity != null)
			entityType = entity.getType();
	}

	public LaunchableInteractEvent(Player who, EntityType entity, ItemStack item) {
		super(who);
		this.entity = null;
		this.entityType = entity;
		this.item = item;
	}

	public static void launchSilent(Entity ent) {
		ent.world.addEntity(ent, SpawnReason.CUSTOM);
	}

	public ItemStack getItem() {
		return item;
	}

	public Projectile getEntity() {
		return entity;
	}

	public EntityType getEntityType() {
		return entityType;
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

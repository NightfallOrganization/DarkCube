package de.pixel.bedwars.spawner;

import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import de.pixel.bedwars.Main;

public abstract class ItemSpawner implements Runnable {

	private Block block;
	private int maxItemCount;
	private BukkitTask task;
	private int skipCount;
	protected boolean splitItems = true;

	public ItemSpawner(Block spawnerBlock, int maxItemCount) {
		block = spawnerBlock;
		this.maxItemCount = maxItemCount;
	}

	public final boolean isSplitItems() {
		return splitItems;
	}

	public abstract int getTicksDelay();

	public final void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}

	public final int getSkipCount() {
		return skipCount;
	}

	public final void start() {
		stop();
		task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this, getTicksDelay(), getTicksDelay());
	}

	public final void setBlock(Block block) {
		this.block = block;
	}

	public final void stop() {
		if (task != null)
			task.cancel();
	}

	@Override
	public final void run() {
		spawnItem();
	}

	public final Block getSpawnerBlock() {
		return block;
	}

	public final int getMaxItemCount() {
		return maxItemCount;
	}

	public final Location getSpawnLocation() {
		return block.getLocation().add(0.5, 1.5, 0.5);
	}

	public final void spawnItem() {
		if (skipCount > 0) {
			skipCount--;
			return;
		}
		Location loc = getSpawnLocation();
		Collection<Item> entities = loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5).stream()
				.filter(e -> e.getType() == EntityType.DROPPED_ITEM).map(e -> (Item) e).collect(Collectors.toSet());
		int count = entities.stream().mapToInt(e -> e.getItemStack().getAmount()).sum();
		if (count < maxItemCount) {
			forceSpawnItem();
		}
	}

	public final void forceSpawnItem() {
		forceSpawnItem(getSpawnLocation(), getItem());
	}

	public final void forceSpawnItem(Location loc, ItemStack item) {
		Item i = loc.getWorld().dropItem(loc, item);
		i.setVelocity(new Vector());
		i.teleport(loc);
		i.setPickupDelay(5);
		if (isSplitItems())
			i.setMetadata("spawnerItem", new FixedMetadataValue(Main.getInstance(), this));
	}

	public abstract ItemStack getItem();
}

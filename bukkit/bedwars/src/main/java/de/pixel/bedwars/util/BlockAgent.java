package de.pixel.bedwars.util;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Stopwatch;

import de.pixel.bedwars.Main;
import net.minecraft.server.v1_8_R3.MinecraftServer;

public class BlockAgent {

	private static final BlockFace[] faces = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
			BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

	private Block origin;
	private Material originalType;
	private byte originalData;
	private boolean useData = true;

	@SuppressWarnings("deprecation")
	public BlockAgent(Block origin) {
		this.origin = origin;
		this.originalType = origin.getType();
		this.originalData = origin.getData();
	}

	public void setUseData(boolean useData) {
		this.useData = useData;
	}

	public void scheduledAction(Limit limit, Limit limitPerAction, Consumer<Block> consumer) {
		Set<Block> blocks = new HashSet<>();
		Set<Block> checked = new HashSet<>();
		Set<Block> toScan = new HashSet<>();
		toScan.add(origin);
		AtomicInteger allBlocksCount = new AtomicInteger(0);
		new BukkitRunnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Stopwatch timer = Stopwatch.createStarted();
				int limitInt = limitPerAction.limit;
				w: while (true) {
					Set<Block> clone = new HashSet<>(toScan);
					f: for (Block b : clone) {
						if (timer.elapsed(TimeUnit.MILLISECONDS) >= 30) {
							break w;
						}
						toScan.remove(b);
						if (allBlocksCount.get() >= limit.limit) {
							cancel();
							return;
						}
						if (checked.contains(b) || blocks.contains(b)) {
							continue f;
						}
						if (b.getType() != originalType || (b.getData() != originalData && useData)) {
							checked.add(b);
							continue f;
						}
						if (consumer != null) {
							consumer.accept(b);
						}
						blocks.add(b);
						allBlocksCount.addAndGet(1);
						limitInt--;
						for (BlockFace f : faces) {
							toScan.add(b.getRelative(f));
						}
						if (limitInt <= 0 || MinecraftServer.getServer().recentTps[0] < 19) {
							break w;
						}
					}
					if (toScan.isEmpty()) {
						cancel();
						break;
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 1, 1);
	}

	public Set<Block> scan() {
		return scan(Limit.NO_LIMIT);
	}

	public Set<Block> scan(Limit limit) {
		return scan(limit, null);
	}

	public Set<Block> scan(Limit limit, Consumer<Block> consumer) {
		Set<Block> blocks = new HashSet<>();
		Set<Block> checked = new HashSet<>();
		scan(blocks, checked, limit.getLimit(), consumer);
		return blocks;
	}

	private Set<Block> scan(Set<Block> blocks, Set<Block> checked, int maxCount, Consumer<Block> consumer) {
		Set<Block> toScan = new HashSet<>();
		toScan.add(origin);
		return scan(toScan, blocks, checked, maxCount, consumer);
	}

	@SuppressWarnings("deprecation")
	private Set<Block> scan(Set<Block> toScan, Set<Block> blocks, Set<Block> checked, int maxCount,
			Consumer<Block> consumer) {
//		toScan.add(origin);
		w: while (true) {
			Set<Block> clone = new HashSet<>(toScan);
			toScan.clear();
			f: for (Block b : clone) {
				if (maxCount == 0) {
					break w;
				}
				if (checked.contains(b) || blocks.contains(b)) {
					continue f;
				}
				if (b.getType() != origin.getType() || (b.getData() != origin.getData() && useData)) {
					checked.add(b);
					continue f;
				}
				if (consumer != null)
					consumer.accept(b);
				blocks.add(b);
				maxCount--;
				for (BlockFace f : faces) {
					toScan.add(b.getRelative(f));
				}
			}
			if (toScan.isEmpty()) {
				break;
			}
		}
		return toScan;
	}

	public static class Limit {

		public static final Limit NO_LIMIT = Limit.of(Integer.MAX_VALUE);

		private int limit;

		private Limit(int limit) {
			this.limit = limit;
		}

		public static Limit of(int limit) {
			return new Limit(limit);
		}

		public int getLimit() {
			return limit;
		}
	}
}

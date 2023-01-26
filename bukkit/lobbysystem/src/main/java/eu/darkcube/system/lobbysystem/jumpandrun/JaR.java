/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.jumpandrun;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.util.ParticleEffect;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.math.BigInteger;
import java.util.Random;

public class JaR {
	private static final int PREGEN = 3;
	private static final int KEEP = 3;
	private static final int FORCE_FIELD_MAX_DISTANCE = 20;
	private static final int FORCE_FIELD_MIN_DISTANCE = 5;
	private static final double FORCE_FIELD_STRENGTH = 0.6;
	private static final double UP_RATIO = 1D / 6D;
	// 1/6 default (on average 1 block up every 6 blocks)
	final JaRManager manager;
	final LobbyUser user;
	final Random r = new Random();
	JaRListener jaRListener;
	BukkitRunnable particles;
	JarNode node;
	JarNode fallbackNode = null;
	JarNode oldNode = null;
	boolean destroyed = false;

	public JaR(JaRManager manager, LobbyUser user) {
		this.manager = manager;
		this.user = user;
		this.jaRListener = new JaRListener(this);
		generate();
		(particles = new BukkitRunnable() {
			@Override
			public void run() {
				JarNode node = JaR.this.node;
				if (node == null)
					node = fallbackNode;
				if (node == null)
					return;
				ParticleEffect.VILLAGER_HAPPY.display(0.2F, 0.2F, 0.2F, 1, 1,
						getCurrentBlock().getLocation().add(0.5, 1.5, 0.5),
						user.getUser().asPlayer());
				int n = node.prev != null ? node.prev.count / 5 : 0;
				user.getUser().sendActionBar(Component.text("-[").color(NamedTextColor.GRAY)
						.append(Component.text(Integer.toString(n))
								.color(NamedTextColor.LIGHT_PURPLE))
						.append(Component.text("]-").color(NamedTextColor.GRAY)));
			}
		}).runTaskTimer(Lobby.getInstance(), 2, 2);
	}

	public void reset() {
		if (node != null)
			deleteAll(node);
		oldNode = null;
		fallbackNode = node;
		node = null;
		generate();
		if (getCurrentBlock() != null)
			user.getUser().asPlayer().teleport(getCurrentBlock().getLocation().add(0.5, 1, 0.5)
					.setDirection(getCurrentDirection()));
		user.playSound(Sound.VILLAGER_NO, 100, 1);
	}

	public void startFromBeginning() {
		oldNode = node;
		node = null;
		new BukkitRunnable() {
			@Override
			public void run() {
				deleteAll(oldNode);
				generate();
				oldNode = null;
				user.getUser().asPlayer().teleport(getCurrentBlock().getLocation().add(0.5, 1, 0.5)
						.setDirection(getCurrentDirection()));
			}
		}.runTaskLater(Lobby.getInstance(), 40);
		user.playSound(Sound.LEVEL_UP, 1, 1);
	}

	private void deleteAll(JarNode node) {
		if (node == null)
			return;
		while (node.prev != null)
			node = node.prev;
		do {
			node.block.toLocation(node.region.world).getBlock().setType(Material.AIR);
			node = node.next;
		} while (node != null);
	}

	public void stop() {
		particles.cancel();
		this.jaRListener.unregister();
		generate();
		deleteAll(node);
		node = null;
		destroyed = true;
	}

	public LobbyUser getUser() {
		return user;
	}

	public Block getCurrentBlock() {
		if (destroyed)
			return null;
		generate();
		if (node == null)
			return null;
		return node.block.toLocation(node.region.world).getBlock();
	}

	public Vector getCurrentDirection() {
		if (destroyed)
			return null;
		generate();
		return getDirection(node);
	}

	public JarNode getNextBlock() {
		if (destroyed)
			return null;
		generate();
		JarNode cur = node;
		for (int c = 0; c < KEEP; c++) {
			if (cur.prev != null) {
				cur = cur.prev;
			} else {
				cur = null;
				break;
			}
		}
		if (cur != null) {
			cur.block.toLocation(cur.region.world).getBlock().setType(Material.AIR);
			cur.next.prev = null;
		}
		if (node.next == null)
			return null;
		node = node.next;
		return node;
	}

	private void generate() {
		if (destroyed)
			return;
		if (node == null) {
			if (manager.regions.isEmpty())
				return;
			JaRRegion region = oldNode != null
					? oldNode.region
					: manager.regions.get(r.nextInt(manager.regions.size()));
			Vector start = new Vector(region.x, region.y, region.z).add(
					new Vector(r.nextInt(region.widthX), r.nextInt(region.height / 5),
							r.nextInt(region.widthZ)));
			node = new JarNode(this, start);
			node.count = 0;
			node.color = randomColor(null);
			node.region = region;
			if (oldNode != null) {
				node.count = oldNode.count;
				node.add = oldNode.add;
			}
			build(node);
		}
		//		setBorders(node.region);
		int depth = 1;
		JarNode current = node;
		while (depth < PREGEN) {
			if (current.last) {
				return;
			}
			if (current.next != null) {
				current = current.next;
				build(current);
				depth++;
				continue;
			}
			depth++;
			Vector odirection = getDirection(current).normalize();
			Vector center = new Vector(current.region.x + current.region.widthX / 2D,
					current.region.y + current.region.height / 2D,
					current.region.z + current.region.widthZ / 2D);
			Vector forcedDir = center.subtract(current.block);
			forcedDir.setY(0);
			forcedDir.normalize();
			double distX = distance(current.block.getX(), current.region.x,
					current.region.x + current.region.widthX);
			double distZ = distance(current.block.getZ(), current.region.z,
					current.region.z + current.region.widthZ);
			double factorX = calculateFactorDist(distX);
			double factorZ = calculateFactorDist(distZ);
			Vector direction = odirection.clone().normalize();
			double dot = odirection.normalize().dot(forcedDir);
			Vector cross = direction.getCrossProduct(new Vector(0, 1, 0));
			int turn = r.nextInt(2) * 2 - 1;
			boolean usingFactors =
					((factorX != 0 || factorZ != 0) && dot < 0.3) || (distX < 15 || distZ < 15);
			if (usingFactors)
				turn = current.turn;
			cross.multiply(turn);
			if (usingFactors) {
				if (distX < 12 || distZ < 12) {
					Vector ndir = direction.clone().add(cross);
					if (direction.dot(forcedDir) < ndir.dot(forcedDir)) {
						direction = ndir;
					} else {
						ndir = direction.clone().add(cross.clone().multiply(-1));
						if (direction.dot(forcedDir) < ndir.dot(forcedDir)) {
							direction = ndir;
						}
					}
				} else {
					Vector ndir = cross.clone().add(forcedDir.multiply(distance(dot, -2, 2)));
					direction.add(ndir.multiply(factorX));
					direction.add(ndir.multiply(factorZ));
					direction.add(cross.clone().multiply(Math.abs(dot)).multiply(.7));
				}
			} else {
				direction.multiply(3).add(cross.multiply(r.nextDouble() + .3));
			}
			direction.normalize();
			Vector next = getJump(current.block, direction);
			JarNode old = current;
			current = new JarNode(current, next);
			current.count = old.count + 1;
			current.color = current.count % 5 == 0 ? randomColor(old.color) : old.color;
			if (current.count % 25 == 0) { // Every 25 jumps
				current.add = current.add.add(BigInteger.ONE);
			}
			current.turn = turn;

			if (current.block.getY() > current.region.y + current.region.height - 5
					&& current.count % 5 == 0) {
				current.last = true;
				build(current);
				return;
			}
			build(current);
		}
	}

	private double calculateFactorDist(double dist) {
		if (dist > FORCE_FIELD_MAX_DISTANCE) {
			// lets just ignore this....
			return 0;
		}
		return 1 - Math.pow(Math.min(1,
						Math.max(0, (dist - FORCE_FIELD_MIN_DISTANCE)) / (FORCE_FIELD_MAX_DISTANCE)),
				FORCE_FIELD_STRENGTH);
	}

	//	private void setBorders(JaRRegion region) {
	//		for (int dx = 0; dx < region.widthX; dx++) {
	//			setBlock(region, dx, 0, 0);
	//			setBlock(region, dx, 0, region.widthZ);
	//			setBlock(region, dx, region.height, 0);
	//			setBlock(region, dx, region.height, region.widthZ);
	//		}
	//		for (int dz = 0; dz < region.widthZ; dz++) {
	//			setBlock(region, 0, 0, dz);
	//			setBlock(region, region.widthX, 0, dz);
	//			setBlock(region, 0, region.height, dz);
	//			setBlock(region, region.widthX, region.height, dz);
	//		}
	//		for (int dy = 0; dy < region.height; dy++) {
	//			setBlock(region, 0, dy, 0);
	//			setBlock(region, 0, dy, region.widthZ);
	//			setBlock(region, region.widthX, dy, 0);
	//			setBlock(region, region.widthX, dy, region.widthZ);
	//		}
	//	}

	private double distance(double value, double min, double max) {
		return Math.min(Math.abs(value - min), Math.abs(value - max));
	}

	//	private void setBlock(JaRRegion region, int dx, int dy, int dz) {
	//		region.world.getBlockAt(region.x + dx, region.y + dy, region.z + dz)
	//				.setType(Material.DIAMOND_BLOCK);
	//	}

	private void build(JarNode node) {
		Block block = node.block.toLocation(node.region.world).getBlock();
		if (node.last) {
			block.setType(Material.GOLD_BLOCK);
			return;
		}
		block.setType(Material.STAINED_CLAY);
		block.setData(node.color.getWoolData());
	}

	private DyeColor randomColor(DyeColor oldColor) {
		DyeColor c;
		do {
			c = DyeColor.values()[r.nextInt(DyeColor.values().length)];
		} while (c == oldColor);
		return c;
	}

	private Vector getDirection(JarNode node) {
		if (node.prev != null) {
			return node.block.clone().subtract(node.prev.block).setY(0).normalize();
		}
		if (node.next != null) {
			return node.next.block.clone().subtract(node.block).setY(0).normalize();
		}
		Vector vec = node.block.clone().subtract(new Vector(node.region.x + node.region.widthX / 2D,
				node.region.y + node.region.height / 2D, node.region.z + node.region.widthZ / 2D));
		vec.crossProduct(new Vector(0, 1, 0));
		if (vec.lengthSquared() == 0) {
			do {
				vec = new Vector(r.nextDouble() - .5, 0, r.nextDouble() - .5);
			} while (vec.lengthSquared() == 0);
		}
		return vec.normalize();
	}

	private Vector getJump(Vector start, Vector dirIn) {
		start = start.clone().setX(start.getBlockX()).setZ(start.getBlockZ())
				.add(new Vector(0.5, 0, 0.5));
		Vector dir = dirIn.clone().normalize();
		double dx = 0.5 - r.nextDouble();
		double dy = r.nextDouble() * UP_RATIO * 2;
		double dz = 0.5 - r.nextDouble();
		dir.add(new Vector(dx, dy, dz).multiply(0.6));
		dir.normalize();
		dir.multiply(3.2 + r.nextDouble() * 1.2);
		dir.setY(Math.min(1, dir.getY()));
		return start.clone().add(dir);
	}
}

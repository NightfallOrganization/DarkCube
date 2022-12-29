/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.commandapi.v3.BoundingBox;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.MinMaxBounds.FloatBound;
import eu.darkcube.system.commandapi.v3.Vector3d;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntitySelector {

	private final int limit;

	private final boolean includeNonPlayers;

	private final boolean currentWordOnly;

	private final Predicate<Entity> filter;

	private final FloatBound distance;

	private final Function<Vector3d, Vector3d> positionGetter;

	private final BoundingBox bb;

	private final BiConsumer<Vector3d, List<? extends Entity>> sorter;

	private final boolean self;

	private final String username;

	private final UUID uuid;

	private final EntityType type;

	public EntitySelector(int limit, boolean includeNonPlayers, boolean currentWordOnly,
			Predicate<Entity> filter, FloatBound distance,
			Function<Vector3d, Vector3d> positionGetter, BoundingBox bb,
			BiConsumer<Vector3d, List<? extends Entity>> sorter, boolean self, String username,
			UUID uuid, EntityType type) {
		this.limit = limit;
		this.includeNonPlayers = includeNonPlayers;
		this.currentWordOnly = currentWordOnly;
		this.filter = filter;
		this.distance = distance;
		this.positionGetter = positionGetter;
		this.bb = bb;
		this.sorter = sorter;
		this.self = self;
		this.username = username;
		this.uuid = uuid;
		this.type = type;
	}

	public int getLimit() {
		return this.limit;
	}

	public boolean includesEntities() {
		return this.includeNonPlayers;
	}

	public boolean isSelfSelector() {
		return this.self;
	}

	public boolean isWorldLimited() {
		return this.currentWordOnly;
	}

	private void checkPermission(CommandSource source) {
	}

	public Entity selectOne(CommandSource source) throws CommandSyntaxException {
		this.checkPermission(source);
		List<? extends Entity> list = this.select(source);
		if (list.isEmpty()) {
			throw EntityArgument.ENTITY_NOT_FOUND.create();
		} else if (list.size() > 1) {
			throw EntityArgument.TOO_MANY_ENTITIES.create();
		} else {
			return list.get(0);
		}
	}

	public List<? extends Entity> select(CommandSource source) {
		this.checkPermission(source);
		if (!this.includeNonPlayers) {
			return this.selectPlayers(source);
		} else if (this.username != null) {
			Player player = Bukkit.getPlayer(this.username);
			return player == null ? Collections.emptyList() : Lists.newArrayList(player);
		} else if (this.uuid != null) {
			for (World world : Bukkit.getWorlds()) {
				for (Entity entity : world.getEntities()) {
					if (entity.getUniqueId().equals(this.uuid)) {
						return Lists.newArrayList(entity);
					}
				}
			}
			return Collections.emptyList();
		} else {
			Vector3d vector3d = this.positionGetter.apply(source.getPos());
			Predicate<Entity> predicate = this.updateFilter(vector3d);
			if (this.self) {
				return source.getEntity() != null && predicate.test(source.getEntity())
						? Lists.newArrayList(source.getEntity())
						: Collections.emptyList();
			}
			List<Entity> list = Lists.newArrayList();
			if (this.isWorldLimited()) {
				this.getEntities(list, source.getWorld(), vector3d, predicate);
			} else {
				for (World serverworld : Bukkit.getWorlds()) {
					this.getEntities(list, serverworld, vector3d, predicate);
				}
			}

			return this.sortAndLimit(vector3d, list);
		}
	}

	private void getEntities(List<Entity> result, World worldIn, Vector3d offset,
			Predicate<Entity> predicate) {
		if (this.bb != null) {
			//	         result.addAll(worldIn.getEntitiesWithinAABB(this.type, this.bb.offset(pos), predicate));
			result.addAll(this.bb.offset(offset).getEntitiesWithin(worldIn, this.type, predicate));
		} else {
			for (Entity ent : worldIn.getEntities()) {
				if (this.type == null || ent.getType() == this.type) {
					if (predicate.test(ent)) {
						result.add(ent);
					}
				}
			}
			//			result.addAll(worldIn.getEntities(this.type, predicate));
		}
	}

	public Player selectOnePlayer(CommandSource source) throws CommandSyntaxException {
		this.checkPermission(source);
		List<Player> list = this.selectPlayers(source);
		if (list.size() != 1) {
			throw EntityArgument.PLAYER_NOT_FOUND.create();
		}
		return list.get(0);
	}

	public List<Player> selectPlayers(CommandSource source) {
		this.checkPermission(source);
		if (this.username != null) {
			Player player = Bukkit.getPlayer(this.username);
			return player == null ? Collections.emptyList() : Lists.newArrayList(player);
		} else if (this.uuid != null) {
			Player player = Bukkit.getPlayer(this.uuid);
			return player == null ? Collections.emptyList() : Lists.newArrayList(player);
		} else {
			Vector3d vector3d = this.positionGetter.apply(source.getPos());
			Predicate<Entity> predicate = this.updateFilter(vector3d);
			if (this.self) {
				if (source.getEntity() instanceof Player) {
					Player player = (Player) source.getEntity();
					if (predicate.test(player)) {
						return Lists.newArrayList(player);
					}
				}

				return Collections.emptyList();
			}
			List<Player> list;
			if (this.isWorldLimited()) {
				list = source.getWorld().getPlayers().stream().filter(predicate)
						.collect(Collectors.toList());
			} else {
				list = Lists.newArrayList();

				for (Player serverplayerentity : Bukkit.getOnlinePlayers()) {
					if (predicate.test(serverplayerentity)) {
						list.add(serverplayerentity);
					}
				}
			}

			return this.sortAndLimit(vector3d, list);
		}
	}

	private Predicate<Entity> updateFilter(Vector3d pos) {
		Predicate<Entity> predicate = this.filter;
		if (this.bb != null) {
			BoundingBox boundingBox = this.bb.offset(pos);
			predicate = predicate.and((entity) -> {
				return boundingBox.intersects(new BoundingBox(entity));
			});
		}

		if (!this.distance.isUnbounded()) {
			predicate = predicate.and((entity) -> {
				return this.distance.testSquared(
						Vector3d.position(entity.getLocation()).squareDistanceTo(pos));
			});
		}

		return predicate;
	}

	private <T extends Entity> List<T> sortAndLimit(Vector3d pos, List<T> entities) {
		if (entities.size() > 1) {
			this.sorter.accept(pos, entities);
		}

		return entities.subList(0, Math.min(this.limit, entities.size()));
	}

}

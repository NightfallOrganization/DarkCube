/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import eu.darkcube.minigame.woolbattle.api.command.CommandSource;
import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.BoundingBox;
import eu.darkcube.minigame.woolbattle.api.util.MinMaxBounds;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.minigame.woolbattle.common.command.arguments.CommonEntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class EntitySelector {
    public static final int INFINITE = Integer.MAX_VALUE;
    public static final BiConsumer<Position, List<? extends Entity>> ORDER_ARBITRARY = (vec3d, list) -> {
    };
    private static final EntityTypeTest<Entity, ?> ANY_TYPE = new EntityTypeTest<Entity, Entity>() {
        @Override
        public Entity tryCast(Entity obj) {
            return obj;
        }

        @Override
        public Class<? extends Entity> getBaseClass() {
            return Entity.class;
        }
    };
    private final int maxResults;
    private final boolean includesEntities;
    private final boolean sameWorldOnly;
    private final List<Predicate<Entity>> contextFreePredicates;
    private final MinMaxBounds.Doubles range;
    private final Function<Position, Position> position;
    @Nullable
    private final BoundingBox aabb;
    private final BiConsumer<Position, List<? extends Entity>> order;
    private final boolean currentEntity;
    @Nullable
    private final String playerName;
    @Nullable
    private final UUID entityUUID;
    private final EntityTypeTest<Entity, ?> type;
    private final boolean usesSelector;

    public EntitySelector(int count, boolean includesNonPlayers, boolean localWorldOnly, List<Predicate<Entity>> predicates, MinMaxBounds.Doubles distance, Function<Position, Position> positionOffset, @Nullable BoundingBox box, BiConsumer<Position, List<? extends Entity>> sorter, boolean senderOnly, @Nullable String playerName, @Nullable UUID uuid, @Nullable EntityType<? extends Entity> type, boolean usesAt) {
        this.maxResults = count;
        this.includesEntities = includesNonPlayers;
        this.sameWorldOnly = localWorldOnly;
        this.contextFreePredicates = predicates;
        this.range = distance;
        this.position = positionOffset;
        this.aabb = box;
        this.order = sorter;
        this.currentEntity = senderOnly;
        this.playerName = playerName;
        this.entityUUID = uuid;
        this.type = (EntityTypeTest<Entity, ?>) (type == null ? EntitySelector.ANY_TYPE : type);
        this.usesSelector = usesAt;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public boolean includesEntities() {
        return this.includesEntities;
    }

    public boolean isSelfSelector() {
        return this.currentEntity;
    }

    public boolean isSameWorldOnly() {
        return this.sameWorldOnly;
    }

    public boolean usesSelector() {
        return this.usesSelector;
    }

    private void checkPermissions(CommandSource source) throws CommandSyntaxException {
        if ((this.usesSelector && !source.hasPermission("minecraft.command.selector"))) { // CraftBukkit // Paper - add bypass for selector perms
            throw CommonEntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create();
        }
    }

    public Entity findSingleEntity(CommandSource source) throws CommandSyntaxException {
        this.checkPermissions(source);
        List<? extends Entity> list = this.findEntities(source);

        if (list.isEmpty()) {
            throw CommonEntityArgument.NO_ENTITIES_FOUND.create();
        } else if (list.size() > 1) {
            throw CommonEntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
        } else {
            return (Entity) list.get(0);
        }
    }

    public List<? extends Entity> findEntities(CommandSource source) throws CommandSyntaxException {
        this.checkPermissions(source);
        if (!this.includesEntities) {
            return this.findPlayers(source);
        } else if (this.playerName != null) {
            WBUser entityplayer = source.woolbattle().user(this.playerName);

            return entityplayer == null ? List.of() : List.of(entityplayer);
        } else if (this.entityUUID != null) {
            Iterator iterator = source.getServer().getAllLevels().iterator();

            while (iterator.hasNext()) {
                ServerLevel worldserver = (ServerLevel) iterator.next();
                Entity entity = worldserver.getEntity(this.entityUUID);

                if (entity != null) {
                    if (entity.getType().isEnabled(source.enabledFeatures())) {
                        return List.of(entity);
                    }
                    break;
                }
            }

            return List.of();
        } else {
            Position vec3d = this.position.apply(source.pos());
            BoundingBox axisalignedbb = this.getAbsoluteAabb(vec3d);
            Predicate predicate;

            if (this.currentEntity) {
                predicate = this.getPredicate(vec3d, axisalignedbb);
                return source.entity() != null && predicate.test(source.entity()) ? List.of(source.entity()) : List.of();
            } else {
                predicate = this.getPredicate(vec3d, axisalignedbb);
                List<Entity> list = new ObjectArrayList();

                if (this.isSameWorldOnly()) {
                    this.addEntities(list, source.world(), axisalignedbb, predicate);
                } else {
                    Iterator iterator1 = source.woolbattle().getServer().getAllLevels().iterator();

                    while (iterator1.hasNext()) {
                        ServerLevel worldserver1 = (ServerLevel) iterator1.next();

                        this.addEntities(list, worldserver1, axisalignedbb, predicate);
                    }
                }

                return this.sortAndLimit(vec3d, list);
            }
        }
    }

    private void addEntities(List<Entity> entities, World world, @Nullable BoundingBox box, Predicate<Entity> predicate) {
        int i = this.getResultLimit();

        if (entities.size() < i) {
            if (box != null) {
                world.getEntities(this.type, box, predicate, entities, i);
            } else {
                world.getEntities(this.type, predicate, entities, i);
            }
        }
    }

    private int getResultLimit() {
        return this.order == EntitySelector.ORDER_ARBITRARY ? this.maxResults : Integer.MAX_VALUE;
    }

    public WBUser findSinglePlayer(CommandSource source) throws CommandSyntaxException {
        this.checkPermissions(source);
        List<WBUser> list = this.findPlayers(source);

        if (list.size() != 1) {
            throw CommonEntityArgument.NO_PLAYERS_FOUND.create();
        } else {
            return list.getFirst();
        }
    }

    public List<WBUser> findPlayers(CommandSource source) throws CommandSyntaxException {
        this.checkPermissions(source);
        WBUser entityplayer;

        if (this.playerName != null) {
            entityplayer = source.woolbattle().user(this.playerName);
            return entityplayer == null ? List.of() : List.of(entityplayer);
        } else if (this.entityUUID != null) {
            entityplayer = source.woolbattle().user(this.entityUUID);
            return entityplayer == null ? List.of() : List.of(entityplayer);
        } else {
            Position pos = this.position.apply(source.pos());
            BoundingBox axisalignedbb = this.getAbsoluteAabb(pos);
            Predicate<Entity> predicate = this.getPredicate(pos, axisalignedbb);

            if (this.currentEntity) {
                Entity entity = source.entity();

                if (entity instanceof WBUser entityplayer1) {
                    if (predicate.test(entityplayer1)) {
                        return List.of(entityplayer1);
                    }
                }

                return List.of();
            } else {
                int i = this.getResultLimit();
                List<WBUser> object;

                if (this.isSameWorldOnly()) {
                    object = new ArrayList<>(source.world().getPlayers(predicate, i));
                } else {
                    object = new ObjectArrayList<>();

                    for (WBUser player : source.getPlayers()) {
                        if (predicate.test(player)) {
                            (object).add(player);
                            if ((object).size() >= i) {
                                return object;
                            }
                        }
                    }
                }

                return this.sortAndLimit(pos, object);
            }
        }
    }

    @Nullable
    private BoundingBox getAbsoluteAabb(Position offset) {
        return this.aabb != null ? this.aabb.move(offset) : null;
    }

    private Predicate<Entity> getPredicate(Position pos, @Nullable BoundingBox box) {
        boolean hasBox = box != null;
        boolean hasRange = !this.range.isAny();
        int i = (hasBox ? 1 : 0) + (hasRange ? 1 : 0);
        List<? extends Predicate<Entity>> object;

        if (i == 0) {
            object = this.contextFreePredicates;
        } else {
            List<Predicate<Entity>> list = new ObjectArrayList<>(this.contextFreePredicates.size() + i);

            list.addAll(this.contextFreePredicates);

            if (hasBox) {
                list.add((entity) -> box.intersects(entity.boundingBox()));
            }

            if (hasRange) {
                list.add((entity) -> this.range.matchesSqr(entity.distanceToSqr(pos)));
            }

            object = list;
        }

        return allOf(object);
    }

    public static <T> Predicate<T> allOf(List<? extends Predicate<T>> predicates) {
        return switch (predicates.size()) {
            case 0 -> object -> true;
            case 1 -> (Predicate) predicates.get(0);
            case 2 -> predicates.get(0).and((Predicate<? super T>) predicates.get(1));
            default -> {
                Predicate<T>[] predicates2 = predicates.toArray(Predicate[]::new);
                yield object -> {
                    for (Predicate<T> predicate : predicates2) {
                        if (!predicate.test((T) object)) {
                            return false;
                        }
                    }

                    return true;
                };
            }
        };
    }

    private <T extends Entity> List<T> sortAndLimit(Position pos, List<T> entities) {
        if (entities.size() > 1) {
            this.order.accept(pos, entities);
        }

        return entities.subList(0, Math.min(this.maxResults, entities.size()));
    }

    public static Component joinNames(List<? extends Entity> entities) {
        return ComponentUtils.formatList(entities, Entity::getDisplayName);
    }
}

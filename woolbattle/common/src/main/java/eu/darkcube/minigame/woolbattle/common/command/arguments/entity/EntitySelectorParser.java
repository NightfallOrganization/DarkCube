/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments.entity;

import static eu.darkcube.minigame.woolbattle.common.command.arguments.entity.TranslatableWrapper.translatable;
import static eu.darkcube.minigame.woolbattle.common.command.arguments.entity.TranslatableWrapper.translatableEscape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import eu.darkcube.minigame.woolbattle.api.command.CommandSource;
import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.api.util.BoundingBox;
import eu.darkcube.minigame.woolbattle.api.util.MinMaxBounds;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public class EntitySelectorParser {
    public static final char SYNTAX_SELECTOR_START = '@';
    private static final char SYNTAX_OPTIONS_START = '[';
    private static final char SYNTAX_OPTIONS_END = ']';
    public static final char SYNTAX_OPTIONS_KEY_VALUE_SEPARATOR = '=';
    private static final char SYNTAX_OPTIONS_SEPARATOR = ',';
    public static final char SYNTAX_NOT = '!';
    public static final char SYNTAX_TAG = '#';
    private static final char SELECTOR_NEAREST_PLAYER = 'p';
    private static final char SELECTOR_ALL_PLAYERS = 'a';
    private static final char SELECTOR_RANDOM_PLAYERS = 'r';
    private static final char SELECTOR_CURRENT_ENTITY = 's';
    private static final char SELECTOR_ALL_ENTITIES = 'e';
    private static final char SELECTOR_NEAREST_ENTITY = 'n';
    public static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID = new SimpleCommandExceptionType(translatable("argument.entity.invalid"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE = new DynamicCommandExceptionType((object) -> translatableEscape("argument.entity.selector.unknown", object));
    public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(translatable("argument.entity.selector.not_allowed"));
    public static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE = new SimpleCommandExceptionType(translatable("argument.entity.selector.missing"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS = new SimpleCommandExceptionType(translatable("argument.entity.options.unterminated"));
    public static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE = new DynamicCommandExceptionType((object) -> translatableEscape("argument.entity.options.valueless", object));
    public static final BiConsumer<Position, List<? extends Entity>> ORDER_NEAREST = (pos, list) -> list.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(pos)));
    public static final BiConsumer<Position, List<? extends Entity>> ORDER_FURTHEST = (pos, list) -> list.sort((entity, entity1) -> Double.compare(entity1.distanceToSqr(pos), entity.distanceToSqr(pos)));
    public static final BiConsumer<Position, List<? extends Entity>> ORDER_RANDOM = (_, list) -> Collections.shuffle(list);
    public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (suggestionsbuilder, _) -> suggestionsbuilder.buildFuture();
    private final StringReader reader;
    private final boolean allowSelectors;
    private int maxResults;
    private boolean includesEntities;
    private boolean worldLimited;
    private MinMaxBounds.Doubles distance;
    private MinMaxBounds.Ints level;
    @Nullable
    private Double x;
    @Nullable
    private Double y;
    @Nullable
    private Double z;
    @Nullable
    private Double deltaX;
    @Nullable
    private Double deltaY;
    @Nullable
    private Double deltaZ;
    private MinMaxBounds.Doubles rotX;
    private MinMaxBounds.Doubles rotY;
    private final List<Predicate<Entity>> predicates;
    private BiConsumer<Position, List<? extends Entity>> order;
    private boolean currentEntity;
    @Nullable
    private String playerName;
    private int startPosition;
    @Nullable
    private UUID entityUUID;
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions;
    private boolean hasNameEquals;
    private boolean hasNameNotEquals;
    private boolean isLimited;
    private boolean isSorted;
    private boolean hasGamemodeEquals;
    private boolean hasGamemodeNotEquals;
    private boolean hasTeamEquals;
    private boolean hasTeamNotEquals;
    @Nullable
    private EntityType<?> type;
    private boolean typeInverse;
    private boolean hasScores;
    private boolean hasAdvancements;
    private boolean usesSelectors;
    public boolean parsingEntityArgumentSuggestions; // Paper - tell clients to ask server for suggestions for EntityArguments

    public EntitySelectorParser(StringReader reader, boolean atAllowed) {
        // Paper start - tell clients to ask server for suggestions for EntityArguments
        this(reader, atAllowed, false);
    }

    public EntitySelectorParser(StringReader reader, boolean atAllowed, boolean parsingEntityArgumentSuggestions) {
        this.parsingEntityArgumentSuggestions = parsingEntityArgumentSuggestions;
        // Paper end - tell clients to ask server for suggestions for EntityArguments
        this.distance = MinMaxBounds.Doubles.ANY;
        this.level = MinMaxBounds.Ints.ANY;
        this.rotX = MinMaxBounds.Doubles.ANY;
        this.rotY = MinMaxBounds.Doubles.ANY;
        this.predicates = new ArrayList<>();
        this.order = EntitySelector.ORDER_ARBITRARY;
        this.suggestions = EntitySelectorParser.SUGGEST_NOTHING;
        this.reader = reader;
        this.allowSelectors = atAllowed;
    }

    public static <S> boolean allowSelectors(S source) {
        return source instanceof CommandSource stack && stack.hasPermission("minecraft.command.selector");
    }

    public EntitySelector getSelector() {
        BoundingBox axisalignedbb;

        if (this.deltaX == null && this.deltaY == null && this.deltaZ == null) {
            if (this.distance.max().isPresent()) {
                double d0 = (Double) this.distance.max().get();

                axisalignedbb = new BoundingBox(-d0, -d0, -d0, d0 + 1.0D, d0 + 1.0D, d0 + 1.0D);
            } else {
                axisalignedbb = null;
            }
        } else {
            axisalignedbb = this.createAabb(this.deltaX == null ? 0.0D : this.deltaX, this.deltaY == null ? 0.0D : this.deltaY, this.deltaZ == null ? 0.0D : this.deltaZ);
        }

        Function<Position, Position> function; // CraftBukkit - decompile error

        if (this.x == null && this.y == null && this.z == null) {
            function = (vec3d) -> vec3d;
        } else {
            function = (vec3d) -> new Position.Simple(this.x == null ? vec3d.x() : this.x, this.y == null ? vec3d.y() : this.y, this.z == null ? vec3d.z() : this.z);
        }

        return new EntitySelector(this.maxResults, this.includesEntities, this.worldLimited, List.copyOf(this.predicates), this.distance, function, axisalignedbb, this.order, this.currentEntity, this.playerName, this.entityUUID, this.type, this.usesSelectors);
    }

    private BoundingBox createAabb(double x, double y, double z) {
        boolean xstz = x < 0.0D;
        boolean ystz = y < 0.0D;
        boolean zstz = z < 0.0D;
        double minX = xstz ? x : 0.0D;
        double minY = ystz ? y : 0.0D;
        double minZ = zstz ? z : 0.0D;
        double maxX = (xstz ? 0.0D : x) + 1.0D;
        double maxY = (ystz ? 0.0D : y) + 1.0D;
        double maxZ = (zstz ? 0.0D : z) + 1.0D;

        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private void finalizePredicates() {
        if (this.rotX != MinMaxBounds.Doubles.ANY) {
            this.predicates.add(this.createRotationPredicate(this.rotX, e -> Optional.ofNullable(e.location()).map(Location::pitch).orElse(0F)));
        }

        if (this.rotY != MinMaxBounds.Doubles.ANY) {
            this.predicates.add(this.createRotationPredicate(this.rotY, e -> Optional.ofNullable(e.location()).map(Location::yaw).orElse(0F)));
        }

        if (!this.level.isAny()) {
            this.predicates.add((entity) -> entity instanceof CommonWBUser user && this.level.matches(user.platformAccess().xpLevel()));
        }

    }

    private Predicate<Entity> createRotationPredicate(MinMaxBounds.Doubles angleRange, ToDoubleFunction<Entity> entityToAngle) {
        double d0 = Mth.wrapDegrees(angleRange.min().isEmpty() ? 0.0F : angleRange.min().get());
        double d1 = Mth.wrapDegrees(angleRange.max().isEmpty() ? 359.0F : angleRange.max().get());

        return (entity) -> {
            double d2 = Mth.wrapDegrees(entityToAngle.applyAsDouble(entity));

            return d0 > d1 ? d2 >= d0 || d2 <= d1 : d2 >= d0 && d2 <= d1;
        };
    }

    // CraftBukkit start
    protected void parseSelector(boolean overridePermissions) throws CommandSyntaxException {
        this.usesSelectors = !overridePermissions;
        // CraftBukkit end
        this.suggestions = this::suggestSelector;
        if (!this.reader.canRead()) {
            throw EntitySelectorParser.ERROR_MISSING_SELECTOR_TYPE.createWithContext(this.reader);
        } else {
            int i = this.reader.getCursor();
            char c0 = this.reader.read();
            boolean aliveCheck;

            switch (c0) {
                case SELECTOR_ALL_PLAYERS:
                    this.maxResults = Integer.MAX_VALUE;
                    this.includesEntities = false;
                    this.order = EntitySelector.ORDER_ARBITRARY;
                    this.limitToType(EntityType.PLAYER);
                    aliveCheck = false;
                    break;
                case SELECTOR_ALL_ENTITIES:
                    this.maxResults = Integer.MAX_VALUE;
                    this.includesEntities = true;
                    this.order = EntitySelector.ORDER_ARBITRARY;
                    aliveCheck = true;
                    break;
                case SELECTOR_NEAREST_ENTITY:
                    this.maxResults = 1;
                    this.includesEntities = true;
                    this.order = EntitySelectorParser.ORDER_NEAREST;
                    aliveCheck = true;
                    break;
                case SELECTOR_NEAREST_PLAYER:
                    this.maxResults = 1;
                    this.includesEntities = false;
                    this.order = EntitySelectorParser.ORDER_NEAREST;
                    this.limitToType(EntityType.PLAYER);
                    aliveCheck = false;
                    break;
                case SELECTOR_RANDOM_PLAYERS:
                    this.maxResults = 1;
                    this.includesEntities = false;
                    this.order = EntitySelectorParser.ORDER_RANDOM;
                    this.limitToType(EntityType.PLAYER);
                    aliveCheck = false;
                    break;
                case SELECTOR_CURRENT_ENTITY:
                    this.maxResults = 1;
                    this.includesEntities = true;
                    this.currentEntity = true;
                    aliveCheck = false;
                    break;
                default:
                    this.reader.setCursor(i);
                    throw EntitySelectorParser.ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, "@" + c0);
            }

            if (aliveCheck) {
                this.predicates.add(Entity::isAlive);
            }

            this.suggestions = this::suggestOpenOptions;
            if (this.reader.canRead() && this.reader.peek() == SYNTAX_OPTIONS_START) {
                this.reader.skip();
                this.suggestions = this::suggestOptionsKeyOrClose;
                this.parseOptions();
            }

        }
    }

    protected void parseNameOrUUID() throws CommandSyntaxException {
        if (this.reader.canRead()) {
            this.suggestions = this::suggestName;
        }

        int i = this.reader.getCursor();
        String s = this.reader.readString();

        try {
            this.entityUUID = UUID.fromString(s);
            this.includesEntities = true;
        } catch (IllegalArgumentException illegalargumentexception) {
            if (s.isEmpty() || s.length() > 16) {
                this.reader.setCursor(i);
                throw EntitySelectorParser.ERROR_INVALID_NAME_OR_UUID.createWithContext(this.reader);
            }

            this.includesEntities = false;
            this.playerName = s;
        }

        this.maxResults = 1;
    }

    protected void parseOptions() throws CommandSyntaxException {
        this.suggestions = this::suggestOptionsKey;
        this.reader.skipWhitespace();

        while (true) {
            if (this.reader.canRead() && this.reader.peek() != SYNTAX_OPTIONS_END) {
                this.reader.skipWhitespace();
                int i = this.reader.getCursor();
                String s = this.reader.readString();
                EntitySelectorOptions.Modifier playerselector_a = EntitySelectorOptions.get(this, s, i);

                this.reader.skipWhitespace();
                if (!this.reader.canRead() || this.reader.peek() != SYNTAX_OPTIONS_KEY_VALUE_SEPARATOR) {
                    this.reader.setCursor(i);
                    throw EntitySelectorParser.ERROR_EXPECTED_OPTION_VALUE.createWithContext(this.reader, s);
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestions = EntitySelectorParser.SUGGEST_NOTHING;
                playerselector_a.handle(this);
                this.reader.skipWhitespace();
                this.suggestions = this::suggestOptionsNextOrClose;
                if (!this.reader.canRead()) {
                    continue;
                }

                if (this.reader.peek() == SYNTAX_OPTIONS_SEPARATOR) {
                    this.reader.skip();
                    this.suggestions = this::suggestOptionsKey;
                    continue;
                }

                if (this.reader.peek() != SYNTAX_OPTIONS_END) {
                    throw EntitySelectorParser.ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
                }
            }

            if (this.reader.canRead()) {
                this.reader.skip();
                this.suggestions = EntitySelectorParser.SUGGEST_NOTHING;
                return;
            }

            throw EntitySelectorParser.ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
        }
    }

    public boolean shouldInvertValue() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == SYNTAX_NOT) {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }

    public boolean isTag() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == SYNTAX_TAG) {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }

    public StringReader getReader() {
        return this.reader;
    }

    public void addPredicate(Predicate<Entity> predicate) {
        this.predicates.add(predicate);
    }

    public void setWorldLimited() {
        this.worldLimited = true;
    }

    public MinMaxBounds.Doubles getDistance() {
        return this.distance;
    }

    public void setDistance(MinMaxBounds.Doubles distance) {
        this.distance = distance;
    }

    public MinMaxBounds.Ints getLevel() {
        return this.level;
    }

    public void setLevel(MinMaxBounds.Ints levelRange) {
        this.level = levelRange;
    }

    public MinMaxBounds.Doubles getRotX() {
        return this.rotX;
    }

    public void setRotX(MinMaxBounds.Doubles pitchRange) {
        this.rotX = pitchRange;
    }

    public MinMaxBounds.Doubles getRotY() {
        return this.rotY;
    }

    public void setRotY(MinMaxBounds.Doubles yawRange) {
        this.rotY = yawRange;
    }

    @Nullable
    public Double getX() {
        return this.x;
    }

    @Nullable
    public Double getY() {
        return this.y;
    }

    @Nullable
    public Double getZ() {
        return this.z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setDeltaX(double dx) {
        this.deltaX = dx;
    }

    public void setDeltaY(double dy) {
        this.deltaY = dy;
    }

    public void setDeltaZ(double dz) {
        this.deltaZ = dz;
    }

    @Nullable
    public Double getDeltaX() {
        return this.deltaX;
    }

    @Nullable
    public Double getDeltaY() {
        return this.deltaY;
    }

    @Nullable
    public Double getDeltaZ() {
        return this.deltaZ;
    }

    public void setMaxResults(int limit) {
        this.maxResults = limit;
    }

    public void setIncludesEntities(boolean includesNonPlayers) {
        this.includesEntities = includesNonPlayers;
    }

    public BiConsumer<Position, List<? extends Entity>> getOrder() {
        return this.order;
    }

    public void setOrder(BiConsumer<Position, List<? extends Entity>> sorter) {
        this.order = sorter;
    }

    public EntitySelector parse() throws CommandSyntaxException {
        // CraftBukkit start
        return this.parse(false);
    }

    public EntitySelector parse(boolean overridePermissions) throws CommandSyntaxException {
        // CraftBukkit end
        this.startPosition = this.reader.getCursor();
        this.suggestions = this::suggestNameOrSelector;
        if (this.reader.canRead() && this.reader.peek() == SYNTAX_SELECTOR_START) {
            if (!this.allowSelectors) {
                throw EntitySelectorParser.ERROR_SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
            }

            this.reader.skip();
            this.parseSelector(overridePermissions); // CraftBukkit
        } else {
            this.parseNameOrUUID();
        }

        this.finalizePredicates();
        return this.getSelector();
    }

    private static void fillSelectorSuggestions(SuggestionsBuilder builder) {
        builder.suggest("@p", translatable("argument.entity.selector.nearestPlayer"));
        builder.suggest("@a", translatable("argument.entity.selector.allPlayers"));
        builder.suggest("@r", translatable("argument.entity.selector.randomPlayer"));
        builder.suggest("@s", translatable("argument.entity.selector.self"));
        builder.suggest("@e", translatable("argument.entity.selector.allEntities"));
        builder.suggest("@n", translatable("argument.entity.selector.nearestEntity"));
    }

    private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        consumer.accept(builder);
        if (this.allowSelectors) {
            EntitySelectorParser.fillSelectorSuggestions(builder);
        }

        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        SuggestionsBuilder suggestionsbuilder1 = builder.createOffset(this.startPosition);

        consumer.accept(suggestionsbuilder1);
        return builder.add(suggestionsbuilder1).buildFuture();
    }

    private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        SuggestionsBuilder suggestionsbuilder1 = builder.createOffset(builder.getStart() - 1);

        EntitySelectorParser.fillSelectorSuggestions(suggestionsbuilder1);
        builder.add(suggestionsbuilder1);
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        builder.suggest(String.valueOf(SYNTAX_OPTIONS_START));
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        builder.suggest(String.valueOf(SYNTAX_OPTIONS_END));
        EntitySelectorOptions.suggestNames(this, builder);
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsKey(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        EntitySelectorOptions.suggestNames(this, builder);
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOptionsNextOrClose(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        builder.suggest(String.valueOf(SYNTAX_OPTIONS_SEPARATOR));
        builder.suggest(String.valueOf(SYNTAX_OPTIONS_END));
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        builder.suggest(String.valueOf(SYNTAX_OPTIONS_KEY_VALUE_SEPARATOR));
        return builder.buildFuture();
    }

    public boolean isCurrentEntity() {
        return this.currentEntity;
    }

    public void setSuggestions(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionProvider) {
        this.suggestions = suggestionProvider;
    }

    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> consumer) {
        return (CompletableFuture) this.suggestions.apply(builder.createOffset(this.reader.getCursor()), consumer);
    }

    public boolean hasNameEquals() {
        return this.hasNameEquals;
    }

    public void setHasNameEquals(boolean selectsName) {
        this.hasNameEquals = selectsName;
    }

    public boolean hasNameNotEquals() {
        return this.hasNameNotEquals;
    }

    public void setHasNameNotEquals(boolean excludesName) {
        this.hasNameNotEquals = excludesName;
    }

    public boolean isLimited() {
        return this.isLimited;
    }

    public void setLimited(boolean hasLimit) {
        this.isLimited = hasLimit;
    }

    public boolean isSorted() {
        return this.isSorted;
    }

    public void setSorted(boolean hasSorter) {
        this.isSorted = hasSorter;
    }

    public boolean hasGamemodeEquals() {
        return this.hasGamemodeEquals;
    }

    public void setHasGamemodeEquals(boolean selectsGameMode) {
        this.hasGamemodeEquals = selectsGameMode;
    }

    public boolean hasGamemodeNotEquals() {
        return this.hasGamemodeNotEquals;
    }

    public void setHasGamemodeNotEquals(boolean excludesGameMode) {
        this.hasGamemodeNotEquals = excludesGameMode;
    }

    public boolean hasTeamEquals() {
        return this.hasTeamEquals;
    }

    public void setHasTeamEquals(boolean selectsTeam) {
        this.hasTeamEquals = selectsTeam;
    }

    public boolean hasTeamNotEquals() {
        return this.hasTeamNotEquals;
    }

    public void setHasTeamNotEquals(boolean excludesTeam) {
        this.hasTeamNotEquals = excludesTeam;
    }

    public void limitToType(EntityType<?> entityType) {
        this.type = entityType;
    }

    public void setTypeLimitedInversely() {
        this.typeInverse = true;
    }

    public boolean isTypeLimited() {
        return this.type != null;
    }

    public boolean isTypeLimitedInversely() {
        return this.typeInverse;
    }

    public boolean hasScores() {
        return this.hasScores;
    }

    public void setHasScores(boolean selectsScores) {
        this.hasScores = selectsScores;
    }

    public boolean hasAdvancements() {
        return this.hasAdvancements;
    }

    public void setHasAdvancements(boolean selectsAdvancements) {
        this.hasAdvancements = selectsAdvancements;
    }
}

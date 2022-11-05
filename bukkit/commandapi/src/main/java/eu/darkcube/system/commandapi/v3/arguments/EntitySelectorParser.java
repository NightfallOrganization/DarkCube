package eu.darkcube.system.commandapi.v3.arguments;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.google.common.primitives.Doubles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import eu.darkcube.system.commandapi.v3.BoundingBox;
import eu.darkcube.system.commandapi.v3.MathHelper;
import eu.darkcube.system.commandapi.v3.Message;
import eu.darkcube.system.commandapi.v3.MinMaxBounds;
import eu.darkcube.system.commandapi.v3.MinMaxBoundsWrapped;
import eu.darkcube.system.commandapi.v3.Vector3d;

public class EntitySelectorParser {

	public static final DynamicCommandExceptionType UNKNOWN_SELECTOR_TYPE = Message.UNKNOWN_COMMAND_EXCEPTION_TYPE
			.newDynamicCommandExceptionType();
	public static final SimpleCommandExceptionType INVALID_ENTITY_NAME_OR_UUID = Message.INVALID_ENTITY_NAME_OR_UUID
			.newSimpleCommandExceptionType();
	public static final SimpleCommandExceptionType SELECTOR_NOT_ALLOWED = Message.SELECTOR_NOT_ALLOWED
			.newSimpleCommandExceptionType();
	public static final SimpleCommandExceptionType SELECTOR_TYPE_MISSING = Message.SELECTOR_TYPE_MISSING
			.newSimpleCommandExceptionType();
	public static final SimpleCommandExceptionType EXPECTED_END_OF_OPTIONS = Message.EXPECTED_END_OF_OPTIONS
			.newSimpleCommandExceptionType();
	public static final DynamicCommandExceptionType EXPECTED_VALUE_FOR_OPTION = Message.EXPECTED_VALUE_FOR_OPTION
			.newDynamicCommandExceptionType();
	public static final BiConsumer<Vector3d, List<? extends Entity>> ARBITRARY = (vec, entities) -> {
	};
	public static final BiConsumer<Vector3d, List<? extends Entity>> NEAREST = (vec, entities) -> {
		entities.sort((e1, e2) -> {
			return Doubles.compare(Vector3d.position(e1.getLocation()).squareDistanceTo(vec),
					Vector3d.position(e2.getLocation()).squareDistanceTo(vec));
		});
	};
	public static final BiConsumer<Vector3d, List<? extends Entity>> FURTHEST = (vec, entities) -> {
		entities.sort((e1, e2) -> {
			return Doubles.compare(Vector3d.position(e1.getLocation()).squareDistanceTo(vec),
					Vector3d.position(e2.getLocation()).squareDistanceTo(vec));
		});
	};
	public static final BiConsumer<Vector3d, List<? extends Entity>> RANDOM = (vec, entities) -> {
		Collections.shuffle(entities);
	};
	public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_NONE = (
			p_201342_0_, p_201342_1_) -> {
		return p_201342_0_.buildFuture();
	};

	private final StringReader reader;
	private final boolean hasPermission;
	private int limit;
	private boolean includeNonPlayers;
	private boolean currentWorldOnly;
	private MinMaxBounds.FloatBound distance = MinMaxBounds.FloatBound.UNBOUNDED;
	private MinMaxBounds.IntBound level = MinMaxBounds.IntBound.UNBOUNDED;
	private Double x, y, z, dx, dy, dz;
	private MinMaxBoundsWrapped xRotation = MinMaxBoundsWrapped.UNBOUNDED;
	private MinMaxBoundsWrapped yRotation = MinMaxBoundsWrapped.UNBOUNDED;
	private Predicate<Entity> filter = entity -> true;
	private BiConsumer<Vector3d, List<? extends Entity>> sorter = EntitySelectorParser.ARBITRARY;
	private boolean self;
	private String username;
	private int cursorStart;
	private UUID uuid;
	private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionHandler = EntitySelectorParser.SUGGEST_NONE;
	private boolean hasNameEquals;
	private boolean hasNameNotEquals;
	private boolean isLimited;
	private boolean isSorted;
	private boolean hasGamemodeEquals;
	private boolean hasGamemodeNotEquals;
	private boolean hasTeamEquals;
	private EntityType type;
	private boolean typeInverse;
	private boolean hasScores;
	private boolean hasAdvancements;

	public EntitySelectorParser(StringReader readerIn) {
		this(readerIn, true);
	}

	public EntitySelectorParser(StringReader readerIn, boolean hasPermissionIn) {
		this.reader = readerIn;
		this.hasPermission = hasPermissionIn;
	}

	public EntitySelector build() {
		BoundingBox bb;
		if (this.dx == null && this.dy == null && this.dz == null) {
			if (this.distance.getMax() != null) {
				float f = this.distance.getMax();
				bb = new BoundingBox(-f, -f, -f, f + 1, f + 1, f + 1);
			} else {
				bb = null;
			}
		} else {
			bb = this.createAABB(this.dx == null ? 0.0D : this.dx, this.dy == null ? 0.0D : this.dy,
					this.dz == null ? 0.0D : this.dz);
		}
		Function<Vector3d, Vector3d> function;
		if (this.x == null && this.y == null && this.z == null) {
			function = vec -> vec;
		} else {
			function = (vec) -> new Vector3d(this.x == null ? vec.x : this.x, this.y == null ? vec.y : this.y,
					this.z == null ? vec.z : this.z);
		}
		return new EntitySelector(this.limit, this.includeNonPlayers, this.currentWorldOnly, this.filter, this.distance,
				function, bb, this.sorter, this.self, this.username, this.uuid, this.type);
	}

	private BoundingBox createAABB(double sizeX, double sizeY, double sizeZ) {
		boolean flag = sizeX < 0.0D;
		boolean flag1 = sizeY < 0.0D;
		boolean flag2 = sizeZ < 0.0D;
		double d0 = flag ? sizeX : 0.0D;
		double d1 = flag1 ? sizeY : 0.0D;
		double d2 = flag2 ? sizeZ : 0.0D;
		double d3 = (flag ? 0.0D : sizeX) + 1.0D;
		double d4 = (flag1 ? 0.0D : sizeY) + 1.0D;
		double d5 = (flag2 ? 0.0D : sizeZ) + 1.0D;
		return new BoundingBox(d0, d1, d2, d3, d4, d5);
	}

	private void updateFilter() {
		if (this.xRotation != MinMaxBoundsWrapped.UNBOUNDED) {
			this.filter = this.filter.and(this.createRotationPredicate(this.xRotation, (entity) -> {
				return (double) entity.getLocation().getYaw();
			}));
		}

		if (this.yRotation != MinMaxBoundsWrapped.UNBOUNDED) {
			this.filter = this.filter.and(this.createRotationPredicate(this.yRotation, (entity) -> {
				return (double) entity.getLocation().getPitch();
			}));
		}

		if (!this.level.isUnbounded()) {
			this.filter = this.filter.and((entity) -> {
				return !(entity instanceof Player) ? false : this.level.test(((Player) entity).getLevel());
			});
		}

	}

	private Predicate<Entity> createRotationPredicate(MinMaxBoundsWrapped angleBounds,
			ToDoubleFunction<Entity> angleFunc) {
		double d0 = MathHelper.wrapDegrees(angleBounds.getMin() == null ? 0.0F : angleBounds.getMin());
		double d1 = MathHelper.wrapDegrees(angleBounds.getMax() == null ? 359.0F : angleBounds.getMax());
		return (p_197374_5_) -> {
			double d2 = MathHelper.wrapDegrees(angleFunc.applyAsDouble(p_197374_5_));
			if (d0 > d1) {
				return d2 >= d0 || d2 <= d1;
			}
			return d2 >= d0 && d2 <= d1;
		};
	}

	protected void parseSelector() throws CommandSyntaxException {
		this.suggestionHandler = this::suggestSelector;
		if (!this.reader.canRead()) {
			throw EntitySelectorParser.SELECTOR_TYPE_MISSING.createWithContext(this.reader);
		}
		int i = this.reader.getCursor();
		char c0 = this.reader.read();
		if (c0 == 'p') {
			this.limit = 1;
			this.includeNonPlayers = false;
			this.sorter = EntitySelectorParser.NEAREST;
			this.setEntityType(EntityType.PLAYER);
		} else if (c0 == 'a') {
			this.limit = Integer.MAX_VALUE;
			this.includeNonPlayers = false;
			this.sorter = EntitySelectorParser.ARBITRARY;
			this.setEntityType(EntityType.PLAYER);
		} else if (c0 == 'r') {
			this.limit = 1;
			this.includeNonPlayers = false;
			this.sorter = EntitySelectorParser.RANDOM;
			this.setEntityType(EntityType.PLAYER);
		} else if (c0 == 's') {
			this.limit = 1;
			this.includeNonPlayers = true;
			this.self = true;
		} else {
			if (c0 != 'e') {
				this.reader.setCursor(i);
				throw EntitySelectorParser.UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, '@' + String.valueOf(c0));
			}

			this.limit = Integer.MAX_VALUE;
			this.includeNonPlayers = true;
			this.sorter = EntitySelectorParser.ARBITRARY;
			this.filter = e -> !e.isDead() && e.isValid();
		}

		this.suggestionHandler = this::suggestOpenBracket;
		if (this.reader.canRead() && this.reader.peek() == '[') {
			this.reader.skip();
			this.suggestionHandler = this::suggestOptionsOrEnd;
			this.parseArguments();
		}
	}

	protected void parseSingleEntity() throws CommandSyntaxException {
		if (this.reader.canRead()) {
			this.suggestionHandler = this::suggestName;
		}

		int i = this.reader.getCursor();
		String s = this.reader.readString();

		try {
			this.uuid = UUID.fromString(s);
			this.includeNonPlayers = true;
		} catch (IllegalArgumentException illegalargumentexception) {
			if (s.isEmpty() || s.length() > 16) {
				this.reader.setCursor(i);
				throw EntitySelectorParser.INVALID_ENTITY_NAME_OR_UUID.createWithContext(this.reader);
			}

			this.includeNonPlayers = false;
			this.username = s;
		}

		this.limit = 1;
	}

	protected void parseArguments() throws CommandSyntaxException {
		this.suggestionHandler = this::suggestOptions;
		this.reader.skipWhitespace();

		while (true) {
			if (this.reader.canRead() && this.reader.peek() != ']') {
				this.reader.skipWhitespace();
				int i = this.reader.getCursor();
				String s = this.reader.readString();
				EntityOptions.IFilter entityoptions$ifilter = EntityOptions.get(this, s, i);
				this.reader.skipWhitespace();
				if (!this.reader.canRead() || this.reader.peek() != '=') {
					this.reader.setCursor(i);
					throw EntitySelectorParser.EXPECTED_VALUE_FOR_OPTION.createWithContext(this.reader, s);
				}

				this.reader.skip();
				this.reader.skipWhitespace();
				this.suggestionHandler = EntitySelectorParser.SUGGEST_NONE;
				entityoptions$ifilter.handle(this);
				this.reader.skipWhitespace();
				this.suggestionHandler = this::suggestCommaOrEnd;
				if (!this.reader.canRead()) {
					continue;
				}

				if (this.reader.peek() == ',') {
					this.reader.skip();
					this.suggestionHandler = this::suggestOptions;
					continue;
				}

				if (this.reader.peek() != ']') {
					throw EntitySelectorParser.EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
				}
			}

			if (this.reader.canRead()) {
				this.reader.skip();
				this.suggestionHandler = EntitySelectorParser.SUGGEST_NONE;
				return;
			}

			throw EntitySelectorParser.EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
		}
	}

	public boolean shouldInvertValue() {
		this.reader.skipWhitespace();
		if (this.reader.canRead() && this.reader.peek() == '!') {
			this.reader.skip();
			this.reader.skipWhitespace();
			return true;
		}
		return false;
	}

	public boolean isOfType() {
		this.reader.skipWhitespace();
		if (this.reader.canRead() && this.reader.peek() == '#') {
			this.reader.skip();
			this.reader.skipWhitespace();
			return true;
		}
		return false;
	}

	public StringReader getReader() {
		return this.reader;
	}

	public void addFilter(Predicate<Entity> filterIn) {
		this.filter = this.filter.and(filterIn);
	}

	public void setCurrentWorldOnly() {
		this.currentWorldOnly = true;
	}

	public MinMaxBounds.FloatBound getDistance() {
		return this.distance;
	}

	public void setDistance(MinMaxBounds.FloatBound distanceIn) {
		this.distance = distanceIn;
	}

	public MinMaxBounds.IntBound getLevel() {
		return this.level;
	}

	public void setLevel(MinMaxBounds.IntBound levelIn) {
		this.level = levelIn;
	}

	public MinMaxBoundsWrapped getXRotation() {
		return this.xRotation;
	}

	public void setXRotation(MinMaxBoundsWrapped xRotationIn) {
		this.xRotation = xRotationIn;
	}

	public MinMaxBoundsWrapped getYRotation() {
		return this.yRotation;
	}

	public void setYRotation(MinMaxBoundsWrapped yRotationIn) {
		this.yRotation = yRotationIn;
	}

	public Double getX() {
		return this.x;
	}

	public Double getY() {
		return this.y;
	}

	public Double getZ() {
		return this.z;
	}

	public void setX(double xIn) {
		this.x = xIn;
	}

	public void setY(double yIn) {
		this.y = yIn;
	}

	public void setZ(double zIn) {
		this.z = zIn;
	}

	public void setDx(double dxIn) {
		this.dx = dxIn;
	}

	public void setDy(double dyIn) {
		this.dy = dyIn;
	}

	public void setDz(double dzIn) {
		this.dz = dzIn;
	}

	public Double getDx() {
		return this.dx;
	}

	public Double getDy() {
		return this.dy;
	}

	public Double getDz() {
		return this.dz;
	}

	public void setLimit(int limitIn) {
		this.limit = limitIn;
	}

	public void setIncludeNonPlayers(boolean includeNonPlayersIn) {
		this.includeNonPlayers = includeNonPlayersIn;
	}

	public void setSorter(BiConsumer<Vector3d, List<? extends Entity>> sorterIn) {
		this.sorter = sorterIn;
	}

	public EntitySelector parse() throws CommandSyntaxException {
		this.cursorStart = this.reader.getCursor();
		this.suggestionHandler = this::suggestNameOrSelector;
		if (this.reader.canRead() && this.reader.peek() == '@') {
			if (!this.hasPermission) {
				throw EntitySelectorParser.SELECTOR_NOT_ALLOWED.createWithContext(this.reader);
			}

			this.reader.skip();
			this.parseSelector();
		} else {
			this.parseSingleEntity();
		}

		this.updateFilter();
		return this.build();
	}

	private static void fillSelectorSuggestions(SuggestionsBuilder suggestionBuilder) {
		suggestionBuilder.suggest("@p", Message.SELECTOR_NEAREST_PLAYER.newSimpleWrapper());
		suggestionBuilder.suggest("@a", Message.SELECTOR_ALL_PLAYERS.newSimpleWrapper());
		suggestionBuilder.suggest("@r", Message.SELECTOR_RANDOM_PLAYER.newSimpleWrapper());
		suggestionBuilder.suggest("@s", Message.SELECTOR_SELF.newSimpleWrapper());
		suggestionBuilder.suggest("@e", Message.SELECTOR_ALL_ENTITIES.newSimpleWrapper());
	}

	private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder suggestionBuilder,
			Consumer<SuggestionsBuilder> consumer) {
		consumer.accept(suggestionBuilder);
		if (this.hasPermission) {
			EntitySelectorParser.fillSelectorSuggestions(suggestionBuilder);
		}

		return suggestionBuilder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestName(SuggestionsBuilder builder,
			Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsbuilder = builder.createOffset(this.cursorStart);
		consumer.accept(suggestionsbuilder);
		return builder.add(suggestionsbuilder).buildFuture();
	}

	private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder builder,
			Consumer<SuggestionsBuilder> consumer) {
		SuggestionsBuilder suggestionsbuilder = builder.createOffset(builder.getStart() - 1);
		EntitySelectorParser.fillSelectorSuggestions(suggestionsbuilder);
		builder.add(suggestionsbuilder);
		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestOpenBracket(SuggestionsBuilder builder,
			Consumer<SuggestionsBuilder> consumer) {
		builder.suggest(String.valueOf('['));
		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestOptionsOrEnd(SuggestionsBuilder builder,
			Consumer<SuggestionsBuilder> consumer) {
		builder.suggest(String.valueOf(']'));
		EntityOptions.suggestOptions(this, builder);
		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestOptions(SuggestionsBuilder builder,
			Consumer<SuggestionsBuilder> consumer) {
		EntityOptions.suggestOptions(this, builder);
		return builder.buildFuture();
	}

	private CompletableFuture<Suggestions> suggestCommaOrEnd(SuggestionsBuilder builder,
			Consumer<SuggestionsBuilder> consumer) {
		builder.suggest(String.valueOf(','));
		builder.suggest(String.valueOf(']'));
		return builder.buildFuture();
	}

	public boolean isCurrentEntity() {
		return this.self;
	}

	public void setSuggestionHandler(
			BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestionHandlerIn) {
		this.suggestionHandler = suggestionHandlerIn;
	}

	public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder,
			Consumer<SuggestionsBuilder> consumer) {
		return this.suggestionHandler.apply(builder.createOffset(this.reader.getCursor()), consumer);
	}

	public boolean hasNameEquals() {
		return this.hasNameEquals;
	}

	public void setHasNameEquals(boolean value) {
		this.hasNameEquals = value;
	}

	public boolean hasNameNotEquals() {
		return this.hasNameNotEquals;
	}

	public void setHasNameNotEquals(boolean value) {
		this.hasNameNotEquals = value;
	}

	public boolean isLimited() {
		return this.isLimited;
	}

	public void setLimited(boolean value) {
		this.isLimited = value;
	}

	public boolean isSorted() {
		return this.isSorted;
	}

	public void setSorted(boolean value) {
		this.isSorted = value;
	}

	public boolean hasGamemodeEquals() {
		return this.hasGamemodeEquals;
	}

	public void setHasGamemodeEquals(boolean value) {
		this.hasGamemodeEquals = value;
	}

	public boolean hasGamemodeNotEquals() {
		return this.hasGamemodeNotEquals;
	}

	public void setHasGamemodeNotEquals(boolean value) {
		this.hasGamemodeNotEquals = value;
	}

	public boolean hasTeamEquals() {
		return this.hasTeamEquals;
	}

	public void setHasTeamEquals(boolean value) {
		this.hasTeamEquals = value;
	}

	public void setHasTeamNotEquals(boolean value) {
	}

	public void setEntityType(EntityType type) {
		this.type = type;
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

	public void setHasScores(boolean value) {
		this.hasScores = value;
	}

	public boolean hasAdvancements() {
		return this.hasAdvancements;
	}

	public void setHasAdvancements(boolean value) {
		this.hasAdvancements = value;
	}

}

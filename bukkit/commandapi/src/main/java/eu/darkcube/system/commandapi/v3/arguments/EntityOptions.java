/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3.arguments;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.MathHelper;
import eu.darkcube.system.commandapi.v3.Message;
import eu.darkcube.system.commandapi.v3.MinMaxBounds;
import eu.darkcube.system.commandapi.v3.MinMaxBoundsWrapped;
import eu.darkcube.system.commandapi.v3.Vector3d;

public class EntityOptions {

	private static final Map<String, EntityOptions.OptionHandler> REGISTRY = Maps.newHashMap();

	public static final DynamicCommandExceptionType UNKNOWN_ENTITY_OPTION = Message.UNKNOWN_ENTITY_OPTION
			.newDynamicCommandExceptionType();

	public static final DynamicCommandExceptionType INAPPLICABLE_ENTITY_OPTION = Message.INAPPLICABLE_ENTITY_OPTION
			.newDynamicCommandExceptionType();

	public static final SimpleCommandExceptionType NEGATIVE_DISTANCE = Message.NEGATIVE_DISTANCE
			.newSimpleCommandExceptionType();

	public static final SimpleCommandExceptionType NEGATIVE_LEVEL = Message.NEGATIVE_LEVEL
			.newSimpleCommandExceptionType();

	public static final SimpleCommandExceptionType NONPOSITIVE_LIMIT = Message.NONPOSITIVE_LIMIT
			.newSimpleCommandExceptionType();

	public static final DynamicCommandExceptionType INVALID_SORT = Message.INVALID_SORT
			.newDynamicCommandExceptionType();

	public static final DynamicCommandExceptionType INVALID_GAME_MODE = Message.INVALID_GAME_MODE
			.newDynamicCommandExceptionType();

	public static final DynamicCommandExceptionType INVALID_ENTITY_TYPE = Message.INVALID_ENTITY_TYPE
			.newDynamicCommandExceptionType();

	public static void register(String id, EntityOptions.IFilter handler, Predicate<EntitySelectorParser> filter,
			com.mojang.brigadier.Message tooltip) {
		EntityOptions.REGISTRY.put(id, new EntityOptions.OptionHandler(handler, filter, tooltip));
	}

	public static void registerOptions() {
		if (EntityOptions.REGISTRY.isEmpty()) {
			EntityOptions.register("name", (parser) -> {
				int i = parser.getReader().getCursor();
				boolean flag = parser.shouldInvertValue();
				String s = parser.getReader().readString();
				if (parser.hasNameNotEquals() && !flag) {
					parser.getReader().setCursor(i);
					throw EntityOptions.INAPPLICABLE_ENTITY_OPTION.createWithContext(parser.getReader(), "name");
				}
				if (flag) {
					parser.setHasNameNotEquals(true);
				} else {
					parser.setHasNameEquals(true);
				}

				parser.addFilter((entity) -> {
					return (entity.getCustomName() == null ? entity.getName() : entity.getCustomName())
							.equals(s) != flag;
				});
			}, (parser) -> {
				return !parser.hasNameEquals();
			}, null);
			EntityOptions.register("distance", (parser) -> {
				int i = parser.getReader().getCursor();
				MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound
						.fromReader(parser.getReader());
				if ((minmaxbounds$floatbound.getMin() == null || !(minmaxbounds$floatbound.getMin() < 0.0F))
						&& (minmaxbounds$floatbound.getMax() == null || !(minmaxbounds$floatbound.getMax() < 0.0F))) {
					parser.setDistance(minmaxbounds$floatbound);
					parser.setCurrentWorldOnly();
				} else {
					parser.getReader().setCursor(i);
					throw EntityOptions.NEGATIVE_DISTANCE.createWithContext(parser.getReader());
				}
			}, (parser) -> {
				return parser.getDistance().isUnbounded();
			}, null);
			EntityOptions.register("level", (parser) -> {
				int i = parser.getReader().getCursor();
				MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromReader(parser.getReader());
				if ((minmaxbounds$intbound.getMin() == null || minmaxbounds$intbound.getMin() >= 0)
						&& (minmaxbounds$intbound.getMax() == null || minmaxbounds$intbound.getMax() >= 0)) {
					parser.setLevel(minmaxbounds$intbound);
					parser.setIncludeNonPlayers(false);
				} else {
					parser.getReader().setCursor(i);
					throw EntityOptions.NEGATIVE_LEVEL.createWithContext(parser.getReader());
				}
			}, (parser) -> {
				return parser.getLevel().isUnbounded();
			}, null);
			EntityOptions.register("x", (parser) -> {
				parser.setCurrentWorldOnly();
				parser.setX(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getX() == null;
			}, null);
			EntityOptions.register("y", (parser) -> {
				parser.setCurrentWorldOnly();
				parser.setY(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getY() == null;
			}, null);
			EntityOptions.register("z", (parser) -> {
				parser.setCurrentWorldOnly();
				parser.setZ(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getZ() == null;
			}, null);
			EntityOptions.register("dx", (parser) -> {
				parser.setCurrentWorldOnly();
				parser.setDx(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getDx() == null;
			}, null);
			EntityOptions.register("dy", (parser) -> {
				parser.setCurrentWorldOnly();
				parser.setDy(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getDy() == null;
			}, null);
			EntityOptions.register("dz", (parser) -> {
				parser.setCurrentWorldOnly();
				parser.setDz(parser.getReader().readDouble());
			}, (parser) -> {
				return parser.getDz() == null;
			}, null);
			EntityOptions.register("x_rotation", (parser) -> {
				parser.setXRotation(MinMaxBoundsWrapped.fromReader(parser.getReader(), true, MathHelper::wrapDegrees));
			}, (parser) -> {
				return parser.getXRotation() == MinMaxBoundsWrapped.UNBOUNDED;
			}, null);
			EntityOptions.register("y_rotation", (parser) -> {
				parser.setYRotation(MinMaxBoundsWrapped.fromReader(parser.getReader(), true, MathHelper::wrapDegrees));
			}, (parser) -> {
				return parser.getYRotation() == MinMaxBoundsWrapped.UNBOUNDED;
			}, null);
			EntityOptions.register("limit", (parser) -> {
				int i = parser.getReader().getCursor();
				int j = parser.getReader().readInt();
				if (j < 1) {
					parser.getReader().setCursor(i);
					throw EntityOptions.NONPOSITIVE_LIMIT.createWithContext(parser.getReader());
				}
				parser.setLimit(j);
				parser.setLimited(true);
			}, (parser) -> {
				return !parser.isCurrentEntity() && !parser.isLimited();
			}, null);
			EntityOptions.register("sort", (parser) -> {
				int i = parser.getReader().getCursor();
				String s = parser.getReader().readUnquotedString();
				parser.setSuggestionHandler((builder, p_202056_1_) -> {
					return ISuggestionProvider.suggest(Arrays.asList("nearest", "furthest", "random", "arbitrary"),
							builder);
				});
				BiConsumer<Vector3d, List<? extends Entity>> biconsumer;
				switch (s) {
				case "nearest":
					biconsumer = EntitySelectorParser.NEAREST;
					break;
				case "furthest":
					biconsumer = EntitySelectorParser.FURTHEST;
					break;
				case "random":
					biconsumer = EntitySelectorParser.RANDOM;
					break;
				case "arbitrary":
					biconsumer = EntitySelectorParser.ARBITRARY;
					break;
				default:
					parser.getReader().setCursor(i);
					throw EntityOptions.INVALID_SORT.createWithContext(parser.getReader(), s);
				}

				parser.setSorter(biconsumer);
				parser.setSorted(true);
			}, (parser) -> {
				return !parser.isCurrentEntity() && !parser.isSorted();
			}, null);
			EntityOptions.register("gamemode", (parser) -> {
				parser.setSuggestionHandler((suggestionBuilder, p_202018_2_) -> {
					String s1 = suggestionBuilder.getRemaining().toLowerCase(Locale.ROOT);
					boolean flag1 = !parser.hasGamemodeNotEquals();
					boolean flag2 = true;
					if (!s1.isEmpty()) {
						if (s1.charAt(0) == '!') {
							flag1 = false;
							s1 = s1.substring(1);
						} else {
							flag2 = false;
						}
					}

					for (GameMode gamemode : GameMode.values()) {
						if (gamemode.name().toLowerCase(Locale.ROOT).startsWith(s1)) {
							if (flag2) {
								suggestionBuilder.suggest('!' + gamemode.name().toLowerCase(Locale.ROOT));
							}

							if (flag1) {
								suggestionBuilder.suggest(gamemode.name().toLowerCase(Locale.ROOT));
							}
						}
					}

					return suggestionBuilder.buildFuture();
				});
				int i = parser.getReader().getCursor();
				boolean flag = parser.shouldInvertValue();
				if (parser.hasGamemodeNotEquals() && !flag) {
					parser.getReader().setCursor(i);
					throw EntityOptions.INAPPLICABLE_ENTITY_OPTION.createWithContext(parser.getReader(), "gamemode");
				}
				String s = parser.getReader().readUnquotedString();
				GameMode gametype = null;
				for (GameMode mode : GameMode.values()) {
					if (s.equals(mode.name().toLowerCase(Locale.ROOT))) {
						gametype = mode;
					}
				}
				final GameMode gamemode = gametype;
				if (gametype == null) {
					parser.getReader().setCursor(i);
					throw EntityOptions.INVALID_GAME_MODE.createWithContext(parser.getReader(), s);
				}
				parser.setIncludeNonPlayers(false);
				parser.addFilter((entity) -> {
					if (!(entity instanceof Player)) {
						return false;
					}
					GameMode gametype1 = ((Player) entity).getGameMode();
					return flag ? gametype1 != gamemode : gametype1 == gamemode;
				});
				if (flag) {
					parser.setHasGamemodeNotEquals(true);
				} else {
					parser.setHasGamemodeEquals(true);
				}
			}, (p_202048_0_) -> {
				return !p_202048_0_.hasGamemodeEquals();
			}, null);
			/*
			 * register("team", (parser) -> { boolean flag = parser.shouldInvertValue();
			 * String s = parser.getReader().readUnquotedString();
			 * parser.addFilter((p_197454_2_) -> { if (!(p_197454_2_ instanceof
			 * LivingEntity)) { return false; } else { Team team = p_197454_2_.getTeam();
			 * String s1 = team == null ? "" : team.getName(); return s1.equals(s) != flag;
			 * } }); if (flag) { parser.setHasTeamNotEquals(true); } else {
			 * parser.setHasTeamEquals(true); }
			 * 
			 * }, (p_202038_0_) -> { return !p_202038_0_.hasTeamEquals(); }, null);
			 */
			EntityOptions.register("type", (parser) -> {
				parser.setSuggestionHandler((suggestionBuilder, unused) -> {
					ISuggestionProvider.suggest(Arrays.asList(EntityType.values())
							.stream()
							.map(e -> e.name().toLowerCase(Locale.ROOT))
							.collect(Collectors.toList()), suggestionBuilder, String.valueOf('!'));
					if (!parser.isTypeLimitedInversely()) {
						ISuggestionProvider.suggest(Arrays.asList(EntityType.values())
								.stream()
								.map(e -> e.name().toLowerCase(Locale.ROOT))
								.collect(Collectors.toList()), suggestionBuilder);
					}
					return suggestionBuilder.buildFuture();
				});
				int i = parser.getReader().getCursor();
				boolean inverted = parser.shouldInvertValue();
				if (parser.isTypeLimitedInversely() && !inverted) {
					parser.getReader().setCursor(i);
					throw EntityOptions.INAPPLICABLE_ENTITY_OPTION.createWithContext(parser.getReader(), "type");
				}
				if (inverted) {
					parser.setTypeLimitedInversely();
				}

				String resourcelocation1 = EntityOptions.ResourceLocation_read(parser.getReader());
				EntityType entitytype = Arrays.asList(EntityType.values()).stream().filter(e -> {
					return e.name().toLowerCase(Locale.ROOT).equals(resourcelocation1.toLowerCase(Locale.ROOT));
				}).findAny().orElseThrow(() -> {
					parser.getReader().setCursor(i);
					return EntityOptions.INVALID_ENTITY_TYPE.createWithContext(parser.getReader(), resourcelocation1);
				});
//					EntityType entitytype = Registry.ENTITY_TYPE.getOptional(resourcelocation1).orElseThrow(() -> {
//						p_197447_0_.getReader().setCursor(i);
//						return INVALID_ENTITY_TYPE.createWithContext(p_197447_0_.getReader(),
//								resourcelocation1.toString());
//					});
				if (Objects.equal(EntityType.PLAYER, entitytype) && !inverted) {
					parser.setIncludeNonPlayers(false);
				}

				parser.addFilter((entity) -> {
					return Objects.equal(entitytype, entity.getType()) != inverted;
				});
				if (!inverted) {
					parser.setEntityType(entitytype);
				}
			}, (parser) -> {
				return !parser.isTypeLimited();
			}, null);
			/*
			 * register("tag", (parser) -> { boolean flag = parser.shouldInvertValue();
			 * String s = parser.getReader().readUnquotedString();
			 * parser.addFilter((p_197466_2_) -> { if ("".equals(s)) { return
			 * p_197466_2_.getTags().isEmpty() != flag; } else { return
			 * p_197466_2_.getTags().contains(s) != flag; } }); }, (p_202041_0_) -> { return
			 * true; }, null);
			 */
			/*
			 * register("nbt", (parser) -> { boolean flag = parser.shouldInvertValue();
			 * CompoundNBT compoundnbt = (new JsonToNBT(parser.getReader())).readStruct();
			 * parser.addFilter((p_197443_2_) -> { CompoundNBT compoundnbt1 =
			 * p_197443_2_.writeWithoutTypeId(new CompoundNBT()); if (p_197443_2_ instanceof
			 * ServerPlayerEntity) { ItemStack itemstack = ((ServerPlayerEntity)
			 * p_197443_2_).inventory.getCurrentItem(); if (!itemstack.isEmpty()) {
			 * compoundnbt1.put("SelectedItem", itemstack.write(new CompoundNBT())); } }
			 * 
			 * return NBTUtil.areNBTEquals(compoundnbt, compoundnbt1, true) != flag; }); },
			 * (p_202046_0_) -> { return true; }, null);
			 */
			/*
			 * register("scores", (parser) -> { StringReader stringreader =
			 * parser.getReader(); Map<String, MinMaxBounds.IntBound> map =
			 * Maps.newHashMap(); stringreader.expect('{'); stringreader.skipWhitespace();
			 * 
			 * while (stringreader.canRead() && stringreader.peek() != '}') {
			 * stringreader.skipWhitespace(); String s = stringreader.readUnquotedString();
			 * stringreader.skipWhitespace(); stringreader.expect('=');
			 * stringreader.skipWhitespace(); MinMaxBounds.IntBound minmaxbounds$intbound =
			 * MinMaxBounds.IntBound.fromReader(stringreader); map.put(s,
			 * minmaxbounds$intbound); stringreader.skipWhitespace(); if
			 * (stringreader.canRead() && stringreader.peek() == ',') { stringreader.skip();
			 * } }
			 * 
			 * stringreader.expect('}'); if (!map.isEmpty()) {
			 * parser.addFilter((p_197465_1_) -> { Scoreboard scoreboard =
			 * p_197465_1_.getServer().getScoreboard(); String s1 =
			 * p_197465_1_.getScoreboardName();
			 * 
			 * for (Entry<String, MinMaxBounds.IntBound> entry : map.entrySet()) {
			 * ScoreObjective scoreobjective = scoreboard.getObjective(entry.getKey()); if
			 * (scoreobjective == null) { return false; }
			 * 
			 * if (!scoreboard.entityHasObjective(s1, scoreobjective)) { return false; }
			 * 
			 * Score score = scoreboard.getOrCreateScore(s1, scoreobjective); int i =
			 * score.getScorePoints(); if (!entry.getValue().test(i)) { return false; } }
			 * 
			 * return true; }); }
			 * 
			 * parser.setHasScores(true); }, (p_202033_0_) -> { return
			 * !p_202033_0_.hasScores(); }, null);
			 */
			/*
			 * register("advancements", (parser) -> { StringReader stringreader =
			 * parser.getReader(); Map<ResourceLocation, Predicate<AdvancementProgress>> map
			 * = Maps.newHashMap(); stringreader.expect('{'); stringreader.skipWhitespace();
			 * 
			 * while (stringreader.canRead() && stringreader.peek() != '}') {
			 * stringreader.skipWhitespace(); ResourceLocation resourcelocation =
			 * ResourceLocation.read(stringreader); stringreader.skipWhitespace();
			 * stringreader.expect('='); stringreader.skipWhitespace(); if
			 * (stringreader.canRead() && stringreader.peek() == '{') { Map<String,
			 * Predicate<CriterionProgress>> map1 = Maps.newHashMap();
			 * stringreader.skipWhitespace(); stringreader.expect('{');
			 * stringreader.skipWhitespace();
			 * 
			 * while (stringreader.canRead() && stringreader.peek() != '}') {
			 * stringreader.skipWhitespace(); String s = stringreader.readUnquotedString();
			 * stringreader.skipWhitespace(); stringreader.expect('=');
			 * stringreader.skipWhitespace(); boolean flag1 = stringreader.readBoolean();
			 * map1.put(s, (p_197444_1_) -> { return p_197444_1_.isObtained() == flag1; });
			 * stringreader.skipWhitespace(); if (stringreader.canRead() &&
			 * stringreader.peek() == ',') { stringreader.skip(); } }
			 * 
			 * stringreader.skipWhitespace(); stringreader.expect('}');
			 * stringreader.skipWhitespace(); map.put(resourcelocation, (p_197435_1_) -> {
			 * for (Entry<String, Predicate<CriterionProgress>> entry : map1.entrySet()) {
			 * CriterionProgress criterionprogress =
			 * p_197435_1_.getCriterionProgress(entry.getKey()); if (criterionprogress ==
			 * null || !entry.getValue().test(criterionprogress)) { return false; } }
			 * 
			 * return true; }); } else { boolean flag = stringreader.readBoolean();
			 * map.put(resourcelocation, (p_197451_1_) -> { return p_197451_1_.isDone() ==
			 * flag; }); }
			 * 
			 * stringreader.skipWhitespace(); if (stringreader.canRead() &&
			 * stringreader.peek() == ',') { stringreader.skip(); } }
			 * 
			 * stringreader.expect('}'); if (!map.isEmpty()) { parser.addFilter((entity) ->
			 * { if (!(entity instanceof Player)) { return false; } else { Player
			 * serverplayerentity = (Player) entity; PlayerAdvancements playeradvancements =
			 * serverplayerentity.getAdvancements(); AdvancementManager advancementmanager =
			 * serverplayerentity.getServer() .getAdvancementManager();
			 * 
			 * for (Entry<ResourceLocation, Predicate<AdvancementProgress>> entry :
			 * map.entrySet()) { Advancement advancement =
			 * advancementmanager.getAdvancement(entry.getKey()); if (advancement == null ||
			 * !entry.getValue().test(playeradvancements.getProgress(advancement))) { return
			 * false; } }
			 * 
			 * return true; } }); parser.setIncludeNonPlayers(false); }
			 * 
			 * parser.setHasAdvancements(true); }, (p_202032_0_) -> { return
			 * !p_202032_0_.hasAdvancements(); }, null);
			 */
			/*
			 * register("predicate", (parser) -> { boolean flag =
			 * parser.shouldInvertValue(); ResourceLocation resourcelocation =
			 * ResourceLocation.read(parser.getReader()); parser.addFilter((p_229366_2_) ->
			 * { if (!(p_229366_2_.world instanceof ServerWorld)) { return false; } else {
			 * ServerWorld serverworld = (ServerWorld) p_229366_2_.world; ILootCondition
			 * ilootcondition = serverworld.getServer() .func_229736_aP_()
			 * .func_227517_a_(resourcelocation); if (ilootcondition == null) { return
			 * false; } else { LootContext lootcontext = (new
			 * LootContext.Builder(serverworld)) .withParameter(LootParameters.THIS_ENTITY,
			 * p_229366_2_) .withParameter(LootParameters.field_237457_g_,
			 * p_229366_2_.getPositionVec()) .build(LootParameterSets.SELECTOR); return flag
			 * ^ ilootcondition.test(lootcontext); } } }); }, (p_229365_0_) -> { return
			 * true; }, null);
			 */
		}
	}

	public static String ResourceLocation_read(StringReader reader) {
		int i = reader.getCursor();

		while (reader.canRead() && EntityOptions.ResourceLocation_isValidPathCharacter(reader.peek())) {
			reader.skip();
		}

		String s = reader.getString().substring(i, reader.getCursor());

		return s;
	}

	public static boolean ResourceLocation_isValidPathCharacter(char charIn) {
		return charIn >= '0' && charIn <= '9' || charIn >= 'a' && charIn <= 'z' || charIn == '_' || charIn == ':'
				|| charIn == '/' || charIn == '.' || charIn == '-';
	}

	public static EntityOptions.IFilter get(EntitySelectorParser parser, String id, int cursor)
			throws CommandSyntaxException {
		EntityOptions.OptionHandler entityoptions$optionhandler = EntityOptions.REGISTRY.get(id);
		if (entityoptions$optionhandler != null) {
			if (entityoptions$optionhandler.canHandle.test(parser)) {
				return entityoptions$optionhandler.handler;
			}
			throw EntityOptions.INAPPLICABLE_ENTITY_OPTION.createWithContext(parser.getReader(), id);
		}
		parser.getReader().setCursor(cursor);
		throw EntityOptions.UNKNOWN_ENTITY_OPTION.createWithContext(parser.getReader(), id);
	}

	public static void suggestOptions(EntitySelectorParser parser, SuggestionsBuilder builder) {
		String s = builder.getRemaining().toLowerCase(Locale.ROOT);

		for (Entry<String, EntityOptions.OptionHandler> entry : EntityOptions.REGISTRY.entrySet()) {
			if ((entry.getValue()).canHandle.test(parser) && entry.getKey().toLowerCase(Locale.ROOT).startsWith(s)) {
				builder.suggest(entry.getKey() + '=', (entry.getValue()).tooltip);
			}
		}

	}

	public static interface IFilter {

		void handle(EntitySelectorParser parser) throws CommandSyntaxException;

	}

	public static class OptionHandler {

		public final EntityOptions.IFilter handler;

		public final Predicate<EntitySelectorParser> canHandle;

		public final com.mojang.brigadier.Message tooltip;

		private OptionHandler(EntityOptions.IFilter handlerIn, Predicate<EntitySelectorParser> p_i48717_2_,
				com.mojang.brigadier.Message tooltipIn) {
			this.handler = handlerIn;
			this.canHandle = p_i48717_2_;
			this.tooltip = tooltipIn;
		}

	}

}

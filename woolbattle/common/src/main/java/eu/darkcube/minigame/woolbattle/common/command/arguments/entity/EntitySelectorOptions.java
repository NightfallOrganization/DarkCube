/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments.entity;

import static eu.darkcube.minigame.woolbattle.common.command.arguments.entity.TranslatableWrapper.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.MinMaxBounds;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.key.InvalidKeyException;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class EntitySelectorOptions {
    private static final Map<String, Option> OPTIONS = new HashMap<>();
    public static final DynamicCommandExceptionType ERROR_TEAM_UNKNOWN = Messages.TEAM_CONFIGURATION_NOT_FOUND.newDynamicCommandExceptionType();
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION = new DynamicCommandExceptionType(option -> translatableEscape("argument.entity.options.unknown", option));
    public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION = new DynamicCommandExceptionType(option -> translatableEscape("argument.entity.options.inapplicable", option));
    public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE = new SimpleCommandExceptionType(translatable("argument.entity.options.distance.negative"));
    public static final SimpleCommandExceptionType ERROR_LEVEL_NEGATIVE = new SimpleCommandExceptionType(translatable("argument.entity.options.level.negative"));
    public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL = new SimpleCommandExceptionType(translatable("argument.entity.options.limit.toosmall"));
    public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN = new DynamicCommandExceptionType(sortType -> translatableEscape("argument.entity.options.sort.irreversible", sortType));
    // public static final DynamicCommandExceptionType ERROR_GAME_MODE_INVALID = new DynamicCommandExceptionType(gameMode -> translatableEscape("argument.entity.options.mode.invalid", gameMode));
    public static final DynamicCommandExceptionType ERROR_ENTITY_TYPE_INVALID = new DynamicCommandExceptionType(entity -> translatableEscape("argument.entity.options.type.invalid", entity));
    // Paper start - tell clients to ask server for suggestions for EntityArguments
    // public static final DynamicCommandExceptionType ERROR_ENTITY_TAG_INVALID = new DynamicCommandExceptionType((object) -> text(Component.text("Invalid or unknown entity type tag '" + object + "'").hoverEvent(HoverEvent.showText(Component.text("You can disable this error in 'paper.yml'")))));
    // Paper end - tell clients to ask server for suggestions for EntityArguments

    private static void register(String id, EntitySelectorOptions.Modifier handler, Predicate<EntitySelectorParser> condition, Component description) {
        OPTIONS.put(id, new EntitySelectorOptions.Option(handler, condition, description));
    }

    public static void bootStrap() {
        if (OPTIONS.isEmpty()) {
            register("name", reader -> {
                var cursor = reader.getReader().getCursor();
                var bl = reader.shouldInvertValue();
                var string = reader.getReader().readString();
                if (reader.hasNameNotEquals() && !bl) {
                    reader.getReader().setCursor(cursor);
                    throw ERROR_INAPPLICABLE_OPTION.createWithContext(reader.getReader(), "name");
                } else {
                    if (bl) {
                        reader.setHasNameNotEquals(true);
                    } else {
                        reader.setHasNameEquals(true);
                    }
                    reader.addPredicate(entity -> {
                        var name = PlainTextComponentSerializer.plainText().serialize(entity.getName());
                        return name.equals(string) != bl;
                    });
                }
            }, reader -> !reader.hasNameEquals(), Component.translatable("argument.entity.options.name.description"));
            register("distance", reader -> {
                var i = reader.getReader().getCursor();
                var doubles = MinMaxBounds.Doubles.fromReader(reader.getReader());
                if ((doubles.min().isEmpty() || !(doubles.min().get() < 0.0)) && (doubles.max().isEmpty() || !(doubles.max().get() < 0.0))) {
                    reader.setDistance(doubles);
                    reader.setWorldLimited();
                } else {
                    reader.getReader().setCursor(i);
                    throw ERROR_RANGE_NEGATIVE.createWithContext(reader.getReader());
                }
            }, reader -> reader.getDistance().isAny(), Component.translatable("argument.entity.options.distance.description"));
            register("level", reader -> {
                var i = reader.getReader().getCursor();
                var ints = MinMaxBounds.Ints.fromReader(reader.getReader());
                if ((ints.min().isEmpty() || ints.min().get() >= 0) && (ints.max().isEmpty() || ints.max().get() >= 0)) {
                    reader.setLevel(ints);
                    reader.setIncludesEntities(false);
                } else {
                    reader.getReader().setCursor(i);
                    throw ERROR_LEVEL_NEGATIVE.createWithContext(reader.getReader());
                }
            }, reader -> reader.getLevel().isAny(), Component.translatable("argument.entity.options.level.description"));
            register("x", reader -> {
                reader.setWorldLimited();
                reader.setX(reader.getReader().readDouble());
            }, reader -> reader.getX() == null, Component.translatable("argument.entity.options.x.description"));
            register("y", reader -> {
                reader.setWorldLimited();
                reader.setY(reader.getReader().readDouble());
            }, reader -> reader.getY() == null, Component.translatable("argument.entity.options.y.description"));
            register("z", reader -> {
                reader.setWorldLimited();
                reader.setZ(reader.getReader().readDouble());
            }, reader -> reader.getZ() == null, Component.translatable("argument.entity.options.z.description"));
            register("dx", reader -> {
                reader.setWorldLimited();
                reader.setDeltaX(reader.getReader().readDouble());
            }, reader -> reader.getDeltaX() == null, Component.translatable("argument.entity.options.dx.description"));
            register("dy", reader -> {
                reader.setWorldLimited();
                reader.setDeltaY(reader.getReader().readDouble());
            }, reader -> reader.getDeltaY() == null, Component.translatable("argument.entity.options.dy.description"));
            register("dz", reader -> {
                reader.setWorldLimited();
                reader.setDeltaZ(reader.getReader().readDouble());
            }, reader -> reader.getDeltaZ() == null, Component.translatable("argument.entity.options.dz.description"));
            register("x_rotation", reader -> reader.setRotX(MinMaxBounds.Doubles.fromReader(reader.getReader(), Mth::wrapDegrees)), reader -> reader.getRotX() == MinMaxBounds.Doubles.ANY, Component.translatable("argument.entity.options.x_rotation.description"));
            register("y_rotation", reader -> reader.setRotY(MinMaxBounds.Doubles.fromReader(reader.getReader(), Mth::wrapDegrees)), reader -> reader.getRotY() == MinMaxBounds.Doubles.ANY, Component.translatable("argument.entity.options.y_rotation.description"));
            register("limit", reader -> {
                var i = reader.getReader().getCursor();
                var j = reader.getReader().readInt();
                if (j < 1) {
                    reader.getReader().setCursor(i);
                    throw ERROR_LIMIT_TOO_SMALL.createWithContext(reader.getReader());
                } else {
                    reader.setMaxResults(j);
                    reader.setLimited(true);
                }
            }, reader -> !reader.isCurrentEntity() && !reader.isLimited(), Component.translatable("argument.entity.options.limit.description"));
            register("sort", reader -> {
                var i = reader.getReader().getCursor();
                var string = reader.getReader().readUnquotedString();
                reader.setSuggestions((builder, _) -> ISuggestionProvider.suggest(Arrays.asList("nearest", "furthest", "random", "arbitrary"), builder));

                reader.setOrder(switch (string) {
                    case "nearest" -> EntitySelectorParser.ORDER_NEAREST;
                    case "furthest" -> EntitySelectorParser.ORDER_FURTHEST;
                    case "random" -> EntitySelectorParser.ORDER_RANDOM;
                    case "arbitrary" -> EntitySelector.ORDER_ARBITRARY;
                    default -> {
                        reader.getReader().setCursor(i);
                        throw ERROR_SORT_UNKNOWN.createWithContext(reader.getReader(), string);
                    }
                });
                reader.setSorted(true);
            }, reader -> !reader.isCurrentEntity() && !reader.isSorted(), Component.translatable("argument.entity.options.sort.description"));
            // register("gamemode", reader -> {
            //     reader.setSuggestions((builder, consumer) -> {
            //         String stringx = builder.getRemaining().toLowerCase(Locale.ROOT);
            //         boolean blx = !reader.hasGamemodeNotEquals();
            //         boolean bl2 = true;
            //         if (!stringx.isEmpty()) {
            //             if (stringx.charAt(0) == '!') {
            //                 blx = false;
            //                 stringx = stringx.substring(1);
            //             } else {
            //                 bl2 = false;
            //             }
            //         }
            //
            //         for (GameType gameTypex : GameType.values()) {
            //             if (gameTypex.getName().toLowerCase(Locale.ROOT).startsWith(stringx)) {
            //                 if (bl2) {
            //                     builder.suggest("!" + gameTypex.getName());
            //                 }
            //
            //                 if (blx) {
            //                     builder.suggest(gameTypex.getName());
            //                 }
            //             }
            //         }
            //
            //         return builder.buildFuture();
            //     });
            //     int cursor = reader.getReader().getCursor();
            //     boolean invert = reader.shouldInvertValue();
            //     if (reader.hasGamemodeNotEquals() && !invert) {
            //         reader.getReader().setCursor(cursor);
            //         throw ERROR_INAPPLICABLE_OPTION.createWithContext(reader.getReader(), "gamemode");
            //     } else {
            //         String string = reader.getReader().readUnquotedString();
            //         GameType gameType = GameType.byName(string, null);
            //         if (gameType == null) {
            //             reader.getReader().setCursor(cursor);
            //             throw ERROR_GAME_MODE_INVALID.createWithContext(reader.getReader(), string);
            //         } else {
            //             reader.setIncludesEntities(false);
            //             reader.addPredicate(entity -> {
            //                 if (!(entity instanceof WBUser)) {
            //                     return false;
            //                 } else {
            //                     GameType gameType2 = ((ServerPlayer) entity).gameMode.getGameModeForPlayer();
            //                     return invert ? gameType2 != gameType : gameType2 == gameType;
            //                 }
            //             });
            //             if (invert) {
            //                 reader.setHasGamemodeNotEquals(true);
            //             } else {
            //                 reader.setHasGamemodeEquals(true);
            //             }
            //         }
            //     }
            // }, reader -> !reader.hasGamemodeEquals(), Component.translatable("argument.entity.options.gamemode.description"));
            register("team", reader -> {
                var invert = reader.shouldInvertValue();
                var cursor = reader.getReader().getCursor();
                var string = reader.getReader().readUnquotedString();
                reader.addPredicate(entity -> {
                    if (!(entity instanceof WBUser user)) {
                        return false;
                    } else {
                        var team = user.team();
                        var string2 = team == null ? "" : team.key();
                        return string2.equals(string) != invert;
                    }
                });
                reader.setSuggestions((builder, _, source) -> {
                    var game = source.game();
                    if (game != null) {
                        var input = builder.getRemainingLowerCase();
                        for (var team : game.teamManager().teams()) {
                            var key = team.key();
                            if (ISuggestionProvider.test(input, key.toLowerCase(Locale.ROOT))) {
                                builder.suggest(key, text(team.getName(source.audience())));
                            }
                        }
                        if (ISuggestionProvider.test(input, "none")) {
                            builder.suggest("none");
                        }
                    }
                    return builder.buildFuture();
                });

                var source = reader.source();
                if (source != null) {
                    var game = source.game();
                    if (game != null) {
                        var found = false;
                        for (var team : game.teamManager().teams()) {
                            if (team.key().equals(string)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            reader.getReader().setCursor(cursor);
                            throw ERROR_TEAM_UNKNOWN.createWithContext(reader.getReader(), string);
                        }
                    } else {
                        if (!string.equals("none")) {
                            reader.getReader().setCursor(cursor);
                            throw ERROR_TEAM_UNKNOWN.createWithContext(reader.getReader(), string);
                        }
                    }
                }

                if (invert) {
                    reader.setHasTeamNotEquals(true);
                } else {
                    reader.setHasTeamEquals(true);
                }
            }, reader -> !reader.hasTeamEquals(), Component.translatable("argument.entity.options.team.description"));
            register("type", reader -> {
                reader.setSuggestions((builder, _) -> {
                    ISuggestionProvider.suggest(EntityType.REGISTRY.keySet().stream().map(Key::asMinimalString).toList(), builder, String.valueOf('!'));
                    ISuggestionProvider.suggest(EntityType.REGISTRY.keySet().stream().map(Key::asString).toList(), builder, String.valueOf('!'));
                    if (!reader.isTypeLimitedInversely()) {
                        ISuggestionProvider.suggest(EntityType.REGISTRY.keySet().stream().map(Key::asMinimalString).toList(), builder);
                        ISuggestionProvider.suggest(EntityType.REGISTRY.keySet().stream().map(Key::asString).toList(), builder);
                    }

                    return builder.buildFuture();
                });
                var cursor = reader.getReader().getCursor();
                var invert = reader.shouldInvertValue();
                if (reader.isTypeLimitedInversely() && !invert) {
                    reader.getReader().setCursor(cursor);
                    throw ERROR_INAPPLICABLE_OPTION.createWithContext(reader.getReader(), "type");
                } else {
                    if (invert) {
                        reader.setTypeLimitedInversely();
                    }

                    // NO SUPPORT FOR TAGS
                    // if (reader.isTag()) {
                    //     TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.read(reader.getReader()));
                    //     // Paper start - tell clients to ask server for suggestions for EntityArguments; throw error if invalid entity tag (only on suggestions to keep cmd success behavior)
                    //     if (reader.parsingEntityArgumentSuggestions && io.papermc.paper.configuration.GlobalConfiguration.get().commands.fixTargetSelectorTagCompletion && net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getTag(tagKey).isEmpty()) {
                    //         reader.getReader().setCursor(i);
                    //         throw ERROR_ENTITY_TAG_INVALID.createWithContext(reader.getReader(), tagKey);
                    //     }
                    //     // Paper end - tell clients to ask server for suggestions for EntityArguments
                    //     reader.addPredicate(entity -> entity.type().is(tagKey) != bl);
                    // } else {
                    var resourceLocation = readKey(reader.getReader());
                    var entityType = EntityType.REGISTRY.getOptional(resourceLocation).orElseThrow(() -> {
                        reader.getReader().setCursor(cursor);
                        return ERROR_ENTITY_TYPE_INVALID.createWithContext(reader.getReader(), resourceLocation.toString());
                    });
                    if (Objects.equals(EntityType.PLAYER, entityType) && !invert) {
                        reader.setIncludesEntities(false);
                    }

                    reader.addPredicate(entity -> Objects.equals(entityType, entity.type()) != invert);
                    if (!invert) {
                        reader.limitToType(entityType);
                    }
                    // } NO SUPPORT FOR TAGS
                }
            }, reader -> !reader.isTypeLimited(), Component.translatable("argument.entity.options.type.description"));
            // register("tag", reader -> {
            //     boolean bl = reader.shouldInvertValue();
            //     String string = reader.getReader().readUnquotedString();
            //     reader.addPredicate(entity -> "".equals(string) ? entity.getTags().isEmpty() != bl : entity.getTags().contains(string) != bl);
            // }, reader -> true, Component.translatable("argument.entity.options.tag.description"));
            // register("nbt", reader -> {
            //     boolean bl = reader.shouldInvertValue();
            //     CompoundTag compoundTag = new TagParser(reader.getReader()).readStruct();
            //     reader.addPredicate(entity -> {
            //         CompoundTag compoundTag2 = entity.saveWithoutId(new CompoundTag());
            //         if (entity instanceof ServerPlayer serverPlayer) {
            //             ItemStack itemStack = serverPlayer.getInventory().getSelected();
            //             if (!itemStack.isEmpty()) {
            //                 compoundTag2.put("SelectedItem", itemStack.save(serverPlayer.registryAccess()));
            //             }
            //         }
            //
            //         return NbtUtils.compareNbt(compoundTag, compoundTag2, true) != bl;
            //     });
            // }, reader -> true, Component.translatable("argument.entity.options.nbt.description"));
            // register("scores", reader -> {
            //     StringReader stringReader = reader.getReader();
            //     Map<String, MinMaxBounds.Ints> map = Maps.newHashMap();
            //     stringReader.expect('{');
            //     stringReader.skipWhitespace();
            //
            //     while (stringReader.canRead() && stringReader.peek() != '}') {
            //         stringReader.skipWhitespace();
            //         String string = stringReader.readUnquotedString();
            //         stringReader.skipWhitespace();
            //         stringReader.expect('=');
            //         stringReader.skipWhitespace();
            //         MinMaxBounds.Ints ints = MinMaxBounds.Ints.fromReader(stringReader);
            //         map.put(string, ints);
            //         stringReader.skipWhitespace();
            //         if (stringReader.canRead() && stringReader.peek() == ',') {
            //             stringReader.skip();
            //         }
            //     }
            //
            //     stringReader.expect('}');
            //     if (!map.isEmpty()) {
            //         reader.addPredicate(entity -> {
            //             Scoreboard scoreboard = entity.getServer().getScoreboard();
            //
            //             for (Entry<String, MinMaxBounds.Ints> entry : map.entrySet()) {
            //                 Objective objective = scoreboard.getObjective(entry.getKey());
            //                 if (objective == null) {
            //                     return false;
            //                 }
            //
            //                 ReadOnlyScoreInfo readOnlyScoreInfo = scoreboard.getPlayerScoreInfo(entity, objective);
            //                 if (readOnlyScoreInfo == null) {
            //                     return false;
            //                 }
            //
            //                 if (!entry.getValue().matches(readOnlyScoreInfo.value())) {
            //                     return false;
            //                 }
            //             }
            //
            //             return true;
            //         });
            //     }
            //
            //     reader.setHasScores(true);
            // }, reader -> !reader.hasScores(), Component.translatable("argument.entity.options.scores.description"));
            // register("advancements", reader -> {
            //     StringReader stringReader = reader.getReader();
            //     Map<ResourceLocation, Predicate<AdvancementProgress>> map = Maps.newHashMap();
            //     stringReader.expect('{');
            //     stringReader.skipWhitespace();
            //
            //     while (stringReader.canRead() && stringReader.peek() != '}') {
            //         stringReader.skipWhitespace();
            //         ResourceLocation resourceLocation = ResourceLocation.read(stringReader);
            //         stringReader.skipWhitespace();
            //         stringReader.expect('=');
            //         stringReader.skipWhitespace();
            //         if (stringReader.canRead() && stringReader.peek() == '{') {
            //             Map<String, Predicate<CriterionProgress>> map2 = Maps.newHashMap();
            //             stringReader.skipWhitespace();
            //             stringReader.expect('{');
            //             stringReader.skipWhitespace();
            //
            //             while (stringReader.canRead() && stringReader.peek() != '}') {
            //                 stringReader.skipWhitespace();
            //                 String string = stringReader.readUnquotedString();
            //                 stringReader.skipWhitespace();
            //                 stringReader.expect('=');
            //                 stringReader.skipWhitespace();
            //                 boolean bl = stringReader.readBoolean();
            //                 map2.put(string, criterionProgress -> criterionProgress.isDone() == bl);
            //                 stringReader.skipWhitespace();
            //                 if (stringReader.canRead() && stringReader.peek() == ',') {
            //                     stringReader.skip();
            //                 }
            //             }
            //
            //             stringReader.skipWhitespace();
            //             stringReader.expect('}');
            //             stringReader.skipWhitespace();
            //             map.put(resourceLocation, advancementProgress -> {
            //                 for (Entry<String, Predicate<CriterionProgress>> entry : map2.entrySet()) {
            //                     CriterionProgress criterionProgress = advancementProgress.getCriterion(entry.getKey());
            //                     if (criterionProgress == null || !entry.getValue().test(criterionProgress)) {
            //                         return false;
            //                     }
            //                 }
            //
            //                 return true;
            //             });
            //         } else {
            //             boolean bl2 = stringReader.readBoolean();
            //             map.put(resourceLocation, advancementProgress -> advancementProgress.isDone() == bl2);
            //         }
            //
            //         stringReader.skipWhitespace();
            //         if (stringReader.canRead() && stringReader.peek() == ',') {
            //             stringReader.skip();
            //         }
            //     }
            //
            //     stringReader.expect('}');
            //     if (!map.isEmpty()) {
            //         reader.addPredicate(entity -> {
            //             if (!(entity instanceof ServerPlayer serverPlayer)) {
            //                 return false;
            //             } else {
            //                 PlayerAdvancements playerAdvancements = serverPlayer.getAdvancements();
            //                 ServerAdvancementManager serverAdvancementManager = serverPlayer.getServer().getAdvancements();
            //
            //                 for (Entry<ResourceLocation, Predicate<AdvancementProgress>> entry : map.entrySet()) {
            //                     AdvancementHolder advancementHolder = serverAdvancementManager.get(entry.getKey());
            //                     if (advancementHolder == null || !entry.getValue().test(playerAdvancements.getOrStartProgress(advancementHolder))) {
            //                         return false;
            //                     }
            //                 }
            //
            //                 return true;
            //             }
            //         });
            //         reader.setIncludesEntities(false);
            //     }
            //
            //     reader.setHasAdvancements(true);
            // }, reader -> !reader.hasAdvancements(), Component.translatable("argument.entity.options.advancements.description"));
            // register("predicate", reader -> {
            //     boolean bl = reader.shouldInvertValue();
            //     ResourceKey<LootItemCondition> resourceKey = ResourceKey.create(Registries.PREDICATE, ResourceLocation.read(reader.getReader()));
            //     reader.addPredicate(entity -> {
            //         if (!(entity.level() instanceof ServerLevel)) {
            //             return false;
            //         } else {
            //             ServerLevel serverLevel = (ServerLevel) entity.level();
            //             Optional<LootItemCondition> optional = serverLevel.getServer().reloadableRegistries().lookup().get(Registries.PREDICATE, resourceKey).map(Holder::value);
            //             if (optional.isEmpty()) {
            //                 return false;
            //             } else {
            //                 LootParams lootParams = new LootParams.Builder(serverLevel).withParameter(LootContextParams.THIS_ENTITY, entity).withParameter(LootContextParams.ORIGIN, entity.position()).create(LootContextParamSets.SELECTOR);
            //                 LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());
            //                 lootContext.pushVisitedElement(LootContext.createVisitedEntry(optional.get()));
            //                 return bl ^ optional.get().test(lootContext);
            //             }
            //         }
            //     });
            // }, reader -> true, Component.translatable("argument.entity.options.predicate.description"));
        }
    }

    public static EntitySelectorOptions.Modifier get(EntitySelectorParser reader, String option, int restoreCursor) throws CommandSyntaxException {
        var option2 = OPTIONS.get(option);
        if (option2 != null) {
            if (option2.canUse.test(reader)) {
                return option2.modifier;
            } else {
                throw ERROR_INAPPLICABLE_OPTION.createWithContext(reader.getReader(), option);
            }
        } else {
            reader.getReader().setCursor(restoreCursor);
            throw ERROR_UNKNOWN_OPTION.createWithContext(reader.getReader(), option);
        }
    }

    public static void suggestNames(EntitySelectorParser reader, SuggestionsBuilder suggestionBuilder) {
        var string = suggestionBuilder.getRemaining().toLowerCase(Locale.ROOT);

        for (var entry : OPTIONS.entrySet()) {
            if (entry.getValue().canUse.test(reader) && entry.getKey().toLowerCase(Locale.ROOT).startsWith(string)) {
                suggestionBuilder.suggest(entry.getKey() + "=", text(entry.getValue().description));
            }
        }
    }

    private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(translatable("argument.id.invalid"));

    private static Key readKey(StringReader reader) throws CommandSyntaxException {
        var start = reader.getCursor();
        while (reader.canRead() && isAllowedInResourceLocation(reader.peek())) {
            reader.skip();
        }
        var greedy = reader.getString().substring(start, reader.getCursor());
        try {
            return Key.key(greedy);
        } catch (InvalidKeyException e) {
            throw ERROR_INVALID.createWithContext(reader);
        }
    }

    public static boolean isAllowedInResourceLocation(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
    }

    public interface Modifier {
        void handle(EntitySelectorParser reader) throws CommandSyntaxException;
    }

    record Option(EntitySelectorOptions.Modifier modifier, Predicate<EntitySelectorParser> canUse, Component description) {
    }
}

/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import com.google.common.base.Objects;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public enum Message {

	ERROR_INTS_ONLY("error_ints_only"),
	ERROR_EMPTY("error_empty"),
	ERROR_SWAPPED("error_swapped"),
	TOO_MANY_ENTITIES("too_many_entities"),
	SELECTOR_NOT_ALLOWED("selector_not_allowed"),
	ONLY_PLAYERS_ALLOWED("only_players_allowed"),
	TOO_MANY_PLAYERS("too_many_players"),
	UNKNOWN_COMMAND_EXCEPTION_TYPE("unknown_command_exception_type"),
	INVALID_ENTITY_NAME_OR_UUID("invalid_entity_name_or_uuid"),
	SELECTOR_TYPE_MISSING("selector_type_missing"),
	EXPECTED_END_OF_OPTIONS("expected_end_of_options"),
	EXPECTED_VALUE_FOR_OPTION("expected_value_for_option"),
	ENTITY_NOT_FOUND("entity_not_found"),
	PLAYER_NOT_FOUND("player_not_found"),
	UNKNOWN_ENTITY_OPTION("unknown_entity_option"),
	INAPPLICABLE_ENTITY_OPTION("inapplicable_entity_option"),
	NEGATIVE_DISTANCE("negative_distance"),
	NEGATIVE_LEVEL("negative_level"),
	NONPOSITIVE_LIMIT("nonpositive_limit"),
	INVALID_SORT("invalid_sort"),
	INVALID_GAME_MODE("invalid_game_mode"),
	INVALID_ENTITY_TYPE("invalid_entity_type"),
	SELECTOR_NEAREST_PLAYER("selector_nearest_player"),
	SELECTOR_ALL_PLAYERS("selector_all_players"),
	SELECTOR_RANDOM_PLAYER("selector_random_player"),
	SELECTOR_SELF("selector_self"),
	SELECTOR_ALL_ENTITIES("selector_all_entities"),
	EXPECTED_DOUBLE("expected_double"),
	EXPECTED_INT("expected_int"),
	VEC2_INCOMPLETE("vec2_incomplete"),
	POS_INCOMPLETE("pos_incomplete"),
	POS_MIXED_TYPES("pos_mixed_types"),
	ROTATION_INCOMPLETE("rotation_incomplete"),
	ANCHOR_INVALID("anchor_invalid"),
	INVALID_ENUM("invalid_enum"),
	INVALID_WORLD("invalid_world"),
	INVALID_UUID("invalid_uuid"),
	BOOLEAN_INVALID("boolean_invalid"),
	SERVICE_TASK_NOT_PRESENT("service_task_not_present"),

	;

	public static final Map<String, Message> MESSAGES = new HashMap<>();

	private final String key;

	private Message(final String key) {
		this.key = key;
	}

	public SimpleCommandExceptionType newSimpleCommandExceptionType() {
		return new SimpleCommandExceptionType(new SimpleMessageWrapper(this));
	}

	public DynamicCommandExceptionType newDynamicCommandExceptionType() {
		return new DynamicCommandExceptionType(o -> {
			if (!(o instanceof Object[])) {
				o = new Object[] {o};
			}
			Object[] components = (Object[]) o;
			return new DynamicMessageWrapper(this, components);
		});
	}

	public SimpleMessageWrapper newSimpleWrapper() {
		return new SimpleMessageWrapper(this);
	}

	public DynamicMessageWrapper newDynamicWrapper(Object... components) {
		return new DynamicMessageWrapper(this, components);
	}

	public String getKey() {
		return key;
	}

	public TextComponent[] apply(Consumer<CustomComponentBuilder> prefixModifier,
			Function<Message, String> toStringFunction, Object... components) {
		CustomComponentBuilder b = new CustomComponentBuilder("");
		CustomComponentBuilder
				.applyPrefixModifier(prefixModifier,
						CustomComponentBuilder.cast(TextComponent.fromLegacyText(
								String.format(toStringFunction.apply(this), components))))
				.accept(b);
		TextComponent[] text = b.create();
		return text;
	}

	public TextComponent[] apply(Function<Message, String> toStringFunction, Object... components) {
		return this.apply(b -> {
		}, toStringFunction, components);
	}

	@Override
	public String toString() {
		return getKey();
	}

	static {
		for (Message message : values()) {
			MESSAGES.put(message.getKey(), message);
		}
	}

	public static class DynamicMessageWrapper implements com.mojang.brigadier.Message {

		private final Message message;
		private final Object[] components;

		public DynamicMessageWrapper(Message message, Object... components) {
			this.message = message;
			this.components = components;
		}

		@Override
		public String getString() {
			return message.toString();
		}

		public Message getMessage() {
			return message;
		}

		public Object[] getComponents() {
			return components;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof DynamicMessageWrapper
					&& Objects.equal(((DynamicMessageWrapper) obj).message, message)
					&& Arrays.equals(components, ((DynamicMessageWrapper) obj).components);
		}

		@Override
		public int hashCode() {
			return message.hashCode();
		}

	}

	public static class SimpleMessageWrapper implements com.mojang.brigadier.Message {

		private final Message message;

		public SimpleMessageWrapper(Message message) {
			this.message = message;
		}

		@Override
		public String getString() {
			return message.toString();
		}

		public Message getMessage() {
			return message;
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof SimpleMessageWrapper
					&& Objects.equal(((SimpleMessageWrapper) obj).message, message));
		}

		@Override
		public int hashCode() {
			return message.hashCode();
		}
	}
}

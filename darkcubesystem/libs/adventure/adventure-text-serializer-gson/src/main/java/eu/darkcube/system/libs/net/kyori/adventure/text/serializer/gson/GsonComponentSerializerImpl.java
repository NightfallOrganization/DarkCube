/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.util.Services;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class GsonComponentSerializerImpl implements GsonComponentSerializer {
  private static final Optional<Provider> SERVICE = Services.service(Provider.class);
  static final Consumer<Builder> BUILDER = SERVICE
    .map(Provider::builder)
    .orElseGet(() -> builder -> {
      // NOOP
    });

  // We cannot store these fields in GsonComponentSerializerImpl directly due to class initialisation issues.
  static final class Instances {
    static final GsonComponentSerializer INSTANCE = SERVICE
      .map(Provider::gson)
      .orElseGet(() -> new GsonComponentSerializerImpl(false, null, false));
    static final GsonComponentSerializer LEGACY_INSTANCE = SERVICE
      .map(Provider::gsonLegacy)
      .orElseGet(() -> new GsonComponentSerializerImpl(true, null, true));
  }

  private final Gson serializer;
  private final UnaryOperator<GsonBuilder> populator;
  private final boolean downsampleColor;
  private final @Nullable LegacyHoverEventSerializer legacyHoverSerializer;
  private final boolean emitLegacyHover;

  GsonComponentSerializerImpl(final boolean downsampleColor, final @Nullable LegacyHoverEventSerializer legacyHoverSerializer, final boolean emitLegacyHover) {
    this.downsampleColor = downsampleColor;
    this.legacyHoverSerializer = legacyHoverSerializer;
    this.emitLegacyHover = emitLegacyHover;
    this.populator = builder -> {
      builder.registerTypeAdapterFactory(new SerializerFactory(downsampleColor, legacyHoverSerializer, emitLegacyHover));
      return builder;
    };
    this.serializer = this.populator.apply(
      new GsonBuilder()
        .disableHtmlEscaping() // to be consistent with vanilla
    ).create();
  }

  @Override
  public @NotNull Gson serializer() {
    return this.serializer;
  }

  @Override
  public @NotNull UnaryOperator<GsonBuilder> populator() {
    return this.populator;
  }

  @Override
  public @NotNull Component deserialize(final @NotNull String string) {
    final Component component = this.serializer().fromJson(string, Component.class);
    if (component == null) throw ComponentSerializerImpl.notSureHowToDeserialize(string);
    return component;
  }

  @Override
  public @Nullable Component deserializeOr(final @Nullable String input, final @Nullable Component fallback) {
    if (input == null) return fallback;
    final Component component = this.serializer().fromJson(input, Component.class);
    if (component == null) return fallback;
    return component;
  }

  @Override
  public @NotNull String serialize(final @NotNull Component component) {
    return this.serializer().toJson(component);
  }

  @Override
  public @NotNull Component deserializeFromTree(final @NotNull JsonElement input) {
    final Component component = this.serializer().fromJson(input, Component.class);
    if (component == null) throw ComponentSerializerImpl.notSureHowToDeserialize(input);
    return component;
  }

  @Override
  public @NotNull JsonElement serializeToTree(final @NotNull Component component) {
    return this.serializer().toJsonTree(component);
  }

  @Override
  public @NotNull Builder toBuilder() {
    return new BuilderImpl(this);
  }

  static final class BuilderImpl implements Builder {
    private boolean downsampleColor = false;
    private @Nullable LegacyHoverEventSerializer legacyHoverSerializer;
    private boolean emitLegacyHover = false;

    BuilderImpl() {
      BUILDER.accept(this); // let service provider touch the builder before anybody else touches it
    }

    BuilderImpl(final GsonComponentSerializerImpl serializer) {
      this();
      this.downsampleColor = serializer.downsampleColor;
      this.emitLegacyHover = serializer.emitLegacyHover;
      this.legacyHoverSerializer = serializer.legacyHoverSerializer;
    }

    @Override
    public @NotNull Builder downsampleColors() {
      this.downsampleColor = true;
      return this;
    }

    @Override
    public @NotNull Builder legacyHoverEventSerializer(final @Nullable LegacyHoverEventSerializer serializer) {
      this.legacyHoverSerializer = serializer;
      return this;
    }

    @Override
    public @NotNull Builder emitLegacyHoverEvent() {
      this.emitLegacyHover = true;
      return this;
    }

    @Override
    public @NotNull GsonComponentSerializer build() {
      if (this.legacyHoverSerializer == null) {
        return this.downsampleColor ? Instances.LEGACY_INSTANCE : Instances.INSTANCE;
      } else {
        return new GsonComponentSerializerImpl(this.downsampleColor, this.legacyHoverSerializer, this.emitLegacyHover);
      }
    }
  }
}

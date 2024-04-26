/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.plain;

import java.util.Optional;
import java.util.function.Consumer;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.flattener.ComponentFlattener;
import eu.darkcube.system.libs.net.kyori.adventure.util.Services;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

final class PlainTextComponentSerializerImpl implements PlainTextComponentSerializer {
  private static final ComponentFlattener DEFAULT_FLATTENER = ComponentFlattener.basic().toBuilder()
    .unknownMapper(component -> {
      throw new UnsupportedOperationException("Don't know how to turn " + component.getClass().getSimpleName() + " into a string");
    })
    .build();
  private static final Optional<Provider> SERVICE = Services.service(Provider.class);
  static final Consumer<Builder> BUILDER = SERVICE
    .map(Provider::plainText)
    .orElseGet(() -> builder -> {
      // NOOP
    });
  final ComponentFlattener flattener;

  // We cannot store these fields in PlainTextComponentSerializerImpl directly due to class initialisation issues.
  static final class Instances {
    static final PlainTextComponentSerializer INSTANCE = SERVICE
      .map(Provider::plainTextSimple)
      .orElseGet(() -> new PlainTextComponentSerializerImpl(DEFAULT_FLATTENER));
  }

  PlainTextComponentSerializerImpl(final ComponentFlattener flattener) {
    this.flattener = flattener;
  }

  @Override
  public void serialize(final @NotNull StringBuilder sb, final @NotNull Component component) {
    this.flattener.flatten(requireNonNull(component, "component"), sb::append);
  }

  @Override
  public @NotNull Builder toBuilder() {
    return new BuilderImpl(this);
  }

  static final class BuilderImpl implements PlainTextComponentSerializer.Builder {
    private ComponentFlattener flattener = DEFAULT_FLATTENER;

    BuilderImpl() {
      BUILDER.accept(this);
    }

    BuilderImpl(final PlainTextComponentSerializerImpl serializer) {
      this();
      this.flattener = serializer.flattener;
    }

    @Override
    public PlainTextComponentSerializer.@NotNull Builder flattener(final @NotNull ComponentFlattener flattener) {
      this.flattener = requireNonNull(flattener, "flattener");
      return this;
    }

    @Override
    public @NotNull PlainTextComponentSerializer build() {
      return new PlainTextComponentSerializerImpl(this.flattener);
    }
  }
}

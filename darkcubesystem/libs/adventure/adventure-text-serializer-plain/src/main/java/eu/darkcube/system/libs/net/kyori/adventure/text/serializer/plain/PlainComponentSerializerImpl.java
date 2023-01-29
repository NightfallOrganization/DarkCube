/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.serializer.plain;

import java.util.function.Function;
import eu.darkcube.system.libs.net.kyori.adventure.text.KeybindComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.TranslatableComponent;
import eu.darkcube.system.libs.net.kyori.adventure.text.flattener.ComponentFlattener;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

@Deprecated
final class PlainComponentSerializerImpl {
  @Deprecated static final PlainComponentSerializer INSTANCE = new PlainComponentSerializer();

  private PlainComponentSerializerImpl() {
  }

  @Deprecated
  static PlainTextComponentSerializer createRealSerializerFromLegacyFunctions(
    final @Nullable Function<KeybindComponent, String> keybind,
    final @Nullable Function<TranslatableComponent, String> translatable
  ) {
    // if both legacy functions are null we can simply use the standard plain-text serializer, since there is nothing special we need to do
    if (keybind == null && translatable == null) {
      return PlainTextComponentSerializer.plainText();
    }
    final ComponentFlattener.Builder builder = ComponentFlattener.basic().toBuilder();
    if (keybind != null) builder.mapper(KeybindComponent.class, keybind);
    if (translatable != null) builder.mapper(TranslatableComponent.class, translatable);
    return PlainTextComponentSerializer.builder().flattener(builder.build()).build();
  }

  @Deprecated
  static final class BuilderImpl implements PlainComponentSerializer.Builder {
    private final PlainTextComponentSerializer.Builder builder = PlainTextComponentSerializer.builder();

    @Deprecated
    BuilderImpl() {
    }

    @Deprecated
    BuilderImpl(final PlainComponentSerializer serializer) {
      this.builder.flattener(((PlainTextComponentSerializerImpl) serializer.serializer).flattener);
    }

    @Override
    public PlainComponentSerializer.@NotNull Builder flattener(final @NotNull ComponentFlattener flattener) {
      this.builder.flattener(flattener);
      return this;
    }

    @Override
    public @NotNull PlainComponentSerializer build() {
      return new PlainComponentSerializer(this.builder.build());
    }
  }
}

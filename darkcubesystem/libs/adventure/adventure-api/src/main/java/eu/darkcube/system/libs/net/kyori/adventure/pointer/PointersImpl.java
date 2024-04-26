/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.pointer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

final class PointersImpl implements Pointers {
  static final Pointers EMPTY = new Pointers() {
    @Override
    public @NotNull <T> Optional<T> get(final @NotNull Pointer<T> pointer) {
      return Optional.empty();
    }

    @Override
    public <T> boolean supports(final @NotNull Pointer<T> pointer) {
      return false;
    }

    @Override
    public Pointers.@NotNull Builder toBuilder() {
      return new PointersImpl.BuilderImpl();
    }

    @Override
    public String toString() {
      return "EmptyPointers";
    }
  };

  private final Map<Pointer<?>, Supplier<?>> pointers;

  PointersImpl(final @NotNull BuilderImpl builder) {
    this.pointers = new HashMap<>(builder.pointers);
  }

  @Override
  @SuppressWarnings("unchecked") // all values are checked on entry
  public @NotNull <T> Optional<T> get(final @NotNull Pointer<T> pointer) {
    Objects.requireNonNull(pointer, "pointer");
    final Supplier<?> supplier = this.pointers.get(pointer);
    if (supplier == null) {
      return Optional.empty();
    } else {
      return Optional.ofNullable((T) supplier.get());
    }
  }

  @Override
  public <T> boolean supports(final @NotNull Pointer<T> pointer) {
    Objects.requireNonNull(pointer, "pointer");
    return this.pointers.containsKey(pointer);
  }

  @Override
  public Pointers.@NotNull Builder toBuilder() {
    return new BuilderImpl(this);
  }

  static final class BuilderImpl implements Builder {
    private final Map<Pointer<?>, Supplier<?>> pointers;

    BuilderImpl() {
      this.pointers = new HashMap<>();
    }

    BuilderImpl(final @NotNull PointersImpl pointers) {
      this.pointers = new HashMap<>(pointers.pointers);
    }

    @Override
    public @NotNull <T> Builder withDynamic(final @NotNull Pointer<T> pointer, final @NotNull Supplier<@Nullable T> value) {
      this.pointers.put(Objects.requireNonNull(pointer, "pointer"), Objects.requireNonNull(value, "value"));
      return this;
    }

    @Override
    public @NotNull Pointers build() {
      return new PointersImpl(this);
    }
  }
}

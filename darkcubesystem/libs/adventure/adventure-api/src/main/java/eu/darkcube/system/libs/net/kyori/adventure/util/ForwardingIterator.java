/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Supplier;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * An iterable that forwards the {@link #iterator()} and {@link #spliterator()} calls to some {@link Supplier suppliers}.
 *
 * @param <T> the type of the iterable
 * @since 4.9.0
 */
public final class ForwardingIterator<T> implements Iterable<T> {
  private final Supplier<Iterator<T>> iterator;
  private final Supplier<Spliterator<T>> spliterator;

  /**
   * Creates a new forwarding iterable.
   *
   * @param iterator the iterator supplier
   * @param spliterator the spliterator supplier
   * @since 4.9.0
   */
  public ForwardingIterator(final @NotNull Supplier<Iterator<T>> iterator, final @NotNull Supplier<Spliterator<T>> spliterator) {
    this.iterator = Objects.requireNonNull(iterator, "iterator");
    this.spliterator = Objects.requireNonNull(spliterator, "spliterator");
  }

  @Override
  public @NotNull Iterator<T> iterator() {
    return this.iterator.get();
  }

  @Override
  public @NotNull Spliterator<T> spliterator() {
    return this.spliterator.get();
  }
}

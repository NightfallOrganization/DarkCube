/*
 * Copyright (c) 2018-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.examination.string;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import eu.darkcube.system.libs.net.kyori.examination.AbstractExaminer;
import eu.darkcube.system.libs.net.kyori.examination.Examiner;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * {@link Examiner} that outputs a {@link String}.
 *
 * @since 1.0.0
 */
public class StringExaminer extends AbstractExaminer<String> {
  private static final Function<String, String> DEFAULT_ESCAPER = string -> string
    .replace("\"", "\\\"")
    .replace("\\", "\\\\")
    .replace("\b", "\\b")
    .replace("\f", "\\f")
    .replace("\n", "\\n")
    .replace("\r", "\\r")
    .replace("\t", "\\t");
  private static final Collector<CharSequence, ?, String> COMMA_CURLY = Collectors.joining(", ", "{", "}");
  private static final Collector<CharSequence, ?, String> COMMA_SQUARE = Collectors.joining(", ", "[", "]");
  private final Function<String, String> escaper;

  /**
   * Gets a string examiner that escapes simply.
   *
   * @return a string examiner
   * @since 1.0.0
   */
  public static @NotNull StringExaminer simpleEscaping() {
    return Instances.SIMPLE_ESCAPING;
  }

  /**
   * Constructs.
   *
   * @param escaper the string escaper
   * @since 1.0.0
   */
  public StringExaminer(final @NotNull Function<String, String> escaper) {
    this.escaper = escaper;
  }

  @Override
  protected <E> @NotNull String array(final E@NotNull[] array, final @NotNull Stream<String> elements) {
    return elements.collect(COMMA_SQUARE);
  }

  @Override
  protected <E> @NotNull String collection(final @NotNull Collection<E> collection, final @NotNull Stream<String> elements) {
    return elements.collect(COMMA_SQUARE);
  }

  @Override
  protected @NotNull String examinable(final @NotNull String name, final @NotNull Stream<Map.Entry<String, String>> properties) {
    return name + properties.map(property -> property.getKey() + '=' + property.getValue()).collect(COMMA_CURLY);
  }

  @Override
  protected <K, V> @NotNull String map(final @NotNull Map<K, V> map, final @NotNull Stream<Map.Entry<String, String>> entries) {
    return entries.map(entry -> entry.getKey() + '=' + entry.getValue()).collect(COMMA_CURLY);
  }

  @Override
  protected @NotNull String nil() {
    return "null";
  }

  @Override
  protected @NotNull String scalar(final @NotNull Object value) {
    return String.valueOf(value);
  }

  @Override
  public @NotNull String examine(final boolean value) {
    return String.valueOf(value);
  }

  @Override
  public @NotNull String examine(final byte value) {
    return String.valueOf(value);
  }

  @Override
  public @NotNull String examine(final char value) {
    return Strings.wrapIn(this.escaper.apply(String.valueOf(value)), '\'');
  }

  @Override
  public @NotNull String examine(final double value) {
    return Strings.withSuffix(String.valueOf(value), 'd');
  }

  @Override
  public @NotNull String examine(final float value) {
    return Strings.withSuffix(String.valueOf(value), 'f');
  }

  @Override
  public @NotNull String examine(final int value) {
    return String.valueOf(value);
  }

  @Override
  public @NotNull String examine(final long value) {
    return String.valueOf(value);
  }

  @Override
  public @NotNull String examine(final short value) {
    return String.valueOf(value);
  }

  @Override
  protected <T> @NotNull String stream(final @NotNull Stream<T> stream) {
    return stream.map(this::examine).collect(COMMA_SQUARE);
  }

  @Override
  protected @NotNull String stream(final @NotNull DoubleStream stream) {
    return stream.mapToObj(this::examine).collect(COMMA_SQUARE);
  }

  @Override
  protected @NotNull String stream(final @NotNull IntStream stream) {
    return stream.mapToObj(this::examine).collect(COMMA_SQUARE);
  }

  @Override
  protected @NotNull String stream(final @NotNull LongStream stream) {
    return stream.mapToObj(this::examine).collect(COMMA_SQUARE);
  }

  @Override
  public @NotNull String examine(final @Nullable String value) {
    if (value == null) return this.nil();
    return Strings.wrapIn(this.escaper.apply(value), '"');
  }

  @Override
  protected @NotNull String array(final int length, final IntFunction<String> value) {
    final StringBuilder sb = new StringBuilder();
    sb.append('[');
    for (int i = 0; i < length; i++) {
      sb.append(value.apply(i));
      if (i + 1 < length) {
        sb.append(", ");
      }
    }
    sb.append(']');
    return sb.toString();
  }

  private static final class Instances {
    static final StringExaminer SIMPLE_ESCAPING = new StringExaminer(DEFAULT_ESCAPER);
  }
}

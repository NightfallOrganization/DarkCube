/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

final class BookImpl implements Book {
  private final Component title;
  private final Component author;
  private final List<Component> pages;

  BookImpl(final @NotNull Component title, final @NotNull Component author, final @NotNull List<Component> pages) {
    this.title = requireNonNull(title, "title");
    this.author = requireNonNull(author, "author");
    this.pages = Collections.unmodifiableList(requireNonNull(pages, "pages"));
  }

  @Override
  public @NotNull Component title() {
    return this.title;
  }

  @Override
  public @NotNull Book title(final @NotNull Component title) {
    return new BookImpl(requireNonNull(title, "title"), this.author, this.pages);
  }

  @Override
  public @NotNull Component author() {
    return this.author;
  }

  @Override
  public @NotNull Book author(final @NotNull Component author) {
    return new BookImpl(this.title, requireNonNull(author, "author"), this.pages);
  }

  @Override
  public @NotNull List<Component> pages() {
    return this.pages;
  }

  @Override
  public @NotNull Book pages(final @NotNull List<Component> pages) {
    return new BookImpl(this.title, this.author, new ArrayList<>(requireNonNull(pages, "pages")));
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("title", this.title),
      ExaminableProperty.of("author", this.author),
      ExaminableProperty.of("pages", this.pages)
    );
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof BookImpl)) return false;
    final BookImpl that = (BookImpl) o;
    return this.title.equals(that.title)
      && this.author.equals(that.author)
      && this.pages.equals(that.pages);
  }

  @Override
  public int hashCode() {
    int result = this.title.hashCode();
    result = 31 * result + this.author.hashCode();
    result = 31 * result + this.pages.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }

  static final class BuilderImpl implements Book.Builder {
    private Component title = Component.empty();
    private Component author = Component.empty();
    private final List<Component> pages = new ArrayList<>();

    @Override
    public @NotNull Builder title(final @NotNull Component title) {
      this.title = requireNonNull(title, "title");
      return this;
    }

    @Override
    public @NotNull Builder author(final @NotNull Component author) {
      this.author = requireNonNull(author, "author");
      return this;
    }

    @Override
    public @NotNull Builder addPage(final @NotNull Component page) {
      this.pages.add(requireNonNull(page, "page"));
      return this;
    }

    @Override
    public @NotNull Builder pages(final @NotNull Collection<Component> pages) {
      this.pages.addAll(requireNonNull(pages, "pages"));
      return this;
    }

    @Override
    public @NotNull Builder pages(final @NotNull Component@NotNull... pages) {
      Collections.addAll(this.pages, pages);
      return this;
    }

    @Override
    public @NotNull Book build() {
      return new BookImpl(this.title, this.author, new ArrayList<>(this.pages));
    }
  }
}

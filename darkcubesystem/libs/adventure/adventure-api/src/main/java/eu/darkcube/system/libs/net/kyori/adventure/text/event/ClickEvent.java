/*
 * Copyright (c) 2017-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.text.event;

import java.net.URL;
import java.util.Objects;
import java.util.stream.Stream;

import eu.darkcube.system.libs.net.kyori.adventure.internal.Internals;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.StyleBuilderApplicable;
import eu.darkcube.system.libs.net.kyori.adventure.util.Index;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.examination.Examinable;
import eu.darkcube.system.libs.net.kyori.examination.ExaminableProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * A click event.
 *
 * <p>A click event processes an {@link Action} when clicked on.</p>
 *
 * @since 4.0.0
 */
public final class ClickEvent implements Examinable, StyleBuilderApplicable {
  /**
   * Creates a click event that opens a url.
   *
   * @param url the url to open
   * @return a click event
   * @since 4.0.0
   */
  public static @NotNull ClickEvent openUrl(final @NotNull String url) {
    return new ClickEvent(Action.OPEN_URL, url);
  }

  /**
   * Creates a click event that opens a url.
   *
   * @param url the url to open
   * @return a click event
   * @since 4.0.0
   */
  public static @NotNull ClickEvent openUrl(final @NotNull URL url) {
    return openUrl(url.toExternalForm());
  }

  /**
   * Creates a click event that opens a file.
   *
   * <p>This action is not readable, and may only be used locally on the client.</p>
   *
   * @param file the file to open
   * @return a click event
   * @since 4.0.0
   */
  public static @NotNull ClickEvent openFile(final @NotNull String file) {
    return new ClickEvent(Action.OPEN_FILE, file);
  }

  /**
   * Creates a click event that runs a command.
   *
   * @param command the command to run
   * @return a click event
   * @since 4.0.0
   */
  public static @NotNull ClickEvent runCommand(final @NotNull String command) {
    return new ClickEvent(Action.RUN_COMMAND, command);
  }

  /**
   * Creates a click event that suggests a command.
   *
   * @param command the command to suggest
   * @return a click event
   * @since 4.0.0
   */
  public static @NotNull ClickEvent suggestCommand(final @NotNull String command) {
    return new ClickEvent(Action.SUGGEST_COMMAND, command);
  }

  /**
   * Creates a click event that changes to a page.
   *
   * @param page the page to change to
   * @return a click event
   * @since 4.0.0
   */
  public static @NotNull ClickEvent changePage(final @NotNull String page) {
    return new ClickEvent(Action.CHANGE_PAGE, page);
  }

  /**
   * Creates a click event that changes to a page.
   *
   * @param page the page to change to
   * @return a click event
   * @since 4.0.0
   */
  public static @NotNull ClickEvent changePage(final int page) {
    return changePage(String.valueOf(page));
  }

  /**
   * Creates a click event that copies text to the clipboard.
   *
   * @param text the text to copy to the clipboard
   * @return a click event
   * @since 4.0.0
   * @sinceMinecraft 1.15
   */
  public static @NotNull ClickEvent copyToClipboard(final @NotNull String text) {
    return new ClickEvent(Action.COPY_TO_CLIPBOARD, text);
  }

  /**
   * Creates a click event.
   *
   * @param action the action
   * @param value the value
   * @return a click event
   * @since 4.0.0
   */
  public static @NotNull ClickEvent clickEvent(final @NotNull Action action, final @NotNull String value) {
    return new ClickEvent(action, value);
  }

  private final Action action;
  private final String value;

  private ClickEvent(final @NotNull Action action, final @NotNull String value) {
    this.action = requireNonNull(action, "action");
    this.value = requireNonNull(value, "value");
  }

  /**
   * Gets the click event action.
   *
   * @return the click event action
   * @since 4.0.0
   */
  public @NotNull Action action() {
    return this.action;
  }

  /**
   * Gets the click event value.
   *
   * @return the click event value
   * @since 4.0.0
   */
  public @NotNull String value() {
    return this.value;
  }

  @Override
  public void styleApply(final @NotNull Style.Builder style) {
    style.clickEvent(this);
  }

  @Override
  public boolean equals(final @Nullable Object other) {
    if (this == other) return true;
    if (other == null || this.getClass() != other.getClass()) return false;
    final ClickEvent that = (ClickEvent) other;
    return this.action == that.action && Objects.equals(this.value, that.value);
  }

  @Override
  public int hashCode() {
    int result = this.action.hashCode();
    result = (31 * result) + this.value.hashCode();
    return result;
  }

  @Override
  public @NotNull Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
      ExaminableProperty.of("action", this.action),
      ExaminableProperty.of("value", this.value)
    );
  }

  @Override
  public String toString() {
    return Internals.toString(this);
  }

  /**
   * An enumeration of click event actions.
   *
   * @since 4.0.0
   */
  public enum Action {
    /**
     * Opens a url when clicked.
     *
     * @since 4.0.0
     */
    OPEN_URL("open_url", true),
    /**
     * Opens a file when clicked.
     *
     * <p>This action is not readable, and may only be used locally on the client.</p>
     *
     * @since 4.0.0
     */
    OPEN_FILE("open_file", false),
    /**
     * Runs a command when clicked.
     *
     * @since 4.0.0
     */
    RUN_COMMAND("run_command", true),
    /**
     * Suggests a command into the chat box.
     *
     * @since 4.0.0
     */
    SUGGEST_COMMAND("suggest_command", true),
    /**
     * Changes the page of a book.
     *
     * @since 4.0.0
     */
    CHANGE_PAGE("change_page", true),
    /**
     * Copies text to the clipboard.
     *
     * @since 4.0.0
     * @sinceMinecraft 1.15
     */
    COPY_TO_CLIPBOARD("copy_to_clipboard", true);

    /**
     * The name map.
     *
     * @since 4.0.0
     */
    public static final Index<String, Action> NAMES = Index.create(Action.class, constant -> constant.name);
    private final String name;
    /**
     * If this action is readable.
     *
     * <p>When an action is not readable it will not be deserialized.</p>
     */
    private final boolean readable;

    Action(final @NotNull String name, final boolean readable) {
      this.name = name;
      this.readable = readable;
    }

    /**
     * Tests if this action is readable.
     *
     * @return {@code true} if this action is readable, {@code false} if this
     *     action is not readable
     * @since 4.0.0
     */
    public boolean readable() {
      return this.readable;
    }

    @Override
    public @NotNull String toString() {
      return this.name;
    }
  }
}
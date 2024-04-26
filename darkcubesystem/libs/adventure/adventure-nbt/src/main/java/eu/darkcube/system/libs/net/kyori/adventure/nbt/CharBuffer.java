/*
 * Copyright (c) 2017-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.adventure.nbt;

/**
 * A character buffer designed to be inspected by a parser.
 */
final class CharBuffer {
  private final CharSequence sequence;
  private int index;

  CharBuffer(final CharSequence sequence) {
    this.sequence = sequence;
  }

  /**
   * Get the character at the current position.
   *
   * @return The current character
   */
  public char peek() {
    return this.sequence.charAt(this.index);
  }

  public char peek(final int offset) {
    return this.sequence.charAt(this.index + offset);
  }

  /**
   * Get the current character and advance.
   *
   * @return current character
   */
  public char take() {
    return this.sequence.charAt(this.index++);
  }

  public boolean advance() {
    this.index++;
    return this.hasMore();
  }

  public boolean hasMore() {
    return this.index < this.sequence.length();
  }

  public boolean hasMore(final int offset) {
    return this.index + offset < this.sequence.length();
  }

  /**
   * Search for the provided token, and advance the reader index past the {@code until} character.
   *
   * @param until case-insensitive token
   * @return the string starting at the current position (inclusive) and going until the location of {@code until}, exclusive
   * @throws StringTagParseException if {@code until} is not present in the remaining string
   */
  public CharSequence takeUntil(char until) throws StringTagParseException {
    until = Character.toLowerCase(until);
    int endIdx = -1;
    for (int idx = this.index; idx < this.sequence.length(); ++idx) {
      if (this.sequence.charAt(idx) == Tokens.ESCAPE_MARKER) {
        idx++;
      } else if (Character.toLowerCase(this.sequence.charAt(idx)) == until) {
        endIdx = idx;
        break;
      }
    }
    if (endIdx == -1) {
      throw this.makeError("No occurrence of " + until + " was found");
    }

    final CharSequence result = this.sequence.subSequence(this.index, endIdx);
    this.index = endIdx + 1;
    return result;
  }

  /**
   * Assert that the next non-whitespace character is the provided parameter.
   *
   * <p>If the assertion is successful, the token will be consumed.</p>
   *
   * @param expectedChar expected character
   * @return this
   * @throws StringTagParseException if EOF or non-matching value is found
   */
  public CharBuffer expect(final char expectedChar) throws StringTagParseException {
    this.skipWhitespace();
    if (!this.hasMore()) {
      throw this.makeError("Expected character '" + expectedChar + "' but got EOF");
    }
    if (this.peek() != expectedChar) {
      throw this.makeError("Expected character '" + expectedChar + "' but got '" + this.peek() + "'");
    }
    this.take();
    return this;
  }

  /**
   * If the next non-whitespace character is {@code token}, advance past it.
   *
   * <p>This method always consumes whitespace.</p>
   *
   * @param token next non-whitespace character to query
   * @return if the next non-whitespace character is {@code token}
   */
  public boolean takeIf(final char token) {
    this.skipWhitespace();
    if (this.hasMore() && this.peek() == token) {
      this.advance();
      return true;
    }
    return false;
  }

  public CharBuffer skipWhitespace() {
    while (this.hasMore() && Character.isWhitespace(this.peek())) this.advance();
    return this;
  }

  public StringTagParseException makeError(final String message) {
    return new StringTagParseException(message, this.sequence, this.index);
  }
}

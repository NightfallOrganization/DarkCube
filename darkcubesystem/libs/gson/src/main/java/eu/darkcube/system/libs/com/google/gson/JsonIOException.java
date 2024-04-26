/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.com.google.gson;

/**
 * This exception is raised when Gson was unable to read an input stream
 * or write to one.
 * 
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public final class JsonIOException extends JsonParseException {
  private static final long serialVersionUID = 1L;

  public JsonIOException(String msg) {
    super(msg);
  }

  public JsonIOException(String msg, Throwable cause) {
    super(msg, cause);
  }

  /**
   * Creates exception with the specified cause. Consider using
   * {@link #JsonIOException(String, Throwable)} instead if you can describe what happened.
   *
   * @param cause root exception that caused this exception to be thrown.
   */
  public JsonIOException(Throwable cause) {
    super(cause);
  }
}

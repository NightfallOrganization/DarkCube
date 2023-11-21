/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson;

import java.lang.reflect.Field;

/**
 * A mechanism for providing custom field naming in Gson. This allows the client code to translate
 * field names into a particular convention that is not supported as a normal Java field
 * declaration rules. For example, Java does not support "-" characters in a field name.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 * @since 1.3
 */
public interface FieldNamingStrategy {

  /**
   * Translates the field name into its JSON field name representation.
   *
   * @param f the field object that we are translating
   * @return the translated field name.
   * @since 1.3
   */
  public String translateName(Field f);
}
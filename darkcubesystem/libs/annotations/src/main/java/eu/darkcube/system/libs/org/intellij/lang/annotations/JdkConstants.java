/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.org.intellij.lang.annotations;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * @deprecated this class will be removed in future versions to avoid Swing dependency, 
 * which is unnecessary for most of the clients. No IDE or static analysis tool should know 
 * about this class directly, as it creates new annotations using meta-annotations.
 * If it's used in your project, please create similar annotations in your project directly.
 */
@Deprecated
public final class JdkConstants {

  /**
   * Prohibited default constructor.
   */
  private JdkConstants() {
    throw new AssertionError("JdkConstants should not be instantiated");
  }

  @MagicConstant(intValues = {SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.RIGHT, SwingConstants.LEADING, SwingConstants.TRAILING})
  public @interface HorizontalAlignment {}

  @MagicConstant(intValues = {FlowLayout.LEFT, FlowLayout.CENTER, FlowLayout.RIGHT, FlowLayout.LEADING, FlowLayout.TRAILING})
  public @interface FlowLayoutAlignment {}

  @MagicConstant(valuesFromClass = Cursor.class)
  public @interface CursorType {}

  @MagicConstant(flagsFromClass = InputEvent.class)
  public @interface InputEventMask {}

  @MagicConstant(intValues = {Adjustable.HORIZONTAL, Adjustable.VERTICAL})
  public @interface AdjustableOrientation {}

  @MagicConstant(intValues = {
    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
    ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER})
  public @interface VerticalScrollBarPolicy {}

  @MagicConstant(intValues = {
    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED,
    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS,
    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER})
  public @interface HorizontalScrollBarPolicy {}

  @MagicConstant(intValues = {Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER})
  public @interface CalendarMonth {}

  @MagicConstant(flagsFromClass = Pattern.class)
  public @interface PatternFlags {}

  @MagicConstant(valuesFromClass = BoxLayout.class)
  public @interface BoxLayoutAxis {}

  @MagicConstant(valuesFromClass = ListSelectionModel.class)
  public @interface ListSelectionMode{}

  @MagicConstant(valuesFromClass = TreeSelectionModel.class)
  public @interface TreeSelectionMode{}

  @MagicConstant(flags = {Font.PLAIN, Font.BOLD, Font.ITALIC})
  public @interface FontStyle {}

  @MagicConstant(intValues = {TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.LEFT, TitledBorder.CENTER, TitledBorder.RIGHT, TitledBorder.LEADING, TitledBorder.TRAILING})
  public @interface TitledBorderJustification {}

  @MagicConstant(intValues = {TitledBorder.DEFAULT_POSITION, TitledBorder.ABOVE_TOP, TitledBorder.TOP, TitledBorder.BELOW_TOP, TitledBorder.ABOVE_BOTTOM, TitledBorder.BOTTOM, TitledBorder.BELOW_BOTTOM})
  public @interface TitledBorderTitlePosition{}

  @MagicConstant(intValues = {SwingConstants.TOP, SwingConstants.BOTTOM, SwingConstants.LEFT, SwingConstants.RIGHT})
  public @interface TabPlacement{}

  @MagicConstant(intValues = {JTabbedPane.WRAP_TAB_LAYOUT, JTabbedPane.SCROLL_TAB_LAYOUT})
  public @interface TabLayoutPolicy{}
}

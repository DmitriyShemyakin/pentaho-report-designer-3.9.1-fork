/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;

/**
 * An AWT-Based default implementation of an SizeCalculator. This implementation tries to detect the currently used
 * FontRendererContext; some JDKs are unable to return reasonable sizes for the given text.
 *
 * @author Thomas Morgner
 * @see org.pentaho.reporting.engine.classic.core.layout.DefaultSizeCalculator
 */
public strictfp class DefaultSizeCalculator implements SizeCalculator
{
  private static final Log logger = LogFactory.getLog(DefaultSizeCalculator.class);
  private static final boolean VERBOSE_LOGGING = false;

  /**
   * A helper class that is able to detect whether the implementation is considered buggy. A non-buggy implementation
   * should show no differences between aliased-versions of Graphics2D and non-aliased versions.
   * <p/>
   * On JDK 1.4 the font renderer changed. In previous versions, the font renderer was sensitive to fractional metrics,
   * so that fonts were always rendered without FractionalMetrics enabled. Since 1.4, fonts are always rendered with
   * FractionalMetrics disabled.
   * <p/>
   * Obviously this is annoying if you try to write a layouter for all JDKs :(
   */
  public static class BuggyFontRendererDetector
  {
    /**
     * a flag that indicates whether the FontRenderContext implementation is buggy.
     */
    private boolean isBuggyVersion;

    /**
     * a flag that checks whether aliasing is used to draw the contents on Graphics objects.
     */
    private final boolean isAliased;

    /**
     * Cache the created FontRenderContext. FRC is read only.
     */
    private FontRenderContext fontRenderContext;

    /**
     * creates a new BuggyFontRendererDetector.
     */
    protected BuggyFontRendererDetector()
    {
      final ExtendedConfiguration extConfiguration =
          ClassicEngineBoot.getInstance().getExtendedConfig();
      isAliased = extConfiguration.getBoolProperty
          (ClassicEngineCoreModule.FONTRENDERER_USEALIASING_KEY);

      // Another funny thing for the docs: On JDK 1.4 the font renderer changed.
      // in previous versions, the font renderer was sensitive to fractional metrics,
      // so that fonts were always rendered with FractionalMetrics enabled.
      // Since 1.4, fonts are always rendered with FractionalMetrics disabled.

      // On a 1.4 version, the aliasing has no influence on non-fractional metrics
      // aliasing has no influence on any version if fractional metrics are enabled.
      final FontRenderContext frcAlias = new FontRenderContext(null, true, false);
      final FontRenderContext frcNoAlias = new FontRenderContext(null, false, false);
      final Font font = new Font("Serif", Font.PLAIN, 10);
      final String myText = "A simple text with some characters to calculate the length.";

      final double wAlias = font.getStringBounds(myText, 0, myText.length(), frcAlias)
          .getWidth();
      final double wNoAlias =
          font.getStringBounds(myText, 0, myText.length(), frcNoAlias).getWidth();
      isBuggyVersion = (wAlias != wNoAlias);
      final boolean buggyOverride =
          extConfiguration.getBoolProperty(ClassicEngineCoreModule.FONTRENDERER_ISBUGGY_FRC_KEY);

      if (DefaultSizeCalculator.VERBOSE_LOGGING)
      {
        DefaultSizeCalculator.logger.debug("This is a buggy version of the font-renderer context: " + isBuggyVersion);
        DefaultSizeCalculator.logger.debug("The buggy-value is defined in the configuration     : " + buggyOverride);
        if (isBuggyVersion)
        {
          if (isAliased())
          {
            DefaultSizeCalculator.logger.debug("The G2OutputTarget uses Antialiasing. \n"
                + "The FontRendererBugs should not be visible in TextAntiAliasing-Mode.\n"
                + "If there are problems with the string-placement, please report your \n"
                + "Operating System version and your JDK Version to "
                + "www.object-refinery.com/jfreereport.\n");
          }
          else
          {
            DefaultSizeCalculator.logger.debug("The G2OutputTarget does not use Antialiasing. \n"
                + "Your FontRenderer is buggy (text is not displayed correctly by "
                + "default).\n"
                + "The system was able to detect this and tries to correct that bug. \n"
                + "If your strings are not displayed correctly, report your Operating System "
                + "version and your \n"
                + "JDK Version to www.object-refinery.com/jfreereport\n");
          }
        }
        else
        {
          DefaultSizeCalculator.logger.debug(
              "Your FontRenderer seems to be ok, our tests didn't produce buggy results. \n"
                  + "If your strings are not displayed correctly, try to enable the "
                  + "configuration key \n"
                  + "\"org.pentaho.reporting.engine.classic.core.targets.G2OutputTarget.isBuggyFRC=true\"\n"
                  + "in the file 'jfreereport.properties' or set this property as "
                  + "System-property. \n"
                  + "If the bug still remains alive, please report your Operating System version "
                  + "and your \nJDK Version to www.object-refinery.com/jfreereport.\n");
        }
        DefaultSizeCalculator.logger.debug("If text layouting is working as expected, no further action is required.");
      }

      if (buggyOverride == true)
      {
        isBuggyVersion = true;
      }
    }

    /**
     * creates a new FontRenderContext suitable to calculate a string size, independend from the AWT-bug.
     *
     * @return a font render context that is valid and not affected by the bugs.
     */
    protected FontRenderContext createFontRenderContext()
    {
      if (fontRenderContext == null)
      {
        if (isAliased())
        {
          fontRenderContext = new FontRenderContext(null, isAliased(), true);
        }
        else
        {
          // buggy is only important on non-aliased environments ...
          // dont use fractional metrics on buggy versions

          // use int_metrics wenn buggy ...
          fontRenderContext = new FontRenderContext(null, isAliased(), isBuggyVersion() == false);
        }
      }
      return fontRenderContext;
    }

    /**
     * Gets the defined aliasing state for the FontRenderContext and the target Graphics2D.
     *
     * @return the aliasing state.
     */
    public boolean isAliased()
    {
      return isAliased;
    }

    /**
     * Gets the buggy state of the AWT implementation.
     *
     * @return true, if the AWT implementation is buggy and not able to perform accurate font rendering.
     */
    public boolean isBuggyVersion()
    {
      return isBuggyVersion;
    }
  }

  /**
   * the FontRenderContext bug detector instance.
   */
  private static BuggyFontRendererDetector frcDetector;
  private float lineHeight;

  /**
   * Returns a singleon instance of the FontRenderContext bug detector.
   *
   * @return the FontRenderContext-detector
   */
  public static BuggyFontRendererDetector getFrcDetector()
  {
    if (frcDetector == null)
    {
      frcDetector = new BuggyFontRendererDetector();
    }
    return frcDetector;
  }

  /**
   * The font.
   */
  private Font font;

  private char[] chars;

  /**
   * Creates a new size calculator.
   *
   * @param font              The font definition.
   * @param maxLineHeightUsed a flag indicating whether the maximum bounding box is used.
   * @return A default size calculator.
   * @deprecated Do not use the FontDefinition, use the Font-constructor instead and instantiate the size-calculator
   *             directly.
   */
  public static synchronized DefaultSizeCalculator getDefaultSizeCalculator
      (final FontDefinition font, final boolean maxLineHeightUsed)
  {
    return new DefaultSizeCalculator(font.getFont(), maxLineHeightUsed);
  }


  /**
   * Creates a new size calculator.
   *
   * @param font              the font
   * @param maxLineHeightUsed a flag indicating whether the maximum bounding box is used.
   * @deprecated Do not use the FontDefinition, use the Font-constructor instead.
   */
  public DefaultSizeCalculator(final FontDefinition font,
                               final boolean maxLineHeightUsed)
  {
    this(font.getFont(), maxLineHeightUsed);
  }

  /**
   * Creates a new size calculator.
   *
   * @param font              the font.
   * @param maxLineHeightUsed a flag indicating whether the maximum bounding box is used.
   */
  public DefaultSizeCalculator(final Font font,
                               final boolean maxLineHeightUsed)
  {
    if (font == null)
    {
      throw new NullPointerException("Given FontDefinition is null");
    }
    if (font.getSize2D() <= 0)
    {
      throw new IllegalArgumentException("The given FontSize is <= 0");
    }

    if (maxLineHeightUsed)
    {
      final Rectangle2D rect = font.getMaxCharBounds(DefaultSizeCalculator.getFrcDetector().createFontRenderContext());
      this.lineHeight = (float) rect.getHeight();
    }
    else
    {
      this.lineHeight = font.getSize2D();
    }
    // Log.debug ("FontSize: " + rect + " -> " + font.getFont().getSize2D() + " vs " + lineHeight + " -> " + font.getFontName());
    this.font = font;
    this.chars = new char[100];
  }

  /**
   * Returns the height of the current font. The font height specifies the distance between 2 base lines.
   *
   * @return the font height.
   */
  public float getLineHeight()
  {
    return lineHeight;
  }

  /**
   * Calculates the width of the specified String in the current Graphics context.
   *
   * @param text         the text to be weighted.
   * @param lineStartPos the start position of the substring to be weighted.
   * @param endPos       the position of the last characterto be included in the weightening process.
   * @return the width of the given string in 1/72" dpi.
   */
  public float getStringWidth(final String text, final int lineStartPos,
                              final int endPos)
  {
    if (lineStartPos < 0)
    {
      throw new IllegalArgumentException();
    }
    if (lineStartPos > endPos)
    {
      throw new IllegalArgumentException("LineStart on: " + lineStartPos + " End on " + endPos);
    }

    if (lineStartPos == endPos)
    {
      return 0;
    }

    final FontRenderContext frc = DefaultSizeCalculator.getFrcDetector().createFontRenderContext();

    if (chars.length < text.length())
    {
      chars = new char[Math.max(chars.length + 100, text.length())];
    }

    text.getChars(lineStartPos, endPos, chars, 0);
    final Rectangle2D textBounds2 = font.getStringBounds(chars, 0, endPos - lineStartPos, frc);
    return (float) textBounds2.getWidth();
  }

  /**
   * Converts this object to a string.
   *
   * @return a string.
   */
  public String toString()
  {
    return "DefaultSizeCalculator={font=" + font + '}';
  }
}

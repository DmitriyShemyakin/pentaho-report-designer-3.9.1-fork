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

package org.pentaho.reporting.engine.classic.testcases.layout;

import junit.framework.TestCase;

import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.manual.Utf16LE;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.layout.text.DefaultRenderableTextFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementDefaultStyleSheet;

/**
 * Creation-Date: 06.05.2007, 18:03:57
 *
 * @deprecated covered in other tests
 */
public class TextTest extends TestCase
{
  private static CodePointBuffer buffer;

  public static void testText()
  {
    final DefaultRenderableTextFactory textFactory = new DefaultRenderableTextFactory
        (new HtmlOutputProcessorMetaData(ClassicEngineBoot.getInstance().getGlobalConfig(),
            HtmlOutputProcessorMetaData.PAGINATION_NONE));
    textFactory.startText();

    buffer = Utf16LE.getInstance().decodeString("Test\n\n\nTest", buffer); //$NON-NLS-1$
    final int[] data = buffer.getBuffer();

    final int length = buffer.getLength();
    final ElementDefaultStyleSheet defaultStyle = ElementDefaultStyleSheet.getDefaultStyle();
//todo: After performance design ..    
//    final RenderNode[] renderNodes = textFactory.createText
//        (data, 0, length, defaultStyle, RenderNode.HORIZONTAL_AXIS, RenderNode.VERTICAL_AXIS,
//            LegacyType.INSTANCE, ReportAttributeMap.EMPTY_MAP);
//    final RenderNode[] finishNodes = textFactory.finishText();
//    assertNotNull(renderNodes);
//    assertNotNull(finishNodes);
  }
}

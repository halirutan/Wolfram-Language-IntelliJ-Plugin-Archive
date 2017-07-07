/*
 * Copyright (c) 2017 Patrick Scheibe
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.halirutan.mathematica.util;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Provides the files names for the icons used by the plugin. Moreover, and that is important, IDEA will look for additional
 * icon files to give a better look in the Darcula scheme and on retina displays. This means, for each icon listed here
 * there is additionally
 * <ul>
 *   <li>a file icon_dark.png for the Darcula theme</li>
 *   <li>a file icon@2x.png for high-res retina displays</li>
 *   <li>a file icon@2x_dark.png for high-res retina displays using the Darcula theme</li>
 * </ul>
 *
 * @author patrick (4/4/13)
 */
public interface MathematicaIcons {

  Icon FILE_ICON = IconLoader.getIcon("/icons/spikey.png");
  Icon SET_ICON = IconLoader.getIcon("/icons/Set.png");
  Icon SET_DELAYED_ICON = IconLoader.getIcon("/icons/SetDelayed.png");
  Icon TAGSET_ICON = IconLoader.getIcon("/icons/TagSet.png");
  Icon TAGSET_DELAYED_ICON = IconLoader.getIcon("/icons/TagSetDelayed.png");
  Icon UPSET_ICON = IconLoader.getIcon("/icons/UpSet.png");
  Icon UPSETDELAYED_ICON = IconLoader.getIcon("/icons/UpSetDelayed.png");
  Icon OPTIONS_ICON = IconLoader.getIcon("/icons/Options.png");
  Icon ATTRIBUTES_ICON = IconLoader.getIcon("/icons/Attributes.png");
  Icon DEFAULT_VALUES_ICON = IconLoader.getIcon("/icons/Default.png");
  Icon FORMAT_VALUES_ICON = IconLoader.getIcon("/icons/FormatValues.png");
  Icon MESSAGES_ICON = IconLoader.getIcon("/icons/Message.png");
  Icon N_VALUES_ICON = IconLoader.getIcon("/icons/NValues.png");
  Icon SYNTAX_INFORMATION_ICON = IconLoader.getIcon("/icons/SyntaxInformation.png");
  Icon GROUP_BY_NAME_ICON = IconLoader.getIcon("/icons/GroupByName.png");
  Icon GROUP_BY_TYPE_ICON = IconLoader.getIcon("/icons/GroupByType.png");
  Icon SORT_BY_TYPE_APPEARANCE = IconLoader.getIcon("/icons/SortByAppearance.png");


}

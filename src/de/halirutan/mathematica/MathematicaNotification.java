/*
 * Copyright (c) 2018 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.halirutan.mathematica;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import de.halirutan.mathematica.util.MathematicaIcons;

/**
 * @author patrick (06.05.18).
 */
public class MathematicaNotification {
  private static final NotificationGroup GROUP = new NotificationGroup(
      MathematicaBundle.message("mathematica.notification.group"),
      NotificationDisplayType.BALLOON,
      false,
      null,
      MathematicaIcons.FILE_ICON);

  public static void info(String message) {
    showNotification(message, NotificationType.INFORMATION);
  }

  public static void warning(String message) {
    showNotification(message, NotificationType.WARNING);
  }

  public static void error(String message) {
    showNotification(message, NotificationType.ERROR);
  }

  private static void showNotification(String message, NotificationType type) {
    GROUP.createNotification(
        MathematicaBundle.message("language.name"),
        message,
        type,
        null).notify(null);
  }

}

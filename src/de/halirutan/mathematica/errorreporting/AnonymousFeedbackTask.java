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

package de.halirutan.mathematica.errorreporting;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task.Backgroundable;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;


/**
 * Sends crash reports to Github. Extensively inspired by the one used in the Android Studio.
 * https://android.googlesource.com/platform/tools/adt/idea/+/master/android/src/com/android/tools/idea/diagnostics/error/ErrorReporter.java
 * As per answer from here: http://devnet.jetbrains.com/message/5526206;jsessionid=F5422B4AF1AFD05AAF032636E5455E90#5526206
 */
public class AnonymousFeedbackTask extends Backgroundable {
  private final Consumer<String> myCallback;
  private final Consumer<Exception> myErrorCallback;
  private final LinkedHashMap<String, String> myParams;

  AnonymousFeedbackTask(@Nullable Project project,
                        @NotNull String title,
                        boolean canBeCancelled,
                        LinkedHashMap<String, String> params,
                        final Consumer<String> callback,
                        final Consumer<Exception> errorCallback) {
    super(project, title, canBeCancelled);

    myParams = params;
    myCallback = callback;
    myErrorCallback = errorCallback;
  }

  @Override
  public void run(@NotNull ProgressIndicator indicator) {
    indicator.setIndeterminate(true);
    AnonymousFeedback feedback = new AnonymousFeedback();
    try {
      String token = feedback.sendFeedback(myParams);
      myCallback.consume(token);
    } catch (Exception e) {
      myErrorCallback.consume(e);
    }
  }
}

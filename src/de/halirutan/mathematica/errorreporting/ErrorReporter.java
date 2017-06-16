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

import com.intellij.diagnostic.IdeErrorsDialog;
import com.intellij.diagnostic.LogMessageEx;
import com.intellij.diagnostic.ReportMessages;
import com.intellij.errorreport.bean.ErrorBean;
import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.idea.IdeaLogger;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.diagnostic.SubmittedReportInfo.SubmissionStatus;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.LinkedHashMap;

/**
 * Sends crash reports to Github. Extensively inspired by the one used in the Android Studio.
 * https://android.googlesource.com/platform/tools/adt/idea/+/master/android/src/com/android/tools/idea/diagnostics/error/ErrorReporter.java
 * As per answer from here: http://devnet.jetbrains.com/message/5526206;jsessionid=F5422B4AF1AFD05AAF032636E5455E90#5526206
 */
public class ErrorReporter extends ErrorReportSubmitter {
  @NotNull
  @Override
  public String getReportActionText() {
    return ErrorReportBundle.message("report.error.to.plugin.vendor");
  }

  @Override
  public boolean submit(@NotNull IdeaLoggingEvent[] events, String additionalInfo, @NotNull Component parentComponent, @NotNull Consumer<SubmittedReportInfo> consumer) {
    ErrorBean errorBean = new ErrorBean(events[0].getThrowable(), IdeaLogger.ourLastActionId);
    return doSubmit(events[0], parentComponent, consumer, errorBean, additionalInfo);
  }

  @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
  private static boolean doSubmit(final IdeaLoggingEvent event,
                                  final Component parentComponent,
                                  final Consumer<SubmittedReportInfo> callback,
                                  final ErrorBean bean,
                                  final String description) {
    final DataContext dataContext = DataManager.getInstance().getDataContext(parentComponent);

    bean.setDescription(description);
    bean.setMessage(event.getMessage());

    Throwable throwable = event.getThrowable();
    if (throwable != null) {
      final PluginId pluginId = IdeErrorsDialog.findPluginId(throwable);
      if (pluginId != null) {
        final IdeaPluginDescriptor ideaPluginDescriptor = PluginManager.getPlugin(pluginId);
        if (ideaPluginDescriptor != null && !ideaPluginDescriptor.isBundled()) {
          bean.setPluginName(ideaPluginDescriptor.getName());
          bean.setPluginVersion(ideaPluginDescriptor.getVersion());
        }
      }
    }

    Object data = event.getData();

    if (data instanceof LogMessageEx) {
      bean.setAttachments(((LogMessageEx) data).getIncludedAttachments());
    }

    LinkedHashMap<String, String> reportValues = IdeaITNProxy
        .getKeyValuePairs(bean,
            ApplicationManager.getApplication(),
            (ApplicationInfoEx) ApplicationInfo.getInstance(),
            ApplicationNamesInfo.getInstance());

    final Project project = CommonDataKeys.PROJECT.getData(dataContext);

    Consumer<String> successCallback = token -> {
      final SubmittedReportInfo reportInfo = new SubmittedReportInfo(
          null, "Issue " + token, SubmissionStatus.NEW_ISSUE);
      callback.consume(reportInfo);

      ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT,
          "Submitted",
          NotificationType.INFORMATION,
          null).setImportant(false).notify(project);
    };

    Consumer<Exception> errorCallback = e -> {
      String message = e.getMessage();
      ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT,
          message,
          NotificationType.ERROR,
          NotificationListener.URL_OPENING_LISTENER).setImportant(false).notify(project);
    };
    AnonymousFeedbackTask task =
        new AnonymousFeedbackTask(project, ErrorReportBundle.message("progress.dialog.text"), true, reportValues, successCallback, errorCallback);
    if (project == null) {
      task.run(new EmptyProgressIndicator());
    } else {
      ProgressManager.getInstance().run(task);
    }
    return true;
  }
}
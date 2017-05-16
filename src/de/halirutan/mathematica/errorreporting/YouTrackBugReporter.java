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

import com.intellij.diagnostic.DiagnosticBundle;
import com.intellij.diagnostic.IdeErrorsDialog;
import com.intellij.diagnostic.ReportMessages;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.diagnostic.SubmittedReportInfo.SubmissionStatus;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.intellij.openapi.diagnostic.SubmittedReportInfo.SubmissionStatus.*;
import static com.intellij.openapi.util.text.StringUtil.isEmpty;

/**
 * Created by IntelliJ IDEA. User: Jon S Akhtar Date: Oct 19, 2010 Time: 11:35:35 AM
 */
public class YouTrackBugReporter extends ErrorReportSubmitter {
  private static final Logger log = Logger.getInstance(YouTrackBugReporter.class.getName());
  private static final String DESCRIPTION = "Description";
  private static final String PROJECT = "Project";
  private static final String AREA = "Area";
  @NonNls
  private static final String SERVER_URL = "http://halirutan.myjetbrains.com/youtrack/";
  private static final String SERVER_REST_URL = SERVER_URL + "rest/";
  private static final String SERVER_ISSUE_URL = SERVER_REST_URL + "issue";
  private static final String LOGIN_URL = SERVER_REST_URL + "user/login";
  private final CookieManager myCookieManager = new CookieManager();
  private String myDescription = null;
  private String myExtraInformation = "";
  private String myAffectedVersion = null;

  @NotNull
  @Override
  public String getReportActionText() {
    return "Report to halirutan";
  }


  @Override
  public boolean submit(@NotNull final IdeaLoggingEvent[] events, @Nullable final String additionalInfo, @NotNull final Component parentComponent, @NotNull final Consumer<SubmittedReportInfo> consumer) {
    String name = "<anonymous>";
    submit(events, additionalInfo, name, parentComponent);
    return true;
  }


  private SubmittedReportInfo submit(IdeaLoggingEvent[] ideaLoggingEvents, String description, String user,
                                     Component component) {
    final DataContext dataContext = DataManager.getInstance().getDataContext(component);
    final Project project = PlatformDataKeys.PROJECT.getData(dataContext);
    final IdeaLoggingEvent ideaLoggingEvent = ideaLoggingEvents[0];
    final String throwableText = ideaLoggingEvent.getThrowableText();
    this.myDescription = throwableText.substring(0, Math.min(Math.max(80, throwableText.length()), 80));


    @SuppressWarnings("ThrowableResultOfMethodCallIgnored") Integer signature = ideaLoggingEvent.getThrowable()
        .getStackTrace()[0].hashCode();

    String existing = findExisting(signature);
    if (existing != null) {
      final SubmittedReportInfo reportInfo = new SubmittedReportInfo(SERVER_URL + "issue/" + existing,
          existing, DUPLICATE);
      popupResultInfo(reportInfo, project);
      return reportInfo;
    }


    @NonNls StringBuilder descBuilder = new StringBuilder();

    String platformBuild = ApplicationInfo.getInstance().getBuild().asString();

    descBuilder.append("Platform Version: ").append(platformBuild).append('\n');

    Throwable t = ideaLoggingEvent.getThrowable();
    if (t != null) {
      final PluginId pluginId = IdeErrorsDialog.findPluginId(t);
      if (pluginId != null) {
        final IdeaPluginDescriptor ideaPluginDescriptor = PluginManager.getPlugin(pluginId);
        if (ideaPluginDescriptor != null && !ideaPluginDescriptor.isBundled()) {
          descBuilder.append("Plugin ").append(ideaPluginDescriptor.getName()).append(" version: ").append
              (ideaPluginDescriptor.getVersion()).append("\n");
          this.myAffectedVersion = ideaPluginDescriptor.getVersion();
        }
      }
    }

    if (description == null) {
      description = "<none>";
    }

    descBuilder.append("\n\nDescription: ").append(description);

    for (IdeaLoggingEvent e : ideaLoggingEvents) {
      descBuilder.append("\n\n").append(e.toString());
    }

    this.myExtraInformation = descBuilder.toString();

    String result = submit();
    log.info("Error submitted, response: " + result);

    if (result == null) {
      return new SubmittedReportInfo(SERVER_ISSUE_URL, "", FAILED);
    }

    String resultString = null;
    try {
      Pattern regex = Pattern.compile("id=\"([^\"]+)\"", Pattern.DOTALL | Pattern.MULTILINE);
      Matcher regexMatcher = regex.matcher(result);
      if (regexMatcher.find()) {
        resultString = regexMatcher.group(1);
      }
    } catch (PatternSyntaxException ex) {
      // Syntax error in the regular expression
    }

    if (resultString == null) {
      return new SubmittedReportInfo(SERVER_ISSUE_URL, "", FAILED);
    }


    final SubmittedReportInfo reportInfo = new SubmittedReportInfo(SERVER_URL + "issue/" + resultString,
        resultString, NEW_ISSUE);




    /* Now try to set the auto-submit user using a custom command */
    if (user != null) {
      runCommand(resultString, "Autosubmit User " + user);
    }

    if (signature != 0) {
      runCommand(resultString, "Exception Signature " + signature);
    }

    popupResultInfo(reportInfo, project);

    return reportInfo;
  }

  private String submit() {
    if (isEmpty(this.myDescription)) {
      throw new RuntimeException(DESCRIPTION);
    }
    String project = "MMAP";
    if (isEmpty(project)) {
      throw new RuntimeException(PROJECT);
    }
    String area = "Main";
    if (isEmpty(area)) {
      throw new RuntimeException(AREA);
    }

    String response = "";

    //Create Post String
    String data;
    try {
      // Log-In
      String userName = "autosubmit";
      data = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8");
      data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode("root", "UTF-8");
      // Send Data To Page
      URL url = new URL(LOGIN_URL);
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
      wr.write(data);
      wr.flush();


      // Get The Login Cookie
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;

      while ((line = rd.readLine()) != null) {
        response += line;
      }

      log.info(response);
      // myCookieManager.storeCookies(conn);

      // project=TST&assignee=beto&summary=new issue&myDescription=myDescription of new issue
      // #&priority=show-stopper&type=feature&subsystem=UI&state=Reopened&affectsVersion=2.0,
      // 2.0.1&fixedVersions=2.0&fixedInBuild=2.0.1
      // POST /rest/issue?{project}&{assignee}&{summary}&{myDescription}&{priority}&{type}&{subsystem}&{state
      // }&{affectsVersion}&{fixedVersions}&{attachments}&{fixedInBuild}

      // Make the myDescription 1 line
      this.myDescription = this.myDescription.replaceAll("[\r\n]", "");

      // build the static post data for this issue
      data = URLEncoder.encode("project", "UTF-8") + "=" + URLEncoder.encode(project, "UTF-8");
      data += "&" + URLEncoder.encode("assignee", "UTF-8") + "=" + URLEncoder.encode("Unassigned", "UTF-8");
      data += "&" + URLEncoder.encode("summary", "UTF-8") + "=" + URLEncoder.encode(myDescription, "UTF-8");
      data += "&" + URLEncoder.encode("description", "UTF-8") + "=" +
          URLEncoder.encode(myExtraInformation, "UTF-8");
      data += "&" + URLEncoder.encode("priority", "UTF-8") + "=" + URLEncoder.encode("4", "UTF-8");
      data += "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("Exception", "UTF-8");

      if (this.myAffectedVersion != null) {
        data += "&" + URLEncoder.encode("affectsVersion", "UTF-8") + "=" +
            URLEncoder.encode(this.myAffectedVersion, "UTF-8");
      }


      // We will use \n exclusively
      data = data.replaceAll("\r", "");


      // Send Data To Page
      url = new URL(SERVER_ISSUE_URL);
      conn = url.openConnection();

      conn.setDoOutput(true);
     // myCookieManager.setCookies(conn); // Use the login from earlier

      wr = new OutputStreamWriter(conn.getOutputStream());
      wr.write(data);
      wr.flush();

      // Get The Response
      rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      while ((line = rd.readLine()) != null) {
        response += line;
      }

    } catch (Exception e) {
      e.printStackTrace();
      log.info("Error creating issue", e);
    }

    return response;
  }

  //http://sylvanaar.myjetbrains.com/youtrack/rest/issue?filter=Exception%20Signature%3A801961033
  @Nullable
  private String findExisting(Integer signature) {
    try {
      log.debug(String.format("Run Query for signature <%s>", signature.toString()));
      URL url = new URL(String.format("%s?filter=Exception%%20Signature%%3A%s&with=id", SERVER_ISSUE_URL,
          signature.toString()));

      URLConnection conn = getUrlConnectionAndLogin(url);

      // Get The Response
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      StringBuilder response = new StringBuilder(500);
      while ((line = rd.readLine()) != null) {
        response.append(line);
      }

      // <?xml version="1.0" encoding="UTF-8" standalone="yes"?><issueCompacts><issue
      // id="IDLua-1293"/></issueCompacts>
      log.debug(response.toString());

      String resultString = null;
      try {
        Pattern regex = Pattern.compile("<issue id=\"([^\"]+)\"/>", Pattern.MULTILINE);
        Matcher regexMatcher = regex.matcher(response.toString());
        if (regexMatcher.find()) {
          resultString = regexMatcher.group(1);
        }
      } catch (PatternSyntaxException ex) {
        // Syntax error in the regular expression
      }

      if (resultString != null) {
        log.debug("could be duplicate of " + resultString);
        return resultString;
      }

    } catch (IOException e) {
      log.info("Query Failed", e);
    }

    return null;
  }

  private URLConnection getUrlConnectionAndLogin(URL url) throws IOException {
    URLConnection conn = url.openConnection();
    conn.setDoOutput(true);
    myCookieManager.setCookies(conn);
    return conn;
  }

  // POST /rest/issue/{issue}/execute?{command}&{comment}&{group}&{disableNotifications}&{runAs}
  private void runCommand(String issueID, String command) {
    try {
      log.debug(String.format("Run Command <%s> on issue <%s>", command, issueID));
      URL url = new URL(SERVER_ISSUE_URL + "/" + issueID + "/execute");

      URLConnection conn = getUrlConnectionAndLogin(url);

      String data = URLEncoder.encode("command", "UTF-8") + "=" + URLEncoder.encode(command, "UTF-8");
      data += "&" + URLEncoder.encode("disableNotifications", "UTF-8") + "=" + URLEncoder.encode("true", "UTF-8");

      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
      wr.write(data);
      wr.flush();


      // Get The Response
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      String response = "";
      while ((line = rd.readLine()) != null) {
        response += line;
      }

      log.debug(response);

    } catch (IOException e) {
      log.info("Command Failed", e);
    }
  }


  private void popupResultInfo(final SubmittedReportInfo reportInfo, final Project project) {
    //noinspection OverlyComplexAnonymousInnerClass
    ApplicationManager.getApplication().invokeLater(() -> {
      StringBuilder text = new StringBuilder("<html>");

      final String url = reportInfo.getURL();
      IdeErrorsDialog.appendSubmissionInformation(reportInfo, text);
      text.append(".");
      final SubmissionStatus status = reportInfo.getStatus();
      if (status == NEW_ISSUE) {
        text.append("<br/>").append(DiagnosticBundle.message("error.report.gratitude"));
      } else if (status == DUPLICATE) {
        text.append("<br/>Possible duplicate report");
      }
      text.append("</html>");
      NotificationType type;
      if (status == FAILED) {
        type = NotificationType.ERROR;
      } else if (status == DUPLICATE) {
        type = NotificationType.WARNING;
      } else {
        type = NotificationType.INFORMATION;
      }
      NotificationListener listener = url != null ? (notification, event) -> {
        BrowserUtil.browse(url);
        notification.expire();
      } : null;
      ReportMessages.GROUP.createNotification(ReportMessages.ERROR_REPORT, text.toString(), type,
          listener).notify(project);
    });
  }
}

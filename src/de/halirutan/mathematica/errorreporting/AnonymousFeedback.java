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

import com.google.gson.Gson;
import com.intellij.ide.plugins.IdeaPluginDescriptorImpl;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.*;
import com.jcabi.http.wire.RetryWire;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

@Loggable(Loggable.DEBUG)
class AnonymousFeedback {

  private final static String gitUser = "Mathematica-IntelliJ-Plugin";
  private final static String gitAccessToken = "4a78410ba07788116772376360a55e25263e7cec";
  private final static String gitRepo = "Mathematica-IntelliJ-Plugin";

  private Github myGitHub;
  private Repo myRepository;
  private Issues myIssues;

  public AnonymousFeedback() {
    myGitHub = new RtGithub(new RtGithub(gitAccessToken).entry().through(RetryWire.class));
    myRepository = myGitHub.repos().get(new Coordinates.Simple(gitUser, gitRepo));
    myIssues = myRepository.issues();
  }

  public int findDuplicate(@NotNull final String titel) {
    final HashMap<String, String> filter = new HashMap<>();
    filter.put("filter", "all");
    for (Issue issue : myIssues.iterate(filter)) {
      System.out.println(issue);
    }
    return 0;
  }

  public String sendFeedback(LinkedHashMap<String, String> environmentDetails) {
    String errorMessage = environmentDetails.get("error.message");
    if (errorMessage == null || errorMessage.isEmpty()) {
      errorMessage = "Unspecified error";
    }

    final String body = generateGithubIssueBody(environmentDetails);
    try {
      final Issue newIssue = myIssues.create(ErrorReportBundle.message("issue.title", errorMessage), body);
      final int issueNumber = newIssue.number();
      final Repo issueRepo = newIssue.repo();
      return "Created issue #" + issueNumber + " on " + issueRepo;
    } catch (IOException e) {
      return "Failed to create issue on GitHub";
    }
  }

  static String sendFeedback(
      HttpConnectionFactory httpConnectFactory,
      LinkedHashMap<String, String> environmentDetails) throws IOException {

    sendFeedback(httpConnectFactory, convertToGitHubIssueFormat(environmentDetails));

    return Long.toString(System.currentTimeMillis());
  }

  private static byte[] convertToGitHubIssueFormat(LinkedHashMap<String, String> environmentDetails) {
    LinkedHashMap<String, String> result = new LinkedHashMap<>(5);

    String errorMessage = environmentDetails.get("error.message");
    if (errorMessage == null || errorMessage.isEmpty()) {
      errorMessage = "Unspecified error";
    }
    environmentDetails.remove("error.message");

    result.put("title", ErrorReportBundle.message("issue.title", errorMessage));
    result.put("label", ErrorReportBundle.message("issue.label"));
    result.put("body", generateGithubIssueBody(environmentDetails));

    return ((new Gson()).toJson(result)).getBytes(Charset.forName("UTF-8"));
  }

  private static String generateGithubIssueBody(LinkedHashMap<String, String> body) {
    String errorDescription = body.get("error.description");
    if (errorDescription == null) {
      errorDescription = "";
    }
    body.remove("error.description");

    String stackTrace = body.get("error.stacktrace");
    if (stackTrace == null || stackTrace.isEmpty()) {
      stackTrace = "invalid stacktrace";
    }
    body.remove("error.stacktrace");

    StringBuilder result = new StringBuilder();

    if (!errorDescription.isEmpty()) {
      result.append(errorDescription);
      result.append("\n\n");
    }

    for (Entry<String, String> entry : body.entrySet()) {
      result.append(entry.getKey());
      result.append(": ");
      result.append(entry.getValue());
      result.append("\n");
    }

    result.append("\n```\n");
    result.append(stackTrace);
    result.append("\n```\n");

    return result.toString();
  }

  private static void sendFeedback(HttpConnectionFactory httpConnectFactory, byte[] payload) throws IOException {
    String url = "https://api.github.com/repos/halirutan/Mathematica-IntelliJ-Plugin/issues?access_token=3d23871d02daa221d2270b0df18196ba821628f5";
    String userAgent = "Mathematica IntelliJ IDEA plugin";

    IdeaPluginDescriptorImpl pluginDescriptor = (IdeaPluginDescriptorImpl) PluginManager.getPlugin(PluginId.getId("de.halirutan.mathematica"));
    if (pluginDescriptor != null) {
      String name = pluginDescriptor.getName();
      String version = pluginDescriptor.getVersion();
      userAgent = name + " (" + version + ")";
    }

    HttpURLConnection httpURLConnection = connect(httpConnectFactory, url);
    httpURLConnection.setDoOutput(true);
    httpURLConnection.setRequestMethod("POST");
    httpURLConnection.setRequestProperty("User-Agent", userAgent);
    httpURLConnection.setRequestProperty("Content-Type", "application/json");

    try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
      outputStream.write(payload);
    }

    int responseCode = httpURLConnection.getResponseCode();
    if (responseCode != 201) {
      throw new RuntimeException("Expected HTTP_CREATED (201), obtained " + responseCode);
    }
  }

  private static HttpURLConnection connect(HttpConnectionFactory httpConnectFactory, String url) throws IOException {
    HttpURLConnection httpURLConnection = httpConnectFactory.openHttpConnection(url);
    httpURLConnection.setConnectTimeout(5000);
    httpURLConnection.setReadTimeout(5000);
    return httpURLConnection;
  }

  public static class HttpConnectionFactory {
    HttpConnectionFactory() {
    }

    protected HttpURLConnection openHttpConnection(String url) throws IOException {
      return (HttpURLConnection) ((new URL(url)).openConnection());
    }
  }
}

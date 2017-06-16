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

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

class AnonymousFeedback {

  private final static String gitAccessToken = "097a2a4e4a94ff65a73508083da690d4565fd038";
  private final static String gitRepoUser = "halirutan";
  private final static String gitRepo = "Mathematica-IntelliJ-Plugin";

  private GitHubClient myGitHub;
  private RepositoryId myRepoID;

  AnonymousFeedback() {
    myGitHub = new GitHubClient();
    myGitHub.setOAuth2Token(gitAccessToken);
    myRepoID = new RepositoryId(gitRepoUser, gitRepo);
  }
//
//  public int findDuplicate(@NotNull final String titel) {
//    final HashMap<String, String> filter = new HashMap<>();
//    filter.put("filter", "all");
//    for (Issue issue : myIssues.iterate(filter)) {
//      System.out.println(issue);
//    }
//    return 0;
//  }

  String sendFeedback(LinkedHashMap<String, String> environmentDetails) throws IOException {
    String errorMessage = environmentDetails.get("error.message");
    if (errorMessage == null || errorMessage.isEmpty()) {
      errorMessage = "Unspecified error";
    }
    environmentDetails.remove("error.message");

    final String body = generateGitHubIssueBody(environmentDetails);
    try {
      final Issue newIssue = new Issue();
      newIssue.setTitle(ErrorReportBundle.message("issue.title", errorMessage));
      newIssue.setBody(body);
      Label label = new Label();
      label.setName("auto-generated");
      newIssue.setLabels(Collections.singletonList(label));
      IssueService issueService = new IssueService(myGitHub);
      final Issue issue = issueService.createIssue(myRepoID, newIssue);
      final long id = issue.getNumber();
      return "<a href=\"" + issue.getHtmlUrl() + "\">Created issue #" + id + "</a>";
    } catch (IOException e) {
      throw new IOException("Failed to create issue on GitHub");
    }
  }

  private static String generateGitHubIssueBody(LinkedHashMap<String, String> details) {
    String errorDescription = details.get("error.description");
    if (errorDescription == null) {
      errorDescription = "";
    }
    details.remove("error.description");

    String errorHash = details.get("error.hash");
    if (errorHash == null) {
      errorHash = "";
    }
    details.remove("error.hash");

    String stackTrace = details.get("error.stacktrace");
    if (stackTrace == null || stackTrace.isEmpty()) {
      stackTrace = "invalid stacktrace";
    }
    details.remove("error.stacktrace");

    StringBuilder result = new StringBuilder();

    if (!errorDescription.isEmpty()) {
      result.append(errorDescription);
      result.append("\n\n----------------------\n\n");
    }

    for (Entry<String, String> entry : details.entrySet()) {
      result.append("- ");
      result.append(entry.getKey());
      result.append(": ");
      result.append(entry.getValue());
      result.append("\n");
    }

    result.append("\n```\n");
    result.append(stackTrace);
    result.append("\n```\n");

    result.append("Hash Code:\n").append(errorHash);

    return result.toString();
  }
}

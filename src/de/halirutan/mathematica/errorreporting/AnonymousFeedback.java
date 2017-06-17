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

import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.diagnostic.SubmittedReportInfo.SubmissionStatus;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.IssueService;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

class AnonymousFeedback {

  private final static String gitAccessToken = "097a2a4e4a94ff65a73508083da690d4565fd038";
  private final static String gitRepoUser = "Mathematica-IntelliJ-Plugin";
  private final static String gitRepo = "Auto-generated-issues-for-the-Mathematica-Plugin";

  private final static String issueLabel = "auto-generated";

  private AnonymousFeedback() { }

  static SubmittedReportInfo sendFeedback(LinkedHashMap<String, String> environmentDetails) {

    final SubmittedReportInfo result;
    try {
      GitHubClient client = new GitHubClient();
      client.setOAuth2Token(gitAccessToken);
      RepositoryId repoID = new RepositoryId(gitRepoUser, gitRepo);
      IssueService issueService = new IssueService(client);

      String errorDescription = environmentDetails.get("error.description");

      Issue newGibHubIssue = createNewGibHubIssue(environmentDetails);
      Issue duplicate = findFirstDuplicate(newGibHubIssue.getTitle(), issueService, repoID);
      boolean isNewIssue = true;
      if (duplicate != null) {
        if(errorDescription != null) {
          issueService.createComment(repoID, duplicate.getNumber(), errorDescription);
        }
        newGibHubIssue = duplicate;
        isNewIssue = false;
      } else {
        newGibHubIssue = issueService.createIssue(repoID, newGibHubIssue);
      }

      final long id = newGibHubIssue.getNumber();
      final String htmlUrl = newGibHubIssue.getHtmlUrl();
      final String message = ErrorReportBundle.message(isNewIssue ? "git.issue.text" : "git.issue.duplicate.text", htmlUrl, id);
      result = new SubmittedReportInfo(htmlUrl, message, isNewIssue ? SubmissionStatus.NEW_ISSUE : SubmissionStatus.DUPLICATE);
      return result;
    } catch (IOException e) {
      return new SubmittedReportInfo(null, ErrorReportBundle.message("report.error.connection.failure"), SubmissionStatus.FAILED);
    }
  }

  @Nullable
  private static Issue findFirstDuplicate(String uniqueTitle, final IssueService service, RepositoryId repo) throws IOException {
    Map<String, String> searchParameters = new HashMap<>(2);
    searchParameters.put(IssueService.FILTER_STATE, IssueService.STATE_OPEN);
    final PageIterator<Issue> pages = service.pageIssues(repo, searchParameters);
    for (Collection<Issue> page : pages) {
      for (Issue issue : page) {
        if (issue.getTitle().equals(uniqueTitle)) {
          return issue;
        }
      }
    }
    return null;
  }

  private static Issue createNewGibHubIssue(LinkedHashMap<String, String> details) {
    String errorMessage = details.get("error.message");
    if (errorMessage == null || errorMessage.isEmpty()) {
      errorMessage = "Unspecified error";
    }
    details.remove("error.message");

    String errorHash = details.get("error.hash");
    if (errorHash == null) {
      errorHash = "";
    }
    details.remove("error.hash");

    final Issue gitHubIssue = new Issue();
    final String body = generateGitHubIssueBody(details);
    gitHubIssue.setTitle(ErrorReportBundle.message("git.issue.title", errorHash, errorMessage));
    gitHubIssue.setBody(body);
    Label label = new Label();
    label.setName(issueLabel);
    gitHubIssue.setLabels(Collections.singletonList(label));
    return gitHubIssue;
  }

  private static String generateGitHubIssueBody(LinkedHashMap<String, String> details) {
    String errorDescription = details.get("error.description");
    if (errorDescription == null) {
      errorDescription = "";
    }
    details.remove("error.description");


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

    return result.toString();
  }
}

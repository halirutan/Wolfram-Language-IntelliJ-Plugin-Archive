/*
 * Copyright (c) 2017 Patrick Scheibe
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package de.halirutan.mathematica.codeinsight.completion.providers;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern.Capture;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.FileBasedIndex;
import de.halirutan.mathematica.index.packageexport.MathematicaPackageExportIndex;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static de.halirutan.mathematica.codeinsight.completion.MathematicaCompletionContributor.IMPORT_VARIABLE_PRIORITY;


/**
 * Accesses the file index to provide completion for functions that are defined in other packages.
 */
public class ImportedSymbolCompletion extends MathematicaCompletionProvider {

  @Override
  public void addTo(CompletionContributor contributor) {
    final Capture<PsiElement> symbolPattern = PlatformPatterns.psiElement().withParent(Symbol.class);
    contributor.extend(CompletionType.BASIC, symbolPattern, this);
  }

  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    final Symbol callingSymbol = (Symbol) parameters.getPosition().getParent();
    final Project project = callingSymbol.getProject();

    String prefix = findCurrentText(parameters, parameters.getPosition());
    final PsiFile originalFile = parameters.getOriginalFile();
    final Module module =
        ModuleUtilCore.findModuleForFile(originalFile.getVirtualFile(), project);
    if (module != null) {
      if (!parameters.isExtendedCompletion() || !(prefix.isEmpty() || Character.isDigit(prefix.charAt(0)))) {
        final GlobalSearchScope moduleScope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
                                                               .union(ProjectScope.getLibrariesScope(project));
        final FileBasedIndex index = FileBasedIndex.getInstance();
        index.processAllKeys(
            MathematicaPackageExportIndex.INDEX_ID,
            key -> {
              if (key.isExported() && !Objects.equals(key.getFileName(), originalFile.getName())) {
                result.addElement(PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(key.getSymbol()).withTypeText("(" + key.getFileName() + ")", true),
                    IMPORT_VARIABLE_PRIORITY));
              }
              return true;
            },
            moduleScope,
            null
        );
      }
    }
  }
}

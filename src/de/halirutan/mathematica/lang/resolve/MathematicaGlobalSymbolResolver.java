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

package de.halirutan.mathematica.lang.resolve;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import de.halirutan.mathematica.index.packageexport.MathematicaPackageExportIndex;
import de.halirutan.mathematica.lang.psi.api.MathematicaPsiFile;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.resolve.processors.GlobalDefinitionResolveProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static de.halirutan.mathematica.lang.psi.util.MathematicaPsiUtilities.isBuiltInSymbol;

/**
 * The symbol resolver works currently in 3 steps to find a possible definition of a symbol that appears in the code.
 * It will check if the symbol is a built-in symbol
 * It will make a tree-walk upwards to check if the symbol is in any localization construct
 * It will check the file, if the symbol is defined as a global symbol like a function at file-scope
 * It will check the file-index and look for symbols that are exported from other files
 *
 * @author patrick (08.07.17).
 */
public class MathematicaGlobalSymbolResolver {

  @NotNull
  public ResolveResult[] resolve(@NotNull Symbol ref, @NotNull PsiFile containingFile) {

    if (!containingFile.equals(ref.getContainingFile())) {
      return ResolveResult.EMPTY_ARRAY;
    }

    final MathematicaGlobalResolveCache symbolCache =
        MathematicaGlobalResolveCache.getInstance(containingFile.getProject());

    if (symbolCache.containsSymbol(ref)) {
      return new ResolveResult[]{symbolCache.getValue(ref)};
    }

    if (isBuiltInSymbol(ref)) {
      final SymbolResolveResult result = symbolCache.cacheBuiltInSymbol(ref);
      return new ResolveResult[]{result};
    }

    GlobalDefinitionResolveProcessor globalProcessor = new GlobalDefinitionResolveProcessor(ref);
    PsiTreeUtil.processElements(containingFile, globalProcessor);

    final Symbol resolveResult = globalProcessor.getResolveResult();
    if (resolveResult != null) {
      final SymbolResolveResult result = symbolCache.cacheFileSymbol(resolveResult, containingFile);
      if (containingFile instanceof MathematicaPsiFile) {
        ((MathematicaPsiFile) containingFile).cacheLocalDefinition(result);
      }
      return new ResolveResult[]{result};
    }

    final Project project = containingFile.getProject();
    final Module module =
        ModuleUtilCore.findModuleForFile(containingFile.getVirtualFile(), project);
    if (module != null) {
      final List<SymbolResolveResult> references = new ArrayList<>();
      final PsiManager psiManager = PsiManager.getInstance(project);
      final FileBasedIndex fileIndex = FileBasedIndex.getInstance();
      final GlobalSearchScope moduleScope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module)
                                                             .union(ProjectScope.getLibrariesScope(project));
      fileIndex.processAllKeys(
          MathematicaPackageExportIndex.INDEX_ID,
          key -> {
            if (key.isExported() && key.getSymbol().equals(ref.getSymbolName())) {
              final Collection<VirtualFile> containingFiles =
                  fileIndex.getContainingFiles(MathematicaPackageExportIndex.INDEX_ID, key, moduleScope);
              containingFiles.forEach(file -> {
                final PsiFile psiFile = psiManager.findFile(file);
                if (psiFile != null) {
                  final Symbol externalSymbol =
                      PsiTreeUtil.findElementOfClassAtOffset(psiFile, key.getOffset(), Symbol.class, true);
                  if (externalSymbol != null) {
                    final SymbolResolveResult result = symbolCache.cacheExternalSymbol(ref, externalSymbol, psiFile);
                    references.add(result);
                  }
                }
              });
            }
            return true;
          }, moduleScope, null);
      fileIndex.getAllKeys(MathematicaPackageExportIndex.INDEX_ID, project);
      if (!references.isEmpty()) {
        return references.toArray(new ResolveResult[0]);
      }
    }

    final SymbolResolveResult result = symbolCache.cacheInvalidFileSymbol(ref, containingFile);
    return new ResolveResult[]{result};
  }

}

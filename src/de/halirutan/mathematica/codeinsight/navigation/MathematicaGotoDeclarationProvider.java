package de.halirutan.mathematica.codeinsight.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import de.halirutan.mathematica.lang.psi.LocalizationConstruct;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.resolve.GlobalDefinitionCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

/**
 * @author patrick (30.11.17).
 */
public class MathematicaGotoDeclarationProvider implements GotoDeclarationHandler {
  @Nullable
  @Override
  public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
    if (sourceElement == null) return PsiElement.EMPTY_ARRAY;
    if (sourceElement.getParent() instanceof Symbol) {
      final Symbol symbol = (Symbol) sourceElement.getParent();
      final LocalizationConstruct.MScope scope = symbol.getLocalizationConstruct();
      if (scope == LocalizationConstruct.MScope.NULL_SCOPE || scope == LocalizationConstruct.MScope.KERNEL_SCOPE) {
        return PsiElement.EMPTY_ARRAY;
      }
      if (LocalizationConstruct.isLocalScoping(scope)) {
        final PsiElement resolve = symbol.resolve();
        if (resolve != null) {
          return new PsiElement[]{resolve};
        }
      }

      if (scope == LocalizationConstruct.MScope.IMPORT_SCOPE) {
        final ResolveResult[] resolve = symbol.multiResolve(false);
        if (resolve.length > 0) {
          final PsiElement elm = resolve[0].getElement();
          if (elm != null) {
            return new PsiElement[]{elm};
          }
        }
      }

      return findDeclarations(symbol);
    }
    return PsiElement.EMPTY_ARRAY;
  }

  @Nullable
  @Override
  public String getActionText(DataContext context) {
    return null;
  }

  private PsiElement[] findDeclarations(@NotNull Symbol symbol) {
    java.util.Set<PsiElement> result = new HashSet<>();
    GlobalDefinitionCollector c = new GlobalDefinitionCollector(symbol.getContainingFile());
    if (c.getAssignments().containsKey(symbol.getSymbolName())) {
      c.getAssignments().get(symbol.getSymbolName()).forEach(
          assignmentProperty -> result.add(assignmentProperty.myLhsOfAssignment));
    }
    return result.toArray(new PsiElement[result.size()]);
  }

}

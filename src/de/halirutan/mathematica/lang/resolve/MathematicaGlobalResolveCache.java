package de.halirutan.mathematica.lang.resolve;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.AnyPsiChangeListener;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.util.messages.MessageBus;
import de.halirutan.mathematica.lang.psi.LocalizationConstruct;
import de.halirutan.mathematica.lang.psi.api.Symbol;
import de.halirutan.mathematica.lang.psi.impl.LightBuiltInSymbol;
import de.halirutan.mathematica.lang.psi.impl.LightExternalSymbol;
import de.halirutan.mathematica.lang.psi.impl.LightFileSymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author patrick (29.11.17).
 */
public class MathematicaGlobalResolveCache {

  private static final NotNullLazyKey<MathematicaGlobalResolveCache, Project> INSTANCE_KEY =
      ServiceManager.createLazyKey(MathematicaGlobalResolveCache.class);
  private final Map<LightFileSymbol, SymbolResolveResult> myCachedFileSymbols = new ConcurrentHashMap<>();
  private final Map<LightBuiltInSymbol, SymbolResolveResult> myCachedBuiltinSymbols = new ConcurrentHashMap<>();
  private final Map<LightExternalSymbol, SymbolResolveResult> myCachedExternalSymbols = new ConcurrentHashMap<>();
  private boolean myInvalidCache = true;

  private MathematicaGlobalResolveCache(@Nullable("can be null in com.intellij.core.JavaCoreApplicationEnvironment.JavaCoreApplicationEnvironment") MessageBus messageBus) {
    if (messageBus != null) {
      messageBus.connect().subscribe(PsiManagerImpl.ANY_PSI_CHANGE_TOPIC, new AnyPsiChangeListener.Adapter() {
        @Override
        public void afterPsiChanged(boolean isPhysical) {
          if (isPhysical) {
            markInvalid();
          }
        }
      });
    }
  }

  public static MathematicaGlobalResolveCache getInstance(Project project) {
    return INSTANCE_KEY.getValue(project);
  }

  private void markInvalid() {
    myInvalidCache = true;
  }

  private void clearCaches() {
    myCachedFileSymbols.clear();
    myCachedBuiltinSymbols.clear();
    myCachedExternalSymbols.clear();
    myInvalidCache = false;

  }

  public boolean containsSymbol(@NotNull Symbol symbol) {
    return containsBuildInSymbol(symbol) || containsFileSymbol(symbol) || containsExternalSymbol(symbol);
  }

  public boolean containsFileSymbol(@NotNull Symbol symbol) {
    final LightFileSymbol lightFileSymbol = new LightFileSymbol(symbol);
    return myCachedFileSymbols.containsKey(lightFileSymbol);
  }

  private boolean containsBuildInSymbol(@NotNull Symbol symbol) {
    final LightBuiltInSymbol lightBuiltInSymbol = new LightBuiltInSymbol(symbol);
    return myCachedBuiltinSymbols.containsKey(lightBuiltInSymbol);
  }

  private boolean containsExternalSymbol(@NotNull Symbol symbol) {
    final LightExternalSymbol lightExternalSymbol = new LightExternalSymbol(symbol);
    return myCachedExternalSymbols.containsKey(lightExternalSymbol);
  }

  public SymbolResolveResult cacheFileSymbol(@NotNull Symbol symbol, PsiElement scopeElement) {
    if (myInvalidCache) {
      clearCaches();
    }
    final LightFileSymbol lightSymbol = new LightFileSymbol(symbol);
    return myCachedFileSymbols.computeIfAbsent(lightSymbol,
        k -> new SymbolResolveResult(lightSymbol, LocalizationConstruct.MScope.FILE_SCOPE, scopeElement, true));
  }

  public SymbolResolveResult cacheInvalidFileSymbol(@NotNull Symbol symbol, PsiElement scopeElement) {
    if (myInvalidCache) {
      clearCaches();
    }
    final LightFileSymbol lightSymbol = new LightFileSymbol(symbol);
    return myCachedFileSymbols.computeIfAbsent(lightSymbol,
        k -> new SymbolResolveResult(lightSymbol, LocalizationConstruct.MScope.NULL_SCOPE, scopeElement, false));
  }


  public SymbolResolveResult cacheBuiltInSymbol(@NotNull Symbol symbol) {
    if (myInvalidCache) {
      clearCaches();
    }
    final LightBuiltInSymbol lightSymbol = new LightBuiltInSymbol(symbol);
    return myCachedBuiltinSymbols.computeIfAbsent(lightSymbol,
        k -> new SymbolResolveResult(lightSymbol, LocalizationConstruct.MScope.KERNEL_SCOPE, null, true));
  }

  public SymbolResolveResult cacheExternalSymbol(@NotNull Symbol symbol, @NotNull Symbol externalSymbol, PsiElement scopeElement) {
    if (myInvalidCache) {
      clearCaches();
    }
    final LightExternalSymbol lightSymbol = new LightExternalSymbol(symbol);
    return myCachedExternalSymbols.computeIfAbsent(lightSymbol,
        k -> new SymbolResolveResult(externalSymbol, LocalizationConstruct.MScope.IMPORT_SCOPE, scopeElement, true));
  }

  public ResolveResult getValue(Symbol symbol) {
    SymbolResolveResult symbolResolveResult = myCachedBuiltinSymbols.get(new LightBuiltInSymbol(symbol));
    if (symbolResolveResult != null) {
      return symbolResolveResult;
    }
    symbolResolveResult = myCachedFileSymbols.get(new LightFileSymbol(symbol));
    if (symbolResolveResult != null) {
      return symbolResolveResult;
    }
    return myCachedExternalSymbols.get(new LightExternalSymbol(symbol));
  }

  public List<SymbolResolveResult> getCachedFileSymbolResolves(@NotNull PsiFile containingFile) {
    return myCachedFileSymbols
        .values()
        .parallelStream()
        .filter(resolve -> containingFile.equals(resolve.getScopingElement()) &&
            resolve.getElement() instanceof LightFileSymbol)
        .collect(Collectors.toList());
  }

  public List<String> getCachedFileSymbolNames(@NotNull PsiFile file) {
    return getCachedFileSymbolResolves(file)
        .stream()
        .filter(resolve -> resolve.getElement() != null)
        .map(resolve -> ((LightFileSymbol) resolve.getElement()).getName()).collect(Collectors.toList());
  }
}

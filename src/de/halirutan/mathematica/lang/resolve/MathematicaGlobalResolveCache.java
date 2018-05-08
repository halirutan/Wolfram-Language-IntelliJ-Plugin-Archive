/*
 * Copyright (c) 2018 Patrick Scheibe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.halirutan.mathematica.lang.resolve;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
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

  private static final Logger LOG =
      Logger.getInstance("#de.halirutan.mathematica.lang.resolve.MathematicaGlobalResolveCache");

  private static final NotNullLazyKey<MathematicaGlobalResolveCache, Project> INSTANCE_KEY =
      ServiceManager.createLazyKey(MathematicaGlobalResolveCache.class);
  private final Map<LightFileSymbol, SymbolResolveResult> myCachedFileSymbols = new ConcurrentHashMap<>();
  private final Map<LightBuiltInSymbol, SymbolResolveResult> myCachedBuiltinSymbols = new ConcurrentHashMap<>();
  private final Map<LightExternalSymbol, SymbolResolveResult> myCachedExternalSymbols = new ConcurrentHashMap<>();


  private MathematicaGlobalResolveCache(@Nullable("can be null in com.intellij.core.JavaCoreApplicationEnvironment.JavaCoreApplicationEnvironment") MessageBus messageBus) {
    if (messageBus != null) {
      messageBus.connect().subscribe(PsiManagerImpl.ANY_PSI_CHANGE_TOPIC, new AnyPsiChangeListener.Adapter() {
        @Override
        public void afterPsiChanged(boolean isPhysical) {
          if (isPhysical) {
            clearCaches();
          }
        }
      });
    }
  }

  public static MathematicaGlobalResolveCache getInstance(Project project) {
    return INSTANCE_KEY.getValue(project);
  }

  private void clearCaches() {
    LOG.debug("Clearing symbol caches");
    myCachedFileSymbols.clear();
    myCachedBuiltinSymbols.clear();
    myCachedExternalSymbols.clear();
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
    final LightFileSymbol lightSymbol = new LightFileSymbol(symbol);
    return myCachedFileSymbols.computeIfAbsent(lightSymbol,
        k -> new SymbolResolveResult(lightSymbol, LocalizationConstruct.MScope.FILE_SCOPE, scopeElement, true));
  }

  public SymbolResolveResult cacheInvalidFileSymbol(@NotNull Symbol symbol, PsiElement scopeElement) {
    final LightFileSymbol lightSymbol = new LightFileSymbol(symbol);
    return myCachedFileSymbols.computeIfAbsent(lightSymbol,
        k -> new SymbolResolveResult(lightSymbol, LocalizationConstruct.MScope.NULL_SCOPE, scopeElement, false));
  }


  @NotNull
  public SymbolResolveResult cacheBuiltInSymbol(@NotNull Symbol symbol) {
    final LightBuiltInSymbol lightSymbol = new LightBuiltInSymbol(symbol);
    return myCachedBuiltinSymbols.computeIfAbsent(lightSymbol,
        k -> new SymbolResolveResult(lightSymbol, LocalizationConstruct.MScope.KERNEL_SCOPE, null, true));
  }

  @NotNull
  public SymbolResolveResult cacheExternalSymbol(@NotNull Symbol symbol, @NotNull Symbol externalSymbol, PsiElement scopeElement) {
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

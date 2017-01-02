/*
 * Copyright (c) 2016 Patrick Scheibe
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

package de.halirutan.mathematica.settings;/*
 * Copyright (c) 2016 Patrick Scheibe
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

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (01.12.16).
 */
@SuppressWarnings({"InstanceVariableNamingConvention", "WeakerAccess", "InstanceMethodNamingConvention"})
@State(name = "MathematicaSettings", storages = @Storage("other.xml"))
public class MathematicaSettings implements PersistentStateComponent<MathematicaSettings> {

  public enum SmartEnterResult {
    INSERT_BRACES,
    INSERT_CODE,
    INSERT_TEMPLATE
  }

  public SmartEnterResult smartEnterResult = SmartEnterResult.INSERT_TEMPLATE;
  public boolean sortCompletionEntriesLexicographically = false;

  public static MathematicaSettings getInstance() {
    return ServiceManager.getService(MathematicaSettings.class);
  }

  @Nullable
  @Override
  public MathematicaSettings getState() {
    return this;
  }

  @Override
  public void loadState(MathematicaSettings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    final MathematicaSettings settings = (MathematicaSettings) obj;
    if(smartEnterResult != settings.smartEnterResult) return false;
    return sortCompletionEntriesLexicographically == settings.sortCompletionEntriesLexicographically;
  }

  @Override
  public int hashCode() {
    int result = (smartEnterResult.ordinal());
    result = 29 * result + (sortCompletionEntriesLexicographically ? 1 : 0);
    return result;
  }

  public SmartEnterResult getSmartEnterResult() {
    return smartEnterResult;
  }

  public void setSmartEnterResult(SmartEnterResult result) {
    this.smartEnterResult = result;
  }

  public boolean isSortCompletionEntriesLexicographically() {
    return sortCompletionEntriesLexicographically;
  }

  public void setSortCompletionEntriesLexicographically(boolean shouldSort) {
    this.sortCompletionEntriesLexicographically = shouldSort;
  }
}

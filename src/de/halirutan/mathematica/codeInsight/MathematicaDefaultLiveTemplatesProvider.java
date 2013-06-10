package de.halirutan.mathematica.codeInsight;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (6/10/13)
 */
public class MathematicaDefaultLiveTemplatesProvider implements DefaultLiveTemplatesProvider {
    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nullable
    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}

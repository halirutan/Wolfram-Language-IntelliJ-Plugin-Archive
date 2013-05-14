package de.halirutan.mathematica.projectsAndModules;

import com.intellij.ide.util.projectWizard.SdkSettingsStep;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * User: rsmenon
 * Date: 5/13/13
 * Time: 6:36 PM
 */
public class MathematicaModifiedSettingsStep extends SdkSettingsStep {
    protected MathematicaModuleBuilder MODULE_BUILDER;

    MathematicaModifiedSettingsStep(@NotNull final MathematicaModuleBuilder builder, @NotNull SettingsStep settingsStep) {
        super(settingsStep, builder, new Condition<SdkTypeId>() {
            @Override
            public boolean value(SdkTypeId sdkType) {
                return builder.isSuitableSdkType(sdkType);
            }
        });
        MODULE_BUILDER = builder;
    }

    @Override
    public void updateDataModel() {
        super.updateDataModel();
        final String path = MODULE_BUILDER.getContentEntryPath();

        if (path != null) {
            //don't create an src file
            MODULE_BUILDER.setSourcePaths(Collections.singletonList(Pair.create(path, "")));
        }
    }

}

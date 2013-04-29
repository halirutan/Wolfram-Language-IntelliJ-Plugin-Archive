/*
 * Mathematica Plugin for Jetbrains IDEA
 * Copyright (C) 2013 Patrick Scheibe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.halirutan.mathematica.sdk;

import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.impl.SdkFinder;
import org.jetbrains.annotations.Nullable;

/**
 * @author patrick (4/29/13)
 */
public class MathematicaSdkFinder extends SdkFinder {
    @Nullable
    @Override
    public Sdk findSdk(String name, String sdkType) {
        return super.findSdk(name, sdkType);    //To change body of overridden methods use File | Settings | File Templates.
    }
}

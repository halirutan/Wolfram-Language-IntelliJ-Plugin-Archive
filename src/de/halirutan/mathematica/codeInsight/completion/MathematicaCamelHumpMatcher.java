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

package de.halirutan.mathematica.codeInsight.completion;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author patrick (4/3/13)
 */
public class MathematicaCamelHumpMatcher extends PrefixMatcher {
    @Override
    public boolean prefixMatches(@NotNull String name) {
        return false;
    }

    @NotNull
    @Override
    public PrefixMatcher cloneWithPrefix(@NotNull String prefix) {
        return null;
    }

    protected MathematicaCamelHumpMatcher(String prefix) {
        super(prefix);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean prefixMatches(@NotNull LookupElement element) {
        return super.prefixMatches(element);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean isStartMatch(LookupElement element) {
        return super.isStartMatch(element);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean isStartMatch(String name) {
        return super.isStartMatch(name);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int matchingDegree(String string) {
        return super.matchingDegree(string);    //To change body of overridden methods use File | Settings | File Templates.
    }
}

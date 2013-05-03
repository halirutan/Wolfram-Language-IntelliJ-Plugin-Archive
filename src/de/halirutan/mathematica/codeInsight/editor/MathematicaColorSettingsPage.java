/*
 * Copyright (c) 2013 Patrick Scheibe
 *
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

package de.halirutan.mathematica.codeInsight.editor;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import de.halirutan.mathematica.MathematicaIcons;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

import static de.halirutan.mathematica.codeInsight.editor.MathematicaSyntaxHighlighterColors.*;

/**
 * @author patrick (4/7/13)
 */
public class MathematicaColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] ATTR;

    static {
        ATTR = new AttributesDescriptor[]{
                new AttributesDescriptor("Comment", COMMENT),
                new AttributesDescriptor("String", STRING),
                new AttributesDescriptor("Number", LITERALS),
                new AttributesDescriptor("Identifier", IDENTIFIER),
                new AttributesDescriptor("Parenthesis", BRACES),
                new AttributesDescriptor("Operation sign", OPERATORS),
        };
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MathematicaIcons.FILE_ICON;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new MathematicaSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "ListVectorFieldPlot[ vects:{{{_,_},{_,_}}..}, opts___?OptionQ] :=\n" +
                "    Module[{maxsize,scale,scalefunct,colorfunct,points,\n" +
                "            vectors,colors,mags,scaledmag,allvecs,\n" +
                "            vecs = N[vects], arropts},\n" +
                "      (* -- get option values -- *)\n" +
                "        {maxsize,scale,scalefunct,colorfunct} =\n" +
                "            {MaxArrowLength,ScaleFactor,ScaleFunction,\n" +
                "            ColorFunction}/.Flatten[{opts, Options[ListVectorFieldPlot]}];\n" +
                "      (* select things that can only be vectors from the input *)\n" +
                "        vecs = Cases[vecs,\n" +
                "               {{_?numberQ, _?numberQ}, {_?numberQ, _?numberQ}},\n" +
                "               Infinity];\n" +
                "        {points, vectors} = Transpose[vecs];\n" +
                "        mags = Map[magnitude,vectors];\n" +
                "      (* -- determine the colors -- *)\n" +
                "      (* if the colorfunction is None, cause it to generate empty lists *)\n" +
                "        If[colorfunct == None, colorfunct = {}&];\n" +
                "      (* if all vectors are the same size, make list of colorfunct[0],\n" +
                "          else map the color function across the magnitudes *)\n" +
                "        If[Equal @@ mags,\n" +
                "            colors = Table[Evaluate[colorfunct[0]],{Length[mags]}],\n" +
                "            colors = Map[colorfunct,\n" +
                "                (mags - Min[mags])/Max[mags - Min[mags]]]\n" +
                "        ];\n" +
                "      (* -- scale vectors by scale function -- *)\n" +
                "        If[scalefunct =!= None,\n" +
                "             scaledmag = Map[If[# == 0, 0, scalefunct[#]]&, mags];\n" +
                "             {vectors, mags} = Transpose[MapThread[\n" +
                "                  If[#3 == 0 || !numberQ[#2], {{0,0}, 0}, {#1 #2/#3, #2}]&,\n" +
                "                  {vectors, scaledmag, mags}\n" +
                "              ]]\n" +
                "        ];\n" +
                "\n" +
                "      (* regroup colors, points, and mags with the associated vectors *)\n" +
                "        allvecs = Transpose[{colors, points, vectors, mags}];  \n" +
                "      (* pull all vectors with magnitude greater than MaxArrowLength *)\n" +
                "        If[numberQ[maxsize],\n" +
                "             allvecs = Select[allvecs, (#[[4]] <= N[maxsize])&]\n" +
                "        ];\n" +
                "      (* calculate scale factor *)\n" +
                "        If[numberQ[scale],\n" +
                "            scale = scale/Max[mags],\n" +
                "            scale = 1\n" +
                "        ];\n" +
                "      (* compatability hack: see if user supplied old-style arrowoptions *)\n" +
                "        arropts = getoldarrowopts[Flatten[{opts, Options[ListVectorFieldPlot]}]];\n" +
                "      (* turn the vectors into Arrow objects *)\n" +
                "        If[arropts =!= {},\n" +
                "            Needs[\"Graphics`Arrow`\"];\n" +
                "            allvecs = Apply[\n" +
                "                Flatten[{#1, Arrow[#2, #2 + scale #3, \n" +
                "                                   arropts,\n" +
                "                                   Graphics`Arrow`HeadScaling -> Automatic,\n" +
                "                                   Graphics`Arrow`HeadLength -> 0.02]\n" +
                "                                   }]&,\n" +
                "                allvecs, {1}],\n" +
                "          (* else V6-style arrows *)\n" +
                "            allvecs = Apply[\n" +
                "                Flatten[{#1, If[scale #3 == {0., 0.}, Point[#2],\n" +
                "                                Arrow[{#2, #2 + scale #3}]]}]&,\n" +
                "                allvecs, {1}]\n" +
                "        ];\n" +
                "      (* -- show the vector field plot -- *)\n" +
                "      (* note that line thickness is forced to 0.0001 (thin lines);\n" +
                "         this can be overridden by use of ColorFunction option *)\n" +
                "        Graphics[\n" +
                "             {Thickness[Small], Arrowheads[0.02], allvecs},\n" +
                "             FilterRules[Flatten[{opts, Options[ListVectorFieldPlot]}], Options[Graphics]]\n" +
                "        ]\n" +
                "    ]";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        final THashMap<String, TextAttributesKey> map = new THashMap<String, TextAttributesKey>();
        map.put("c", COMMENT);
        map.put("s", STRING);
        map.put("i", IDENTIFIER);
        map.put("o", OPERATORS);
        map.put("b", BRACES);
        map.put("l", LITERALS);
        return map;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTR;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Mathematica";
    }
}

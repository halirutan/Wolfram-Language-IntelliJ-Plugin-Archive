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

package de.halirutan.mathematica.sdk;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author patrick (27.11.16).
 */
@SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
public class MathematicaLanguageLevelTest {
  @Test
  public void isAtLeast() throws Exception {
    // Using the same version
    assertTrue(MathematicaLanguageLevel.M_11.isAtLeast(MathematicaLanguageLevel.M_11));
    assertTrue(MathematicaLanguageLevel.M_10_4.isAtLeast(MathematicaLanguageLevel.M_10_4));
    assertTrue(MathematicaLanguageLevel.M_10_3.isAtLeast(MathematicaLanguageLevel.M_10_3));
    assertTrue(MathematicaLanguageLevel.M_10_2.isAtLeast(MathematicaLanguageLevel.M_10_2));
    assertTrue(MathematicaLanguageLevel.M_10_1.isAtLeast(MathematicaLanguageLevel.M_10_1));
    assertTrue(MathematicaLanguageLevel.M_10.isAtLeast(MathematicaLanguageLevel.M_10));
    assertTrue(MathematicaLanguageLevel.M_9.isAtLeast(MathematicaLanguageLevel.M_9));
    assertTrue(MathematicaLanguageLevel.M_8.isAtLeast(MathematicaLanguageLevel.M_8));

    // Using all combinations of smaller versions
    assertTrue(MathematicaLanguageLevel.M_11.isAtLeast(MathematicaLanguageLevel.M_10_4));
    assertTrue(MathematicaLanguageLevel.M_11.isAtLeast(MathematicaLanguageLevel.M_10_3));
    assertTrue(MathematicaLanguageLevel.M_11.isAtLeast(MathematicaLanguageLevel.M_10_2));
    assertTrue(MathematicaLanguageLevel.M_11.isAtLeast(MathematicaLanguageLevel.M_10_1));
    assertTrue(MathematicaLanguageLevel.M_11.isAtLeast(MathematicaLanguageLevel.M_10));
    assertTrue(MathematicaLanguageLevel.M_11.isAtLeast(MathematicaLanguageLevel.M_9));
    assertTrue(MathematicaLanguageLevel.M_11.isAtLeast(MathematicaLanguageLevel.M_8));
    assertTrue(MathematicaLanguageLevel.M_10_4.isAtLeast(MathematicaLanguageLevel.M_10_3));
    assertTrue(MathematicaLanguageLevel.M_10_4.isAtLeast(MathematicaLanguageLevel.M_10_2));
    assertTrue(MathematicaLanguageLevel.M_10_4.isAtLeast(MathematicaLanguageLevel.M_10_1));
    assertTrue(MathematicaLanguageLevel.M_10_4.isAtLeast(MathematicaLanguageLevel.M_10));
    assertTrue(MathematicaLanguageLevel.M_10_4.isAtLeast(MathematicaLanguageLevel.M_9));
    assertTrue(MathematicaLanguageLevel.M_10_4.isAtLeast(MathematicaLanguageLevel.M_8));
    assertTrue(MathematicaLanguageLevel.M_10_3.isAtLeast(MathematicaLanguageLevel.M_10_2));
    assertTrue(MathematicaLanguageLevel.M_10_3.isAtLeast(MathematicaLanguageLevel.M_10_1));
    assertTrue(MathematicaLanguageLevel.M_10_3.isAtLeast(MathematicaLanguageLevel.M_10));
    assertTrue(MathematicaLanguageLevel.M_10_3.isAtLeast(MathematicaLanguageLevel.M_9));
    assertTrue(MathematicaLanguageLevel.M_10_3.isAtLeast(MathematicaLanguageLevel.M_8));
    assertTrue(MathematicaLanguageLevel.M_10_2.isAtLeast(MathematicaLanguageLevel.M_10_1));
    assertTrue(MathematicaLanguageLevel.M_10_2.isAtLeast(MathematicaLanguageLevel.M_10));
    assertTrue(MathematicaLanguageLevel.M_10_2.isAtLeast(MathematicaLanguageLevel.M_9));
    assertTrue(MathematicaLanguageLevel.M_10_2.isAtLeast(MathematicaLanguageLevel.M_8));
    assertTrue(MathematicaLanguageLevel.M_10_1.isAtLeast(MathematicaLanguageLevel.M_10));
    assertTrue(MathematicaLanguageLevel.M_10_1.isAtLeast(MathematicaLanguageLevel.M_9));
    assertTrue(MathematicaLanguageLevel.M_10_1.isAtLeast(MathematicaLanguageLevel.M_8));
    assertTrue(MathematicaLanguageLevel.M_10.isAtLeast(MathematicaLanguageLevel.M_9));
    assertTrue(MathematicaLanguageLevel.M_10.isAtLeast(MathematicaLanguageLevel.M_8));
    assertTrue(MathematicaLanguageLevel.M_9.isAtLeast(MathematicaLanguageLevel.M_8));

  }

  @Test
  public void isLessThan() throws Exception {
    assertTrue(MathematicaLanguageLevel.M_8.isLessThan(MathematicaLanguageLevel.M_9));
    assertTrue(MathematicaLanguageLevel.M_8.isLessThan(MathematicaLanguageLevel.M_10));
    assertTrue(MathematicaLanguageLevel.M_8.isLessThan(MathematicaLanguageLevel.M_10_1));
    assertTrue(MathematicaLanguageLevel.M_8.isLessThan(MathematicaLanguageLevel.M_10_2));
    assertTrue(MathematicaLanguageLevel.M_8.isLessThan(MathematicaLanguageLevel.M_10_3));
    assertTrue(MathematicaLanguageLevel.M_8.isLessThan(MathematicaLanguageLevel.M_10_4));
    assertTrue(MathematicaLanguageLevel.M_8.isLessThan(MathematicaLanguageLevel.M_11));
    assertTrue(MathematicaLanguageLevel.M_9.isLessThan(MathematicaLanguageLevel.M_10));
    assertTrue(MathematicaLanguageLevel.M_9.isLessThan(MathematicaLanguageLevel.M_10_1));
    assertTrue(MathematicaLanguageLevel.M_9.isLessThan(MathematicaLanguageLevel.M_10_2));
    assertTrue(MathematicaLanguageLevel.M_9.isLessThan(MathematicaLanguageLevel.M_10_3));
    assertTrue(MathematicaLanguageLevel.M_9.isLessThan(MathematicaLanguageLevel.M_10_4));
    assertTrue(MathematicaLanguageLevel.M_9.isLessThan(MathematicaLanguageLevel.M_11));
    assertTrue(MathematicaLanguageLevel.M_10.isLessThan(MathematicaLanguageLevel.M_10_1));
    assertTrue(MathematicaLanguageLevel.M_10.isLessThan(MathematicaLanguageLevel.M_10_2));
    assertTrue(MathematicaLanguageLevel.M_10.isLessThan(MathematicaLanguageLevel.M_10_3));
    assertTrue(MathematicaLanguageLevel.M_10.isLessThan(MathematicaLanguageLevel.M_10_4));
    assertTrue(MathematicaLanguageLevel.M_10.isLessThan(MathematicaLanguageLevel.M_11));
    assertTrue(MathematicaLanguageLevel.M_10_1.isLessThan(MathematicaLanguageLevel.M_10_2));
    assertTrue(MathematicaLanguageLevel.M_10_1.isLessThan(MathematicaLanguageLevel.M_10_3));
    assertTrue(MathematicaLanguageLevel.M_10_1.isLessThan(MathematicaLanguageLevel.M_10_4));
    assertTrue(MathematicaLanguageLevel.M_10_1.isLessThan(MathematicaLanguageLevel.M_11));
    assertTrue(MathematicaLanguageLevel.M_10_2.isLessThan(MathematicaLanguageLevel.M_10_3));
    assertTrue(MathematicaLanguageLevel.M_10_2.isLessThan(MathematicaLanguageLevel.M_10_4));
    assertTrue(MathematicaLanguageLevel.M_10_2.isLessThan(MathematicaLanguageLevel.M_11));
    assertTrue(MathematicaLanguageLevel.M_10_3.isLessThan(MathematicaLanguageLevel.M_10_4));
    assertTrue(MathematicaLanguageLevel.M_10_3.isLessThan(MathematicaLanguageLevel.M_11));
    assertTrue(MathematicaLanguageLevel.M_10_4.isLessThan(MathematicaLanguageLevel.M_11));
  }

}
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

package de.halirutan.mathematica.index;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author patrick (18.12.16).
 */
public class PackageUtilTest {
  @Test
  public void buildContext() throws Exception {
    final String c1[] = {"Package`", "`Private`"};
    final String c2[] = {"Package`", "New`"};
    final String c3[] = {"Package`", "New`", "`Private`"};
    final String c4[] = {"$Package`", "New01`", "`$Private`"};

    assertEquals("Package`Private`", PackageUtil.buildContext(Arrays.asList(c1)));
    assertEquals("New`", PackageUtil.buildContext(Arrays.asList(c2)));
    assertEquals("New`Private`", PackageUtil.buildContext(Arrays.asList(c3)));
    assertEquals("New01`$Private`", PackageUtil.buildContext(Arrays.asList(c4)));

  }

}
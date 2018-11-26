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

package de.halirutan.mathematica.refactoring;

import de.halirutan.mathematica.information.SymbolInformation;
import de.halirutan.mathematica.information.impl.SymbolInformationImpl;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author patrick (27.12.16).
 */
public class MathematicaNamesValidatorTest {

  // In real code, use ServiceManager.getService(SymbolInformation.class) to retrieve an object of this.
  private final SymbolInformation symbolInfo = new SymbolInformationImpl();

  private final String[] myCounterExamples = {
      "Internal`",
      "0Symbol",
      "Internal`3",
      "Developer`ToPacketArray`"
  };

  @Test
  public void testIsIdentifier() {
    MathematicaNamesValidator validator = new MathematicaNamesValidator();
    for (String nc : symbolInfo.getNamedCharacters()) {
      assertTrue(validator.isIdentifier("\\[" + nc + "]", null));
    }

    for (String symbol : symbolInfo.getSymbolsWithProperties()) {
      assertTrue(symbol, validator.isIdentifier(symbol, null));
    }

    for (String badSymbol : myCounterExamples) {
      assertFalse(badSymbol, validator.isIdentifier(badSymbol, null));
    }

    assertTrue(validator.isIdentifier("Internal`testvar",null));
  }

}
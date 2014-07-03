/*
 * Copyright (c) 2013 Patrick Scheibe
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

package de.halirutan.mathematica.parsing.psi;

import com.intellij.psi.PsiElementVisitor;
import de.halirutan.mathematica.parsing.psi.api.*;
import de.halirutan.mathematica.parsing.psi.api.arithmetic.ArithmeticOperation;
import de.halirutan.mathematica.parsing.psi.api.assignment.*;
import de.halirutan.mathematica.parsing.psi.api.comparison.ComparisonOperation;
import de.halirutan.mathematica.parsing.psi.api.function.Function;
import de.halirutan.mathematica.parsing.psi.api.lists.List;
import de.halirutan.mathematica.parsing.psi.api.pattern.*;
import de.halirutan.mathematica.parsing.psi.api.rules.ReplaceAll;
import de.halirutan.mathematica.parsing.psi.api.rules.ReplaceRepeated;
import de.halirutan.mathematica.parsing.psi.api.rules.Rule;
import de.halirutan.mathematica.parsing.psi.api.rules.RuleDelayed;


/**
 * @author patrick (10/9/13)
 */
public class MathematicaVisitor extends PsiElementVisitor {

  public void visitCompoundExpression(CompoundExpression compoundExpression) {
    visitElement(compoundExpression);
  }

  public void visitSetDelayed(SetDelayed setDelayed) {
    visitElement(setDelayed);
  }

  public void visitSet(Set set) {
    visitElement(set);
  }

  public void visitTagSet(TagSet element) {
    visitElement(element);
  }

  public void visitTagSetDelayed(TagSetDelayed tagSetDelayed) {
    visitElement(tagSetDelayed);
  }
  public void visitUpSet(UpSet upSet) {
    visitElement(upSet);
  }

  public void visitArithmeticOperation(ArithmeticOperation arithmeticOperation) {
    visitElement(arithmeticOperation);
  }

  public void visitBlank(Blank blank) {
    visitElement(blank);
  }

  public void visitBlankSequence(BlankSequence blankSequence) {
    visitElement(blankSequence);
  }

  public void visitBlankNullSequence(BlankNullSequence blankNullSequence) {
    visitElement(blankNullSequence);
  }

  public void visitAlternative(Alternative alternative) {
    visitElement(alternative);
  }

  public void visitCondition(Condition condition) {
    visitElement(condition);
  }

  public void visitDefault(Default aDefault) {
    visitElement(aDefault);
  }

  public void visitOptional(Optional optional) {
    visitElement(optional);
  }

  public void visitPattern(Pattern pattern) {
    visitElement(pattern);
  }

  public void visitPatternTest(PatternTest patternTest) {
    visitElement(patternTest);
  }

  public void visitRepeated(Repeated repeated) {
    visitElement(repeated);
  }

  public void visitRepeatedNull(RepeatedNull repeatedNull) {
    visitElement(repeatedNull);
  }

  public void visitReplaceAll(ReplaceAll replaceAll) {
    visitElement(replaceAll);
  }

  public void visitReplaceRepeated(ReplaceRepeated replaceRepeated) {
    visitElement(replaceRepeated);
  }

  public void visitRuleDelayed(RuleDelayed ruleDelayed) {
    visitElement(ruleDelayed);
  }

  public void visitRule(Rule rule) {
    visitElement(rule);
  }

  public void visitFunction(Function function) {
    visitElement(function);
  }

  public void visitComparisonOperation(ComparisonOperation comparisonOperation) {
    visitElement(comparisonOperation);
  }

  public void visitGroup(Group group) {
    visitElement(group);
  }

  public void visitFunctionCall(FunctionCall functionCall) {
    visitElement(functionCall);
  }

  public void visitMessageName(MessageName messageName) {
    visitElement(messageName);
  }

  public void visitSymbol(Symbol symbol) {
    visitElement(symbol);
  }

  public void visitList(List list) {
    visitElement(list);
  }
}

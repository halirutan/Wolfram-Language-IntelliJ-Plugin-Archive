package de.halirutan.mathematica.codeInsight.formatter;

import com.intellij.codeInsight.editorActions.enter.EnterBetweenBracesHandler;

/**
 * @author patrick (11/12/13)
 */
public class MathematicaEnterBetweenBracesHandler extends EnterBetweenBracesHandler {

  protected boolean isBracePair(char c1, char c2) {

    return (c1 == '(' && c2 == ')') || (c1 == '{' && c2 == '}') || (c1 == '[' && c2 == ']');
  }
}

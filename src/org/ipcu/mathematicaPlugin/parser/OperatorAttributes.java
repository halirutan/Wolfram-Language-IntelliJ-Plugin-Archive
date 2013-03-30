package org.ipcu.mathematicaPlugin.parser;

/**
 *
 * @author patrick (3/27/13)
 */
public class OperatorAttributes {
    public int precedence;
    public boolean isLeftAssociative;

    public OperatorAttributes(int precedence, boolean leftAssociative) {
        this.precedence = precedence;
        isLeftAssociative = leftAssociative;
    }
}

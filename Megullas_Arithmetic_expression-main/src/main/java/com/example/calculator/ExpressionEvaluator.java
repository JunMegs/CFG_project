package com.example.calculator;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.tree.ParseTree;

public class ExpressionEvaluator {
    private Map<String, Double> symbolTable = new HashMap<>();
    
    public ExpressionEvaluator() {
        // Initialize with both lowercase and uppercase variable mappings
        symbolTable.put("x", 10.0);
        symbolTable.put("y", 7.0);
        symbolTable.put("z", 3.0);
        symbolTable.put("p", 6.0);
        symbolTable.put("X", 20.0);
        symbolTable.put("Y", 15.0);
        symbolTable.put("Z", 12.0);
        symbolTable.put("P", 24.0);
    }
    
    public void defineVariable(String name, double value) {
        symbolTable.put(name, value);
    }
    
    public double compute(ParseTree tree) {
        // If it's a leaf node, evaluate directly
        if (tree.getChildCount() == 0) {
            String nodeText = tree.getText();
            if (nodeText.matches("[a-zA-Z]")) {
                // Variable
                return symbolTable.getOrDefault(nodeText, 0.0);
            } 
            // else if (nodeText.matches("[A-Z]")) {
            //     //uppercase variable
            //     return symbolTable.getOrDefault(nodeText, 0.0);
            // }
            else if (nodeText.matches("\\d+")) {
                // Number
                return Double.parseDouble(nodeText);
            }
            return 0.0;
        }
        
        // If the tree has children, evaluate recursively
        if (tree.getChildCount() == 3) {
            // Expected structure: operand1, operator, operand2
            double operand1 = compute(tree.getChild(0)); // Left operand
            String operator = tree.getChild(1).getText(); // Operator
            double operand2 = compute(tree.getChild(2)); // Right operand
            
            // Perform operation based on operator
            switch (operator) {
                case "+":
                    return operand1 + operand2;
                case "-":
                    return operand1 - operand2;
                case "*":
                    return operand1 * operand2;
                case "/":
                    if (operand2 == 0) {
                        throw new ArithmeticException("Cannot divide by zero");
                    }
                    return operand1 / operand2;
                default:
                    throw new UnsupportedOperationException("Unsupported operator: " + operator);
            }
        }
        
        // If the tree is not in the expected structure, throw an error
        throw new IllegalArgumentException("Invalid parse tree structure");
    }
}

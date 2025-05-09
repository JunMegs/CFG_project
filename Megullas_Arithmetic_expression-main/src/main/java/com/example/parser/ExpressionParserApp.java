package com.example.parser;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

public class ExpressionParserApp {
    private static ParseTreeVisualizer parseTreeVisualizer;
    private static Map<String, JTextField> variableInputs = new HashMap<>();
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> setUpGUI());
    }

    private static void setUpGUI() {
        // Create the main window
        JFrame frame = new JFrame("Expression Parser & Evaluator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);

        // Main container panel
        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input section for expression
        JPanel expressionPanel = new JPanel(new BorderLayout(5, 5));
        JLabel expressionLabel = new JLabel("Expression:");
        JTextField expressionField = new JTextField("x + y * z");
        JButton parseBtn = new JButton("Parse");
        JButton evaluateBtn = new JButton("Evaluate");

        // Button panel for parse and evaluate actions
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionButtonPanel.add(parseBtn);
        actionButtonPanel.add(evaluateBtn);
        
        expressionPanel.add(expressionLabel, BorderLayout.WEST);
        expressionPanel.add(expressionField, BorderLayout.CENTER);
        expressionPanel.add(actionButtonPanel, BorderLayout.EAST);

        // Example expression buttons
        JPanel examplePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        examplePanel.add(new JLabel("Example Expressions:"));
        
        String[] exampleExpressions = {"x + y * z", "(x + y) * z", "x * (y + z)", "x + y + z * w"};
        for (String example : exampleExpressions) {
            JButton exampleButton = new JButton(example);
            exampleButton.addActionListener(e -> expressionField.setText(example));
            examplePanel.add(exampleButton);
        }
        
        // Variable input section
        JPanel variablesInputPanel = new JPanel(new GridLayout(1, 4, 10, 5));
        variablesInputPanel.setBorder(BorderFactory.createTitledBorder("Variable Values"));
        
        String[] variables = {"x", "y", "z", "w"};
        for (String variable : variables) {
            JPanel variablePanel = new JPanel(new BorderLayout(5, 0));
            variablePanel.add(new JLabel(variable + " = "), BorderLayout.WEST);
            JTextField varInputField = new JTextField("5");
            variablePanel.add(varInputField, BorderLayout.CENTER);
            variablesInputPanel.add(variablePanel);
            variableInputs.put(variable, varInputField);
        }
        
        // Top section with expression input and variables
        JPanel topSection = new JPanel(new BorderLayout(5, 5));
        topSection.add(expressionPanel, BorderLayout.NORTH);
        topSection.add(variablesInputPanel, BorderLayout.CENTER);
        topSection.add(examplePanel, BorderLayout.SOUTH);

        // Right panel with split layout for the parse tree and logs
        JSplitPane splitLayout = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitLayout.setResizeWeight(0.7);

        // Panel for parse tree visualization
        JPanel treeViewPanel = new JPanel(new BorderLayout());
        treeViewPanel.setBorder(BorderFactory.createTitledBorder("Parse Tree"));

        parseTreeVisualizer = new ParseTreeVisualizer(null, null);
        JScrollPane treeScroll = new JScrollPane(parseTreeVisualizer);
        treeViewPanel.add(treeScroll, BorderLayout.CENTER);

        // Log panel for parsing info
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Parsing & Evaluation Log"));
        
        JTextArea logDisplay = new JTextArea();
        logDisplay.setEditable(false);
        logDisplay.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(logDisplay);
        logPanel.add(logScroll, BorderLayout.CENTER);

        // Add components to split layout
        splitLayout.setLeftComponent(treeViewPanel);
        splitLayout.setRightComponent(logPanel);

        // Add everything to the main container
        mainContainer.add(topSection, BorderLayout.NORTH);
        mainContainer.add(splitLayout, BorderLayout.CENTER);

        // Add action listener for parsing
        parseBtn.addActionListener(e -> {
            String input = expressionField.getText().trim();
            if (input.isEmpty()) {
                logDisplay.setText("Please enter an expression.");
                return;
            }
            
            try {
                // Parse and show the results in the UI
                processExpression(input, logDisplay, false);
            } catch (Exception ex) {
                logDisplay.setText("Parsing Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Add action listener for evaluation
        evaluateBtn.addActionListener(e -> {
            String input = expressionField.getText().trim();
            if (input.isEmpty()) {
                logDisplay.setText("Please enter an expression.");
                return;
            }
            
            try {
                // Parse and evaluate the expression
                processExpression(input, logDisplay, true);
            } catch (Exception ex) {
                logDisplay.setText("Evaluation Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Set up the frame and show it
        frame.getContentPane().add(mainContainer);
        frame.setVisible(true);
    }

    private static void processExpression(String input, JTextArea logArea, boolean evaluate) {
        StringBuilder logOutput = new StringBuilder();
        logOutput.append("Processing expression: ").append(input).append("\n\n");

        // Lexer and parser setup
        CharStream charStream = CharStreams.fromString(input);
        ArithmeticLexer lexer = new ArithmeticLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        logOutput.append("Tokens:\n");
        lexer.reset();
        while (true) {
            Token token = lexer.nextToken();
            if (token.getType() == Token.EOF) break;
            logOutput.append(String.format("%-10s %-20s\n", 
                    token.getText(), 
                    lexer.getVocabulary().getDisplayName(token.getType())));
        }
        logOutput.append("\n");

        // Parser setup
        lexer.reset();
        tokens = new CommonTokenStream(lexer);
        
        ArithmeticParser parser = new ArithmeticParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                                int line, int charPositionInLine, String msg, RecognitionException e) {
                logOutput.append("Error: ").append(msg).append("\n");
            }
        });

        // Parse the expression
        ParseTree parseTree = parser.expr();
        logOutput.append("Parsing completed successfully.\n\n");

        // Update tree visualizer with parse tree
        parseTreeVisualizer.setParseTree(parseTree, parser.getRuleNames());
        
        // Add tree structure to log
        logOutput.append("Parse Tree:\n");
        logOutput.append(parseTree.toStringTree(parser)).append("\n\n");
        
        // Evaluate the expression if required
        if (evaluate) {
            try {
                ArithmeticEvaluator evaluator = new ArithmeticEvaluator();
                
                // Set variable values
                for (Map.Entry<String, JTextField> entry : variableInputs.entrySet()) {
                    String varName = entry.getKey();
                    JTextField field = entry.getValue();
                    try {
                        double value = Double.parseDouble(field.getText());
                        evaluator.setVariable(varName, value);
                    } catch (NumberFormatException ex) {
                        logOutput.append("Invalid value for variable ").append(varName)
                        .append(". Using default value.\n");
                    }
                }
                
                double result = evaluator.evaluate(parseTree);
                logOutput.append("\nResult: ").append(result).append("\n");
            } catch (Exception e) {
                logOutput.append("\nError during evaluation: ").append(e.getMessage()).append("\n");
            }
        }

        // Update log area with results
        logArea.setText(logOutput.toString());
        logArea.setCaretPosition(0);
    }
}

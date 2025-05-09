


grammar Arithmetic;

// Top-level rule for full expressions or programs
prog: expr EOF               # Program;

// Parser Rules
expr
    : expr '+' term          # Addition
    | expr '-' term          # Subtraction
    | term                   # TermOnly
    ;

term
    : term '*' power         # Multiplication
    | term '/' power         # Division
    | power                  # PowerOnly
    ;


factor
    : '-' factor             # UnaryMinus
    | ID_U '(' expr ')'      # FunctionCallUppercase
    | ID '(' expr ')'        # FunctionCallLowercase
    | '(' expr ')'           # Parentheses
    | ID_U                   # Variable
    | ID                     # Variable
    | NUM                    # Number
    ;

// Lexer Rules
ID_U : [A-Z]+ ;              // Matches variable or function name
ID  : [a-z]+ ;               // Matches variable or function name
NUM : [0-9]+ ;               // Integer number
WS  : [ \t\r\n]+ -> skip ;   // Skip whitespace

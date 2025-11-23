import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private final List<Token> tokens;
    private int currentTokenIndex = 0;
    private final Map<String, Double> variables = new HashMap<>();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Map<String, Double> parse() {
        while (!isAtEnd()) {
            Token current = peek();
            if (current.type == Token.Type.SEMICOLON) {
                advance(); // skip empty semicolon
                continue;
            }
            if (current.type == Token.Type.IDENTIFIER && lookahead().type == Token.Type.ASSIGNMENT) {
                String varName = current.value;
                advance(); // consume identifier
                advance(); // consume '='
                double value = evaluateExpression();
                variables.put(varName, value);
                // optional semicolon
                if (peek().type == Token.Type.SEMICOLON) advance();
            } else {
                // either an expression (ignore result) or a stray token -> try to evaluate
                double val = evaluateExpression();
                if (peek().type == Token.Type.SEMICOLON) advance();
                // You may want to do something with val (e.g., print)
            }
        }
        return variables;
    }

    private double evaluateExpression() {
        double left = parseTerm();
        while (matchOperator("+", "-")) {
            String op = previous().value;
            double right = parseTerm();
            if (op.equals("+")) left = left + right;
            else left = left - right;
        }
        return left;
    }

    private double parseTerm() {
        double left = parseFactor();
        while (matchOperator("*", "/", "%")) {
            String op = previous().value;
            double right = parseFactor();
            if (op.equals("*")) left = left * right;
            else if (op.equals("/")) {
                if (right == 0) {
                    System.out.println("Error: Division by zero at pos " + previous().pos);
                    return Double.NaN;
                }
                left = left / right;
            } else if (op.equals("%")) left = left % right;
        }
        return left;
    }

    // handle exponentiation right-associative and unary minus
    private double parseFactor() {
        // unary minus
        if (matchOperator("-")) {
            double v = parseFactor();
            return -v;
        }

        double value;
        Token tok = peek();

        if (tok.type == Token.Type.NUMBER) {
            value = Double.parseDouble(tok.value);
            advance();
        } else if (tok.type == Token.Type.IDENTIFIER) {
            value = retrieveVariableValue(tok.value, tok.pos);
            advance();
        } else if (tok.type == Token.Type.LPAREN) {
            advance(); // '('
            value = evaluateExpression();
            if (peek().type != Token.Type.RPAREN) {
                System.out.println("Error: Expected ')' at pos " + peek().pos);
            } else {
                advance(); // ')'
            }
        } else {
            System.out.println("Error: Unexpected token '" + tok.value + "' at pos " + tok.pos);
            advance();
            return Double.NaN;
        }

        // handle right-associative exponentiation ^ and **
        while (matchOperator("^", "**")) {
            String op = previous().value;
            double exponent = parseFactor(); // right-associative
            value = Math.pow(value, exponent);
        }

        return value;
    }

    private double retrieveVariableValue(String name, int pos) {
        if (!variables.containsKey(name)) {
            System.out.println("Warning: variable '" + name + "' not defined at pos " + pos + ", defaulting to 0.0");
            return 0.0;
        }
        return variables.get(name);
    }

    // Utility parser helpers
    private boolean matchOperator(String... ops) {
        Token t = peek();
        if (t.type != Token.Type.OPERATOR) return false;
        for (String op: ops) if (t.value.equals(op)) { advance(); return true; }
        return false;
    }

    private Token previous() {
        return tokens.get(currentTokenIndex - 1);
    }

    private Token peek() {
        if (currentTokenIndex >= tokens.size()) return new Token(Token.Type.EOF, "", -1);
        return tokens.get(currentTokenIndex);
    }

    private Token lookahead() {
        if (currentTokenIndex + 1 >= tokens.size()) return new Token(Token.Type.EOF, "", -1);
        return tokens.get(currentTokenIndex + 1);
    }

    private void advance() {
        if (!isAtEnd()) currentTokenIndex++;
    }

    private boolean isAtEnd() {
        return peek().type == Token.Type.EOF;
    }
}

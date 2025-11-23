import java.util.*;

public class ParserAST {

    private final List<Token> tokens;
    private int pos = 0;

    public ParserAST(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Parse all statements
    public List<ASTNode> parseStatements() {
        List<ASTNode> statements = new ArrayList<>();
        while (!isAtEnd()) {
            if (peek().type == Token.Type.SEMICOLON) { pos++; continue; }
            statements.add(parseStatement());
        }
        return statements;
    }

    // Parse single assignment
    private ASTNode parseStatement() {
        Token id = expect(Token.Type.IDENTIFIER);
        expect(Token.Type.ASSIGNMENT);
        ASTNode expr = parseExpression();
        if (peek().type == Token.Type.SEMICOLON) pos++;
        return new AssignNode(id.value, expr);
    }

    private ASTNode parseExpression() {
        ASTNode node = parseTerm();
        while (matchOperator("+", "-")) {
            String op = previous().value;
            ASTNode right = parseTerm();
            node = new BinaryOpNode(op, node, right);
        }
        return node;
    }

    private ASTNode parseTerm() {
        ASTNode node = parseFactor();
        while (matchOperator("*", "/", "%")) {
            String op = previous().value;
            ASTNode right = parseFactor();
            node = new BinaryOpNode(op, node, right);
        }
        return node;
    }

    private ASTNode parseFactor() {
        ASTNode node = parseUnary();
        while (matchOperator("^", "**")) {
            String op = previous().value;
            ASTNode right = parseUnary();
            node = new BinaryOpNode(op, node, right);
        }
        return node;
    }

    private ASTNode parseUnary() {
        if (matchOperator("-")) {
            ASTNode expr = parseUnary();
            return new UnaryOpNode("-", expr);
        }
        return parsePrimary();
    }

    private ASTNode parsePrimary() {
        Token t = peek();
        if (match(Token.Type.NUMBER)) return new NumberNode(Double.parseDouble(t.value));
        if (match(Token.Type.IDENTIFIER)) return new IdentifierNode(t.value);
        if (match(Token.Type.LPAREN)) {
            ASTNode expr = parseExpression();
            expect(Token.Type.RPAREN);
            return expr;
        }
        throw new RuntimeException("Unexpected token: " + t);
    }

    private Token peek() {
        if (pos >= tokens.size()) return new Token(Token.Type.EOF, "", -1);
        return tokens.get(pos);
    }

    private Token previous() { return tokens.get(pos - 1); }

    private boolean match(Token.Type type) {
        if (peek().type == type) { pos++; return true; }
        return false;
    }

    private boolean matchOperator(String... ops) {
        Token t = peek();
        if (t.type != Token.Type.OPERATOR) return false;
        for (String op : ops) if (t.value.equals(op)) { pos++; return true; }
        return false;
    }

    private Token expect(Token.Type type) {
        Token t = peek();
        if (t.type != type) throw new RuntimeException("Expected " + type + " but found " + t.type);
        pos++;
        return t;
    }

    private boolean isAtEnd() { return peek().type == Token.Type.EOF; }
}

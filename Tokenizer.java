import java.util.*;

public class Tokenizer {
    private final String input;
    private int pos = 0;

    public Tokenizer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (Character.isWhitespace(c)) { pos++; continue; }

            if (Character.isDigit(c)) {
                int start = pos;
                while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) pos++;
                tokens.add(new Token(Token.Type.NUMBER, input.substring(start, pos), start));
            } else if (Character.isLetter(c)) {
                int start = pos;
                while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) pos++;
                tokens.add(new Token(Token.Type.IDENTIFIER, input.substring(start, pos), start));
            } else {
                switch (c) {
                    case '=' -> tokens.add(new Token(Token.Type.ASSIGNMENT, "=", pos++));
                    case ';' -> tokens.add(new Token(Token.Type.SEMICOLON, ";", pos++));
                    case '(' -> tokens.add(new Token(Token.Type.LPAREN, "(", pos++));
                    case ')' -> tokens.add(new Token(Token.Type.RPAREN, ")", pos++));
                    case '+', '-', '*', '/', '%', '^' -> {
                        int start = pos;
                        pos++;
                        if (c == '*' && pos < input.length() && input.charAt(pos) == '*') pos++; // handle **
                        tokens.add(new Token(Token.Type.OPERATOR, input.substring(start, pos), start));
                    }
                    default -> throw new RuntimeException("Unknown character: " + c + " at pos " + pos);
                }
            }
        }
        tokens.add(new Token(Token.Type.EOF, "", pos));
        return tokens;
    }
}

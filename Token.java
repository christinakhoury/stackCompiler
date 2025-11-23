public class Token {
    public enum Type {
        NUMBER, IDENTIFIER, ASSIGNMENT, OPERATOR,
        SEMICOLON, LPAREN, RPAREN, EOF
    }

    public Type type;
    public String value;
    public int pos;

    public Token(Type type, String value, int pos) {
        this.type = type;
        this.value = value;
        this.pos = pos;
    }

    @Override
    public String toString() {
        return type + "(" + value + ") at " + pos;
    }
}

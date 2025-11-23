public class UnaryOpNode extends ASTNode {
    public String operator;
    public ASTNode expr;

    public UnaryOpNode(String operator, ASTNode expr) {
        this.operator = operator;
        this.expr = expr;
    }
}

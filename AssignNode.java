public class AssignNode extends ASTNode {
    public String variable;
    public ASTNode expression;

    public AssignNode(String variable, ASTNode expression) {
        this.variable = variable;
        this.expression = expression;
    }
}

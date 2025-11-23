public class BinaryOpNode extends ASTNode {
    public String operator;
    public ASTNode left, right;

    public BinaryOpNode(String operator, ASTNode left, ASTNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }
}

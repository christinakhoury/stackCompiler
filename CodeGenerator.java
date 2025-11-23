import java.util.*;

public class CodeGenerator {

    private final List<String> instructions = new ArrayList<>();

    public List<String> generate(ASTNode node) {
        visit(node);
        return instructions;
    }

    private void visit(ASTNode node) {
        if (node instanceof NumberNode) {
            instructions.add("PUSH " + ((NumberNode) node).value);
        } else if (node instanceof IdentifierNode) {
            instructions.add("PUSH " + ((IdentifierNode) node).name);
        } else if (node instanceof UnaryOpNode) {
            UnaryOpNode u = (UnaryOpNode) node;
            visit(u.expr);
            if (u.operator.equals("-")) instructions.add("NEG");
        } else if (node instanceof BinaryOpNode) {
            BinaryOpNode b = (BinaryOpNode) node;
            visit(b.left);
            visit(b.right);
            switch (b.operator) {
                case "+" -> instructions.add("ADD");
                case "-" -> instructions.add("SUB");
                case "*" -> instructions.add("MUL");
                case "/" -> instructions.add("DIV");
                case "^", "**" -> instructions.add("POW");
                case "%" -> instructions.add("MOD");
            }
        } else if (node instanceof AssignNode) {
            AssignNode a = (AssignNode) node;
            visit(a.expression);
            instructions.add("STORE " + a.variable);
        }
    }
}

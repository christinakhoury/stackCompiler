import java.util.*;

public class Interpreter {
    public static void main(String[] args) {
        String input = "a=10; b=5; c=a/b; d=10*2; e=2^3; f=10%3; g=-5; h=2**3;";

        // 1️⃣ Tokenize
        Tokenizer tokenizer = new Tokenizer(input);
        List<Token> tokens = tokenizer.tokenize();

        System.out.println("Tokens:");
        for (Token t : tokens) System.out.println(t);

        // 2️⃣ Parse AST
        ParserAST parserAST = new ParserAST(tokens);
        List<ASTNode> statements = parserAST.parseStatements();

        // 3️⃣ Generate Instructions
        CodeGenerator codeGen = new CodeGenerator();
        List<String> instructions = new ArrayList<>();
        for (ASTNode stmt : statements) instructions.addAll(codeGen.generate(stmt));

        System.out.println("\nGenerated Instructions:");
        for (String instr : instructions) System.out.println(instr);

        // 4️⃣ Execute Instructions
        Map<String, Double> variables = new HashMap<>();
        Stack<Double> stack = new Stack<>();
        for (String instr : instructions) {
            String[] parts = instr.split(" ");
            switch (parts[0]) {
                case "PUSH" -> {
                    try {
                        stack.push(Double.parseDouble(parts[1])); // if number
                    } catch (NumberFormatException e) {
                        // if variable, get its value
                        if (!variables.containsKey(parts[1])) {
                            throw new RuntimeException("Variable " + parts[1] + " not defined.");
                        }
                        stack.push(variables.get(parts[1]));
                    }
                }
                case "ADD" -> stack.push(stack.pop() + stack.pop());
                case "SUB" -> { double b = stack.pop(), a = stack.pop(); stack.push(a - b); }
                case "MUL" -> stack.push(stack.pop() * stack.pop());
                case "DIV" -> { double b = stack.pop(), a = stack.pop(); stack.push(a / b); }
                case "POW" -> { double exp = stack.pop(), base = stack.pop(); stack.push(Math.pow(base, exp)); }
                case "MOD" -> { double b = stack.pop(), a = stack.pop(); stack.push(a % b); }
                case "NEG" -> stack.push(-stack.pop());
                case "STORE" -> variables.put(parts[1], stack.pop());
            }
        }

        System.out.println("\nVariables:");
        variables.forEach((k,v) -> System.out.println(k + " = " + v));
    }
}

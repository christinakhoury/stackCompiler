import java.util.*;

public class Interpreter {
    public static void main(String[] args) {
        // ✅ Example programs (3 small tests)
        String[] testInputs = {
                "a=10+2; b=5-3; c=a+b; d=a-b;",
                "x=2; y=3; z=x^y; w=10%3; v=-z;",
                "a=10; b=5; c=(a*b)+(a/b); d=(258/2*54)-70*30;",
                "i= (3*2/6*9)*8/9; j=4*5*(5/5);",
        };

        // Run all three examples
        for (int t = 0; t < testInputs.length; t++) {
            System.out.println("\n===== Test Program " + (t + 1) + " =====");
            String input = testInputs[t];

            // 1️⃣ Tokenize
            Tokenizer tokenizer = new Tokenizer(input);
            List<Token> tokens = tokenizer.tokenize();
            System.out.println("\nTokens:");
            for (Token token : tokens) System.out.println(token);

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
}

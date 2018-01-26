import java.util.*;

/**
 * Created by Rasoul on 1/25/2018.
 */
public class Parser {

    ParseTable parseTable;
    SymbolTable parentTable;
    SymbolTable currentSymbolTable;
    ArrayList<SymbolTable> scopes;
    Stack<Symbol> parseStack;
    ErrorHandler errorHandler;

    Scanner scanner;

    Grammar grammar;

    String programSrc = "project/src/sample.txt";

    Token currentToken;

    IntermediateCodeGenerator intermediateCodeGenerator;

    Parser(){
        parseTable = new ParseTable();
        parentTable = new SymbolTable("package");
        currentSymbolTable = parentTable;
        parseStack = new Stack<>();
        scopes = new ArrayList<>();
        errorHandler = new ErrorHandler(parseStack);
        scanner = new Scanner(programSrc, parentTable, errorHandler);
        grammar = new Grammar();
        intermediateCodeGenerator = new IntermediateCodeGenerator();

        parseStack.push(grammar.startSymbol);
        startParse();
    }

    public static void main(String[] args) {
        new Parser();
        System.out.println("Done");
    }

    private void startParse() {
        currentToken = scanner.getNextToken();
//        System.out.println(grammar.getRule(59).LHS);
//        System.out.println(parseTable.get(grammar.getRule(59).LHS, "identifier"));
//        return;
//    }
//    private void func(){
        System.out.println(currentToken);
        while(true){
            String s = currentToken.getFirst();
            if (Objects.equals(currentToken.getFirst(), "keyword"))
                s = currentToken.getSecond();
            else
                s = currentToken.getFirst();
            System.out.println(parseStack);
            System.out.println("Top: " + parseStack.peek().toString());
            System.out.println("Lookahead: " + s);
            if (parseStack.empty())
                break;
            switch (parseStack.peek().type){
                case TERMINAL:
                    match(s);
                    break;
                case NON_TERMINAL:
                    updateStack(s);
                    break;
                case ACTION_SYMBOL:
//                    parseStack.pop();
                    doAction();
                    break;
            }
        }

    }

    private void updateStack(String lookahead) {
        Symbol prevTopOfStack = parseStack.pop();

        if (parseTable.get(prevTopOfStack,lookahead) == null){
            error(1);
        }
        ArrayList<Symbol> RHS = parseTable.get(prevTopOfStack,lookahead);
        if (RHS == null)
            return;
        for (int i = RHS.size() - 1; i >= 0; i--){
            parseStack.push(RHS.get(i));
        }
    }

    private void match(String lookahead){

        if (Objects.equals(lookahead, parseStack.peek().name)){
            parseStack.pop();
            currentToken = scanner.getNextToken();
        }
        else
            error(2);
    }

    private void doAction() {
        Symbol prevTopOfStack = parseStack.pop();
        int index;
        Row row;
        switch (prevTopOfStack.name){
            case "#set_declaration":
                scanner.setDefinition(true);
                break;
            case "#reset_declaration":
                scanner.setDefinition(false);
                break;
            case "#put_in_current_table":
                System.out.println(currentToken.getSecond());
                int rowIndex = Integer.parseInt(currentToken.getSecond().split(" ")[2]);
                row = currentSymbolTable.getRows().get(rowIndex);
                row.setType("class");
                break;
            case "#make_symbol_table":
//                System.out.println(currentToken.getSecond());
                SymbolTable s = new SymbolTable(currentToken.getSecond().split(" ")[1]);
                s.setParent(currentSymbolTable);
                intermediateCodeGenerator.scopeStack.push(s);
                scopes.add(s);
                break;
            case "#scope_in":
                currentSymbolTable = intermediateCodeGenerator.scopeStack.pop();
                break;
            case "#scope_out":
                currentSymbolTable = currentSymbolTable.getParent();
                break;
            case "#push":
                intermediateCodeGenerator.semanticStack.push(currentToken.getSecond());
                break;
            case "#set_type_address":
                String type = (String) intermediateCodeGenerator.semanticStack.pop();
                index = Integer.parseInt(currentToken.getSecond().split(" ")[2]);

                row = currentSymbolTable.getRows().get(index);
                row.setType(type);
                row.setAddress(intermediateCodeGenerator.getVariableAddress());
                break;
            case "#pid":
                index = Integer.parseInt(currentToken.getSecond().split(" ")[2]);
                row = currentSymbolTable.getRows().get(index);
                intermediateCodeGenerator.semanticStack.push(row.getAddress());
                break;
            case "#assign":
                assign();
                break;
            case "#add":
                add();
                break;
            case "#sub":
                sub();
                break;



        }
    }

    private void assign() {
        Integer src = (int) intermediateCodeGenerator.semanticStack.pop();
        Integer dst = (int) intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.write("ASSIGN", src.toString(), dst.toString(), "");
    }

    private void add(){
        Integer src1 = (int) intermediateCodeGenerator.semanticStack.pop();
        Integer src2 = (int) intermediateCodeGenerator.semanticStack.pop();
        Integer dst = intermediateCodeGenerator.getTemp();
        intermediateCodeGenerator.write("ADD", src1.toString(), src2.toString(), dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst);
    }

    private void sub(){
        Integer src1 = (int) intermediateCodeGenerator.semanticStack.pop();
        Integer src2 = (int) intermediateCodeGenerator.semanticStack.pop();
        Integer dst = intermediateCodeGenerator.getTemp();
        intermediateCodeGenerator.write("SUB", src2.toString(), src1.toString(), dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst);
    }


    private void error(int code){
        System.out.println("Error " + code);
    }

}

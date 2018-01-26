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
                Row row = currentSymbolTable.getRows().get(rowIndex);
                row.setType("class");
                break;
            case "#make_symbol_table":
                System.out.println(currentToken.getSecond());
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

        }
    }




    private void error(int code){
        System.out.println("Error " + code);
    }

}

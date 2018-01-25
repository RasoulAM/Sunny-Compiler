import java.util.*;

/**
 * Created by Rasoul on 1/25/2018.
 */
public class Parser {

    ParseTable parseTable;
    SymbolTable parentTable;
    ArrayList<SymbolTable> scopes;
    Stack<Symbol> parseStack;
    ErrorHandler errorHandler;

    Scanner scanner;

    Grammar grammar;

    String programSrc = "project/src/sample.txt";

    Token currentToken;

    Parser(){
        parseTable = new ParseTable();
        parentTable = new SymbolTable();
        parseStack = new Stack<>();
        scopes = new ArrayList<>();
        errorHandler = new ErrorHandler(parseStack);
        scanner = new Scanner(programSrc, parentTable, errorHandler);
        grammar = new Grammar();

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
            case "#a":

                break;
            case "#b":

                break;
        }
    }




    private void error(int code){
        System.out.println("Error " + code);
    }

}

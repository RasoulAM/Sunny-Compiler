import java.util.*;

/**
 * Created by Rasoul on 1/25/2018.
 */
public class Parser {

    ParseTable parseTable;
    SymbolTable parentTable;
    ArrayList<SymbolTable> scopes;
    Scanner scanner;

    Grammar grammar;

    Stack<Symbol> parseStack;

    String programSrc = "src";

    Token currentToken;

    Parser(){
        parseTable = new ParseTable();
        parentTable = new SymbolTable();
        scopes = new ArrayList<>();

        scanner = new Scanner(programSrc,parentTable);
        grammar = new Grammar();

        parseStack = new Stack<>();
        parseStack.push(grammar.startSymbol);
        startParse();
    }

    private void startParse() {
        currentToken = scanner.getNextToken();
        while(true){
            if (parseStack.empty())
                break;
            switch (parseStack.peek().type){
                case TERMINAL:
                    match(currentToken.getFirst());
                    break;
                case NON_TERMINAL:
                    updateStack(currentToken.getFirst());
                    break;
                case ACTION_SYMBOL:
                    doAction();
                    break;
            }
        }

    }

    private void updateStack(String lookahead) {
        Symbol prevTopOfStack = parseStack.pop();
        if (parseTable.get(prevTopOfStack,lookahead) == null)
            error();
        ArrayList<Symbol> RHS = parseTable.get(prevTopOfStack,lookahead).RHS;
        for (int i = RHS.size() - 1; i >= 0; i--){
            parseStack.push(RHS.get(i));
        }
    }

    private void match(String lookahead){
        if (Objects.equals(lookahead, parseStack.peek().name)){
            parseStack.pop();
        }
        else
            error();
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




    private void error(){
        System.out.println("Error");
    }

}

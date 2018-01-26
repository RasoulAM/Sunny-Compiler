import java.util.*;

/**
 * Created by Rasoul on 1/25/2018.
 */
public class Parser {

    ParseTable parseTable;
    SymbolTable parentTable;
//    SymbolTable currentSymbolTable;
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
//        scanner.setCurrentSymbolTable(parentTable);
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
            String s;
            if (Objects.equals(currentToken.getFirst(), "keyword"))
                s = currentToken.getSecond();
            else
                s = currentToken.getFirst();
            if (parseStack.empty())
                break;
            System.out.println(parseStack);
            System.out.println("Top: " + parseStack.peek().toString());
            System.out.println("Lookahead: " + s);

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
        for (int i = 0; i < intermediateCodeGenerator.getIndex(); i++){
            System.out.println(i + ": " + intermediateCodeGenerator.programBlock[i]);
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
            if (!Objects.equals(lookahead, "EOF"))
                currentToken = scanner.getNextToken();
        }
        else
            error(2);
    }

    private void doAction() {
        Symbol prevTopOfStack = parseStack.pop();
        int index;
        Row row;
        System.out.println(intermediateCodeGenerator.semanticStack);
        switch (prevTopOfStack.name){
            case "#set_declaration":
                scanner.setDefinition(true);
                break;
            case "#reset_declaration":
                scanner.setDefinition(false);
                break;
            case "#put_in_current_table":
                int rowIndex = Integer.parseInt(currentToken.getSecond().split(" ")[2]);
                row = scanner.getCurrentSymbolTable().getRows().get(rowIndex);
                row.setType("class");
                break;
            case "#make_symbol_table":
//                System.out.println(currentToken.getSecond());
                SymbolTable s = new SymbolTable(currentToken.getSecond().split(" ")[1]);
                s.setParent(scanner.getCurrentSymbolTable());
                intermediateCodeGenerator.scopeStack.push(s);
                scopes.add(s);
                break;
            case "#scope_in":
                scanner.setCurrentSymbolTable(intermediateCodeGenerator.scopeStack.pop());
                break;
            case "#scope_out":
                scanner.setCurrentSymbolTable(scanner.getCurrentSymbolTable().getParent());
                break;
            case "#push":
                intermediateCodeGenerator.semanticStack.push(currentToken.getSecond());
                break;
            case "#set_type_address":
                String type = (String) intermediateCodeGenerator.semanticStack.pop();
                index = Integer.parseInt(currentToken.getSecond().split(" ")[2]);
                row = scanner.getCurrentSymbolTable().getRows().get(index);
                row.setType(type);
                row.setAddress(intermediateCodeGenerator.getVariableAddress());
                break;
            case "#pid":
                index = Integer.parseInt(currentToken.getSecond().split(" ")[2]);
                row = scanner.getCurrentSymbolTable().getRows().get(index);
                intermediateCodeGenerator.semanticStack.push(row.getAddress().toString());
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
            case "#push_number":
                push_number();
                break;
            case "#push_bool":
                push_bool();
                break;
            case "#save":
                save();
                break;
            case "#jpf":
                jpf();
                break;
            case "#fill":
                fill();
                break;
            case "#push_line":
                push_line();
                break;
            case "#while":
                while_action();
                break;
            case "#step_for":
                step_for();
        }
    }

    private void step_for() {
        intermediateCodeGenerator.write("ADD", intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 3).toString(), intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 2).toString(),intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 3).toString());
        intermediateCodeGenerator.write("JP", intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 4).toString(), "", "");
        Integer pbIndex = (Integer) intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 1);
        intermediateCodeGenerator.writeWithDst(pbIndex,"JPF",intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 5).toString(),intermediateCodeGenerator.getIndex().toString(),"");
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
    }

    private void while_action() {
        Integer jmpTarget = (Integer) intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 3);
        intermediateCodeGenerator.write("JP",jmpTarget.toString(),"","");
        Integer pbIndex = (Integer) intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 1);
        String jpfComparator = (String) intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 2);
        intermediateCodeGenerator.writeWithDst(pbIndex, "JPF", jpfComparator, intermediateCodeGenerator.getIndex().toString(), "");
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
    }

    private void push_line() {
        System.out.println("DDDDDDDDDDDDDDDDDD");
        intermediateCodeGenerator.semanticStack.push(intermediateCodeGenerator.getIndex());
    }

    private void fill() {
        Integer pbIndex = (int) intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 1);
        intermediateCodeGenerator.writeWithDst(pbIndex,"JP",intermediateCodeGenerator.getIndex().toString(),"","");
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
    }

    private void jpf() {
        Integer pbIndex = (int) intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 2);
        String dst = (String) intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 3);
        intermediateCodeGenerator.writeWithDst(pbIndex, "JPF", dst, intermediateCodeGenerator.getIndex().toString(),"");
    }

    private void save() {
        intermediateCodeGenerator.semanticStack.push(intermediateCodeGenerator.getIndex());
        intermediateCodeGenerator.incIndex();
    }

    private void push_bool() {
        String a = "0";
        if (currentToken.getSecond().equals("true"))
            a = "1";
        intermediateCodeGenerator.semanticStack.push("#" + a);
    }

    private void push_number() {
        intermediateCodeGenerator.semanticStack.push("#" + currentToken.getSecond());
    }

    private void assign() {
        String src = (String) intermediateCodeGenerator.semanticStack.pop();
        String dst = (String) intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.write("ASSIGN", src, dst, "");
    }

    private void add(){
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        Integer dst = intermediateCodeGenerator.getTemp();
        intermediateCodeGenerator.write("ADD", src1, src2, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void sub(){
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        Integer dst = intermediateCodeGenerator.getTemp();
        intermediateCodeGenerator.write("SUB", src2.toString(), src1.toString(), dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }


    private void error(int code){
        System.out.println("Error " + code);
    }

}

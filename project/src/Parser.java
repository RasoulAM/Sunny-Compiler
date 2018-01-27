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

    String programSrc;

    Token currentToken;

    IntermediateCodeGenerator intermediateCodeGenerator;

    Parser(){
        boolean isWindows = System.getProperty("os.name").contains("Windows");
        String workingDir;
        if (isWindows)
            workingDir = "project";
        else
            workingDir = ".";
        programSrc = workingDir + "/src/sample.txt";
        parseTable = new ParseTable();
        parentTable = new SymbolTable("package");
//        scanner.setCurrentSymbolTable(parentTable);
        parseStack = new Stack<>();
        scopes = new ArrayList<>();
        errorHandler = new ErrorHandler(this);
        scanner = new Scanner(programSrc, parentTable, errorHandler);
        grammar = new Grammar();
        intermediateCodeGenerator = new IntermediateCodeGenerator();

        parseStack.push(grammar.startSymbol);
        startParse();


    }

    public static void main(String[] args) {
        Parser p = new Parser();
        for (SymbolTable s: p.scopes){
            System.out.print(s.getName() + " ");
            if (s.getParent() != null)
                System.out.print(s.getParent().getName());
            System.out.println();
        }
        System.out.println("Done");
    }

    private void startParse() {
        currentToken = scanner.getNextToken();
//        System.out.println(currentToken);
        while(true){
            String s = currentToken.getComparable();
//            if (Objects.equals(currentToken.getFirst(), "keyword"))
//                s = currentToken.getSecond();
//            else
//                s = currentToken.getFirst();
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
                    doAction();
                    break;
            }

        }
        for (int i = 0; i < intermediateCodeGenerator.getIndex(); i++){
            System.out.println(i + "\t" + intermediateCodeGenerator.programBlock[i]);
        }
    }

    private void updateStack(String lookahead) {
        Symbol prevTopOfStack = parseStack.pop();

        if (parseTable.get(prevTopOfStack,lookahead) == null){
            boolean next = errorHandler.emptyParseTable(scanner.getCurrentLineNumber(), lookahead);
            if (next)
                currentToken = scanner.getNextToken();
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
        else{
            errorHandler.missingToken(scanner.getCurrentLineNumber(),parseStack.pop().name);
        }
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
                System.out.println("ZZZZZ " + s.getName());
//                s.setParent(scanner.getCurrentSymbolTable());
                intermediateCodeGenerator.scopeStack.push(s);
                scopes.add(s);
                break;
            case "#scope_in":
                SymbolTable sss = intermediateCodeGenerator.scopeStack.pop();
                intermediateCodeGenerator.scopeStack.push(scanner.getCurrentSymbolTable());
                scanner.setCurrentSymbolTable(sss);
                break;
            case "#scope_out":
                scanner.setCurrentSymbolTable(intermediateCodeGenerator.scopeStack.pop());
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
            case "#mult":
                multiply();
                break;
            case "#and":
                and();
                break;
            case "#equal":
                equal();
                break;
            case "#less_than":
                less_than();
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
                break;
            case "#print":
                print();
                break;
            case "#set_parent":
                set_parent();
                break;
            case "#set_no_parent":
                set_no_parent();
                break;
        }
    }

    private void set_no_parent() {
//        intermediateCodeGenerator.scopeStack.pop();
    }

    private void set_parent() {
        SymbolTable symbolTable = intermediateCodeGenerator.scopeStack.peek();
        String fatherName = currentToken.getSecond().split(" ")[1];
        for (SymbolTable s:scopes) {
            if (s.getName().equals(fatherName)){
                symbolTable.setParent(s);
                break;
            }
        }
    }

    private void print() {
        intermediateCodeGenerator.write("PRINT", intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 1).toString(), "", "");
        intermediateCodeGenerator.semanticStack.pop();
    }

    private void step_for() {
        intermediateCodeGenerator.write("ADD", intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 3).toString(), intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 2).toString(),intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 3).toString());
        intermediateCodeGenerator.write("JP", intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 5).toString(), "", "");
        Integer pbIndex = (Integer) intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 1);
        intermediateCodeGenerator.writeWithDst(pbIndex,"JPF",intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 4).toString(),intermediateCodeGenerator.getIndex().toString(),"");
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
    }

    private void while_action() {
        Integer jmpTarget = (Integer) intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 3);
        intermediateCodeGenerator.write("JP", jmpTarget.toString(),"","");
        Integer pbIndex = (Integer) intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 1);
        String jpfComparator = (String) intermediateCodeGenerator.semanticStack.get(intermediateCodeGenerator.semanticStack.size() - 2);
        intermediateCodeGenerator.writeWithDst(pbIndex, "JPF", jpfComparator, intermediateCodeGenerator.getIndex().toString(), "");
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
    }

    private void push_line() {
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
        intermediateCodeGenerator.write("SUB", src2, src1, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void multiply() {
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        Integer dst = intermediateCodeGenerator.getTemp();
        intermediateCodeGenerator.write("MULT", src2, src1, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void less_than() {
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        Integer dst = intermediateCodeGenerator.getTemp();
        intermediateCodeGenerator.write("LT", src2, src1, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void equal() {
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        Integer dst = intermediateCodeGenerator.getTemp();
        intermediateCodeGenerator.write("EQ", src2, src1, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void and() {
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        Integer dst = intermediateCodeGenerator.getTemp();
        intermediateCodeGenerator.write("AND", src2, src1, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void error(int code){
        System.out.println("Error " + code);
    }

}

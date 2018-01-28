import java.util.*;

/**
 * Created by Rasoul on 1/25/2018.
 */
public class Parser {

    ParseTable parseTable;
    SymbolTable parentTable;
    ArrayList<SymbolTable> scopes;
    Stack<Symbol> parseStack;
    ArrayList<String> temporary = new ArrayList<>();
    ErrorHandler errorHandler;
    Scanner scanner;
    Grammar grammar;
    String programName = "sample.txt";
    Token currentToken;
    IntermediateCodeGenerator intermediateCodeGenerator;

    Parser(){
        boolean isWindows = System.getProperty("os.name").contains("Windows");
        String programSrc;
        if (isWindows)
            programSrc = "project/src/" + programName;
        else
            programSrc = "./src/" + programName;
        parseTable = new ParseTable();
        parentTable = new SymbolTable("package");
        parseStack = new Stack<>();
        scopes = new ArrayList<>();
        scopes.add(parentTable);
        errorHandler = new ErrorHandler(this);
        scanner = new Scanner(programSrc, parentTable, errorHandler);
        grammar = new Grammar();
        intermediateCodeGenerator = new IntermediateCodeGenerator();
        parseStack.push(grammar.startSymbol);
        startParse();
    }

    // return type of desired address
    public String getType(String address){
        for (int i = 0; i < scopes.size(); i++) {
            for (int j = 0; j < scopes.get(i).getRows().size(); j++) {
                if (scopes.get(i).getRows().get(j).getAddress() != null &&
                        scopes.get(i).getRows().get(j).getAddress().toString().equals(address)){
                    return scopes.get(i).getRows().get(j).getType();
                }

            }
        }
        for (int i = 0; i < temporary.size(); i++) {
            if (temporary.get(i).split(" ")[0].equals(address)){
                return temporary.get(i).split(" ")[1];
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Parser p = new Parser();
//        for (SymbolTable s: p.scopes){
//            System.out.print(s.getName() + " ");
//            for (Row r:s.getRows()) {
//                System.out.println("     " + r);
//            }
//        }
        System.out.println("Done");
//        System.out.println(p.scopes.get(1));
    }

    private void startParse() {
        currentToken = scanner.getNextToken();
        while(true){
            String s = currentToken.getComparable();
            if (parseStack.empty())
                break;
//            System.out.println(parseStack);
//            System.out.println("Top: " + parseStack.peek().toString());
//            System.out.println("Lookahead: " + s);
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
        Symbol prevTopOfStack = parseStack.peek();

        if (parseTable.get(prevTopOfStack,lookahead) == null){
            boolean next = errorHandler.emptyParseTable(scanner.getCurrentLineNumber(), lookahead, prevTopOfStack);
            if (next)
                currentToken = scanner.getNextToken();
//            error(1);
        }
        ArrayList<Symbol> RHS = parseTable.get(prevTopOfStack,lookahead);
        parseStack.pop();
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
        switch (prevTopOfStack.name){
            case "#set_declaration":
                scanner.setDefinition(true);
                break;
            case "#reset_declaration":
                scanner.setDefinition(false);
                break;
            case "#put_class_in_current_table":
                put_class_in_current_table();
                break;
            case "#make_symbol_table":
                make_symbol_table();
                break;
            case "#scope_in":
                SymbolTable sss = intermediateCodeGenerator.scopeStack.pop();
                intermediateCodeGenerator.scopeStack.push(scanner.getCurrentSymbolTable());
                scanner.setCurrentSymbolTable(sss);
                break;
            case "#scope_out":
                scanner.setCurrentSymbolTable(intermediateCodeGenerator.scopeStack.pop());
                break;
            case "#push_type":
                intermediateCodeGenerator.semanticStack.push(currentToken.getSecond());
                break;
            case "#push":
                intermediateCodeGenerator.semanticStack.push(currentToken.getSecond().split(" ")[1]);
                break;
            case "#pop":
                intermediateCodeGenerator.semanticStack.pop();
                break;
            case "#set_type_address":
                set_type_address();
                break;
            case "#par_set_type_address":
                par_set_type_address();
                break;
            case "#pid":
                pid();
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
            case "#set_class_parent":
                set_class_parent();
                break;
            case "#set_method_parent":
                set_method_parent();
                break;
            case "#set_no_parent":
                set_no_parent();
                break;
            case "#put_method_in_current_table":
                put_method_in_current_table();
                break;
            case "#fill_return":
                fill_return();
                break;
            case "#push_change_scope":
                push_change_scope();
                break;
            case "#return_scope":
                return_scope();
                break;
            case "#set_counter":
                set_counter();
                break;
            case "#do_method":
                do_method();
                break;
            case "#get_parameter_address":
                get_parameter_address();
                break;
            case "#inc_counter":
                inc_counter();
                break;
            case "#main":
                main_jmp();
                break;
            case "#pop_counter":
                pop_counter();
                break;
        }
    }

    private void pop_counter() {
        Integer numOfGivenParameters = (Integer) intermediateCodeGenerator.semanticStack.pop();
        String methodName = (String) intermediateCodeGenerator.semanticStack.peek();
        Integer numOfActualParameters = 0;
        for (int i = 0; i <scopes.size() ; i++) {
            for (int j = 0; j < scopes.get(i).getRows().size(); j++) {
                if (scopes.get(i).getRows().get(j).getType().equals("method") &&
                        scopes.get(i).getRows().get(j).getName().equals(methodName) &&
                        scopes.get(i).getRows().get(j).getFunctionArgs().size() > numOfGivenParameters){
                    errorHandler.methodParNumberMisMatch(scanner.getCurrentLineNumber(), methodName, 1);
                }

            }
        }
    }

    private void main_jmp() {
        intermediateCodeGenerator.writeWithDst(0, "JP", intermediateCodeGenerator.getIndex().toString(), "", "");
    }

    private void inc_counter() {
        Integer counter = (Integer) intermediateCodeGenerator.semanticStack.pop();
        counter = counter + 1;
        intermediateCodeGenerator.semanticStack.push(counter);
    }

    private void get_parameter_address(){
        Integer counter = (Integer) intermediateCodeGenerator.semanticStack.pop();
        String methodName = (String) intermediateCodeGenerator.semanticStack.peek();
        SymbolTable father = intermediateCodeGenerator.scopeStack.peek();
        Integer index = father.findRowByName(methodName);
        while (index == -1){
            father = father.getParent();
            index = father.findRowByName(methodName);
        }
        intermediateCodeGenerator.scopeStack.pop();
        intermediateCodeGenerator.scopeStack.push(father);
        String parAddress = "$$";
        if (counter < father.getRows().get(index).getFunctionArgs().size())
            parAddress = father.getRows().get(index).getFunctionArgs().get(counter).getMemory();
        else
            errorHandler.methodParNumberMisMatch(scanner.getCurrentLineNumber(), methodName, 0);
        intermediateCodeGenerator.semanticStack.push(counter);
        intermediateCodeGenerator.semanticStack.push(parAddress);
    }

    private void do_method() {
        String methodName = (String) intermediateCodeGenerator.semanticStack.pop();
        SymbolTable father = intermediateCodeGenerator.scopeStack.peek();
        Integer index = father.findRowByName(methodName);
        intermediateCodeGenerator.write("ASSIGN", "#" + (intermediateCodeGenerator.getIndex() + 2 ), father.getRows().get(index).getReturnAddressAddress().toString(),"" );
        intermediateCodeGenerator.write("JP", (String) intermediateCodeGenerator.semanticStack.pop(), "", "");
        intermediateCodeGenerator.semanticStack.push(father.getRows().get(index).getRetValueAddress().toString());
    }

    private void set_counter() {
        Integer counter = 0;
        intermediateCodeGenerator.semanticStack.push(counter);
    }

    private void return_scope() {
        SymbolTable temp = scanner.getCurrentSymbolTable();
        scanner.setCurrentSymbolTable(intermediateCodeGenerator.scopeStack.pop());
        intermediateCodeGenerator.scopeStack.push(temp);
    }

    private void push_change_scope() {
        intermediateCodeGenerator.scopeStack.push(scanner.getCurrentSymbolTable());
        String nameOfNewScope = (String) intermediateCodeGenerator.semanticStack.pop();
        intermediateCodeGenerator.semanticStack.pop();
        for (SymbolTable s: scopes){
            if (s.getName().equals(nameOfNewScope)){
                scanner.setCurrentSymbolTable(s);
            }
//            System.out.println("AAAAAA ");
        }



    }

    private void par_set_type_address() {
        String type = (String) intermediateCodeGenerator.semanticStack.pop();

        SymbolTable parent = scanner.getCurrentSymbolTable().getParent();
        Integer indexInParent = parent.findRowByName(scanner.getCurrentSymbolTable().getName());
        Integer address = intermediateCodeGenerator.getVariableAddress();
        parent.getRows().get(indexInParent).addArg(type,address.toString());


        Integer index = Integer.parseInt(currentToken.getSecond().split(" ")[2]);
        Row row = scanner.getCurrentSymbolTable().getRows().get(index);
        row.setType(type);
        row.setAddress(address);
    }

    private void set_type_address() {
        String type = (String) intermediateCodeGenerator.semanticStack.pop();
        Integer index = Integer.parseInt(currentToken.getSecond().split(" ")[2]);
        Row row = scanner.getCurrentSymbolTable().getRows().get(index);
        row.setType(type);
        row.setAddress(intermediateCodeGenerator.getVariableAddress());
    }

    private void pid() {
//        System.out.println(currentToken);
        String targetSymbolTableName = currentToken.getSecond().split(" ")[0];
        Integer index = Integer.parseInt(currentToken.getSecond().split(" ")[2]);
        SymbolTable targetSymbolTable = scanner.getCurrentSymbolTable();
        for (SymbolTable s: scopes){
//            System.out.print(s.getName() +"\t");
            if (targetSymbolTableName.equals(s.getName())){
                targetSymbolTable = s;
            }
        }
        Row row = targetSymbolTable.getRows().get(index);
        if (row.getAddress() != null)
            intermediateCodeGenerator.semanticStack.push(row.getAddress().toString());
        else
            intermediateCodeGenerator.semanticStack.push("0");
    }


    private void fill_return() {
//        System.out.println(scanner.getCurrentSymbolTable().getName());
        Integer rowIndex = scanner.getCurrentSymbolTable().getParent().findRowByName(scanner.getCurrentSymbolTable().getName());
        Row thisMethod = scanner.getCurrentSymbolTable().getParent().getRows().get(rowIndex);
        Integer address = thisMethod.getRetValueAddress();
        String toBeAssigned = intermediateCodeGenerator.semanticStack.pop().toString();
        String srcType = thisMethod.getReturnValueType();
        String dstType = getType(toBeAssigned);
        if (srcType != null && dstType != null && !srcType.equals(dstType)){
            errorHandler.FunctionReturnTypeMisMatch(scanner.getCurrentLineNumber(), thisMethod.getName());
        }

        intermediateCodeGenerator.write("ASSIGN", toBeAssigned, address.toString(), "");

        intermediateCodeGenerator.write("JP", "@" + thisMethod.getReturnAddressAddress().toString(),"", "");


    }

    private void put_method_in_current_table() {
        Row row;
//        System.out.println(currentToken);
        int rowIndex = Integer.parseInt(currentToken.getSecond().split(" ")[2]);
        row = scanner.getCurrentSymbolTable().getRows().get(rowIndex);
        row.setType("method");
        Integer methodAddress = intermediateCodeGenerator.getIndex();
        row.setAddress(methodAddress);
        Integer retAddress = intermediateCodeGenerator.getReturnValueAddress();
        row.setRetValueAddress(retAddress);
        row.setReturnValueType((String) intermediateCodeGenerator.semanticStack.pop());
        Integer returnAddressAddress = intermediateCodeGenerator.getTemp();
        row.setReturnAddressAddress(returnAddressAddress);

    }

    private void set_no_parent() {
//        intermediateCodeGenerator.scopeStack.pop();
    }


    private void set_method_parent() {

        SymbolTable symbolTable = intermediateCodeGenerator.scopeStack.peek();
        symbolTable.setParent(scanner.getCurrentSymbolTable());
//        String fatherName = currentToken.getSecond().split(" ")[1];
//        for (SymbolTable s:scopes) {
//            if (s.getName().equals(fatherName)){
//                symbolTable.setParent(s);
//                break;
//            }
//        }
    }

    private void set_class_parent() {
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
        String a = "false";
        if (currentToken.getSecond().equals("true"))
            a = "true";
        intermediateCodeGenerator.semanticStack.push(a);
    }

    private void push_number() {
        intermediateCodeGenerator.semanticStack.push("#" + currentToken.getSecond());
    }

    private void assign() {
        String src = (String) intermediateCodeGenerator.semanticStack.pop();
        String dst = (String) intermediateCodeGenerator.semanticStack.pop();
        String srcType = getType(src);
        String dstType = getType(dst);
        if (srcType != null && dstType != null && !getType(src).equals(getType(dst))){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 5, 0);
        }
        if (!src.equals("$$") && !dst.equals("$$"))
            intermediateCodeGenerator.write("ASSIGN", src, dst, "");
    }

    private void add(){
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src1Type = getType(src1);
        String src2Type = getType(src2);
        if (src1Type!= null && src1Type.equals("boolean")){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 1, 0);
        }
        if (src2Type!= null && src2Type.equals("boolean")){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 1, 1);
        }
        Integer dst = intermediateCodeGenerator.getTemp();
        temporary.add(dst.toString() + " " + "int");
        intermediateCodeGenerator.write("ADD", src1, src2, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void sub(){
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src1Type = getType(src1);
        String src2Type = getType(src2);
        if (src1Type != null && src1Type.equals("boolean")){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 2, 0);
        }
        if (src2Type!= null && src2Type.equals("boolean")){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 2, 1);
        }
        Integer dst = intermediateCodeGenerator.getTemp();
        temporary.add(dst.toString() + " " + "int");
        intermediateCodeGenerator.write("SUB", src2, src1, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void multiply() {
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src1Type = getType(src1);
        String src2Type = getType(src2);
        if (src1Type != null && src1Type.equals("boolean")){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 3, 0);
        }
        if (src2Type!= null && src2Type.equals("boolean")){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 3, 1);
        }
        Integer dst = intermediateCodeGenerator.getTemp();
        temporary.add(dst.toString() + " " + "int");
        intermediateCodeGenerator.write("MULT", src2, src1, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void less_than() {
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src1Type = getType(src1);
        String src2Type = getType(src2);
        if (src1Type != null && src1Type.equals("boolean")){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 6, 0);
        }
        if (src2Type != null && src2Type.equals("boolean")){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 6, 1);
        }
        Integer dst = intermediateCodeGenerator.getTemp();
        temporary.add(dst.toString() + " " + "boolean");
        intermediateCodeGenerator.write("LT", src2, src1, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void equal() {
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src1Type = getType(src1);
        String src2Type = getType(src2);
        if (src1Type!= null && src2Type != null && !src1Type.equals(src2Type)){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 7, 0);
        }
        Integer dst = intermediateCodeGenerator.getTemp();
        temporary.add(dst.toString() + " " + "boolean");
        intermediateCodeGenerator.write("EQ", src2, src1, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void and() {
        String src1 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src2 = (String) intermediateCodeGenerator.semanticStack.pop();
        String src1Type = getType(src1);
        String src2Type = getType(src2);
        if (src1Type != null && src1Type.equals("int")){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 4, 0);
        }
        if (src2Type != null && src2Type.equals("int")){
            errorHandler.operandNotMatch(scanner.getCurrentLineNumber(), 4, 1);
        }
        Integer dst = intermediateCodeGenerator.getTemp();
        temporary.add(dst.toString() + " " + "boolean");
        intermediateCodeGenerator.write("AND", src2, src1, dst.toString());
        intermediateCodeGenerator.semanticStack.push(dst.toString());
    }

    private void put_class_in_current_table(){
        int rowIndex = Integer.parseInt(currentToken.getSecond().split(" ")[2]);
        Row row = scanner.getCurrentSymbolTable().getRows().get(rowIndex);
        row.setType("class");
    }

    private void make_symbol_table(){
        SymbolTable s = new SymbolTable(currentToken.getSecond().split(" ")[1]);
        intermediateCodeGenerator.scopeStack.push(s);
        s.setParent(scanner.getCurrentSymbolTable());
        scopes.add(s);
    }

    private void error(int code){
        System.out.println("Error " + code);
    }

}

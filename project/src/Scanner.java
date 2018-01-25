import java.io.*;
import java.util.ArrayList;

public class Scanner {
    // regex
    private static final  String letter = "[a-zA-Z]";
    private static final String digit = "[0-9]";
    private static final String other1 = "[^a-zA-Z0-9]";
    private static final String other2 = "[^0-9]";
    private static final String other3 = "[^=]";
    private static final String saniOther = "(.|\\s)";
    // keywords
    private static final ArrayList<String> keywords= new ArrayList<>();
    static {
        keywords.add("boolean");
        keywords.add("class");
        keywords.add("int");
        keywords.add("extends");
        keywords.add("for");
        keywords.add("if");
        keywords.add("else");
        keywords.add("main");
        keywords.add("public");
        keywords.add("false");
        keywords.add("true");
        keywords.add("static");
        keywords.add("void");
        keywords.add("while");
        keywords.add("return");
        keywords.add("System");
        keywords.add("out");
        keywords.add("println");
        keywords.add("EOF");
    }
    // effective on +=
    private static final ArrayList<String> effective = new ArrayList<>();
    static {
        effective.add("+");
        effective.add("-");
        effective.add("=");
        effective.add("==");
        effective.add("<");
        effective.add("*");
        effective.add("+=");
        effective.add(",");
        effective.add("(");
        effective.add("&&");
    }
    // address of program and the whole program to be compiled
    private String programAddress;
    private String program;
    private SymbolTable currentSymbolTable;
    // act as previous token
    private boolean prevHasEffect = false;
    private int currentState = 0;
    private String currentChar;
    private int lb = 0;
    private int lf = 0;
    private boolean done = false;
    private Token currentToken;

    //constructor
    public Scanner(String programAddress, SymbolTable currentSymbolTable){
        this.programAddress = programAddress;
        this.currentSymbolTable = currentSymbolTable;
        readProgram();
    }

    // parser will invoke this function on scanner
    public Token getNextToken(){
        done = false;
        currentChar = program.substring(lf, lf + 1);
        while (!done){
            nextState();
            currentChar = program.substring(lf, lf + 1);

        }
        return currentToken;
    }

    // compute next state using current state and current character and maybe
    public void nextState(){
        switch (currentState){
            case 0:
                if (currentChar.matches(letter)){
                    currentState = 1;
                    lf++;
                } else if(currentChar.matches("\\s")){
                    currentState = 0;
                    lf++;
                    lb = lf;
                } else if(currentChar.matches("[+|-]") && prevHasEffect){
                    currentState = 3;
                    lf++;
                } else if(currentChar.matches(digit)){
                    currentState = 4;
                    lf++;
                } else if(currentChar.matches("[*|,|;|(|)|{|}|.|<]")){
                    currentState = 8;
                    lf++;
                } else if(currentChar.matches("[&]")){
                    currentState = 11;
                    lf ++;
                } else if (currentChar.matches("[=]")){
                    currentState = 9;
                    lf ++;
                } else if (currentChar.matches("[+]") && !prevHasEffect){
                    currentState = 5;
                    lf ++;
                } else if (currentChar.matches("[-]") && !prevHasEffect){
                    currentState = 7;
                    lf++;
                }
                break;
            case 1:
                if (currentChar.matches(letter) || currentChar.matches(digit)){
                    currentState = 1;
                    lf++;
                } else if(currentChar.matches(other1)){
                    currentState = 2;
                    makeToken(0);
                }
                break;
            case 2:
                currentState = 0;
                done = true;
                break;
            case 3:
                if (currentChar.matches(digit)){
                    currentState = 4;
                    lf++;
                }
                break;
            case 4:
                if (currentChar.matches(digit)){
                    currentState = 4;
                    lf ++;
                } else if (currentChar.matches(other2)){
                    currentState = 2;
                    makeToken(1);
                }
                break;
            case 5:
                if (currentChar.matches(other3)){
                    currentState = 2;
                    makeToken(2);
                } else if (currentChar.matches("[=]")){
                    currentState = 6;
                    lf++;
                }
                break;
            case 6:
                if (currentChar.matches(saniOther)){
                    currentState = 2;
                    makeToken(2);
                }
                break;
            case 7:
                if (currentChar.matches(saniOther)){
                    currentState = 2;
                    makeToken(2);
                }
                break;
            case 8:
                if (currentChar.matches(saniOther)){
                    currentState = 2;
                    makeToken(2);
                }
                break;
            case 9:
                if (currentChar.matches(other3)){
                    currentState = 2;
                    makeToken(2);
                } else if (currentChar.matches("[=]")){
                    currentState = 10;
                    lf++;
                }
                break;
            case 10:
                if (currentChar.matches(saniOther)){
                    currentState = 2;
                    makeToken(2);
                }
                break;
            case 11:
                if (currentChar.matches("[&]")){
                    currentState = 12;
                    lf ++;
                }
                break;
            case 12:
                if (currentChar.matches(saniOther)){
                    currentState = 2;
                    makeToken(2);
                }
                break;
        }
    }

    public void makeToken(int code){
        String lookahead = "";
        lookahead = program.substring(lb, lf);
        lb = lf;
        switch (code){
            case 0:
                if (keywords.contains(lookahead)){
                    currentToken = new Token("keyword", lookahead);
                } else {
                    currentToken = new Token("identifier", currentSymbolTable.tokenHandler(lookahead));
                }
                prevHasEffect = false;
                break;
            case 1:
                currentToken = new Token("integer", lookahead);
                prevHasEffect = false;
                break;
            case 2:
                currentToken = new Token(lookahead, "");
                if (effective.contains(lookahead)){
                    prevHasEffect = true;
                } else {
                    prevHasEffect = false;
                }
        }
    }

    // reading entire program to buffer
    private void readProgram(){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(programAddress));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            program = sb.toString();
            program+="$";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void setCurrentSymbolTable(SymbolTable currentSymbolTable) {
        this.currentSymbolTable = currentSymbolTable;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner("src/sample.txt", new SymbolTable());
        for (int i = 0; i < 100; i++) {
            scanner.getNextToken();
            System.out.println(scanner.currentToken);
        }
    }
}
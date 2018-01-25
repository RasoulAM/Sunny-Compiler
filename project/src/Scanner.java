import java.io.*;
import java.util.HashMap;

public class Scanner {
    // regex
    private String letter = "[a-zA-Z]";
    private String digit = "[0-9]";
    private String other1 = "[^a-zA-Z0-9]";
    private String other2 = "[^0-9]";

    private String programAddress;
    private String program;
    private SymbolTable currentSymbolTable;
    private boolean prevIsOp = false;
    private int currentState = 0;
    private String currentChar;
    private int lb = 0;
    private int lf = 0;
    private String keywords = "boolean-class-int-extends-for-if-else-main-public-false-true-" +
            "static-void-while-return-EOF";
    private boolean done = false;
    private Token currentToken;

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
                } else if(currentChar.matches("[+|-]") && prevIsOp){
                    currentState = 3;
                    lf++;
                } else if(currentChar.matches(digit)){
                    currentState = 4;
                    lf++;
                }
                break;
            case 1:
                if (currentChar.matches(letter) || currentChar.matches(digit)){
                    currentState = 1;
                    lf++;
                } else if(currentChar.matches(other1)){
                    currentState = 2;
                    lf++;
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
                    currentState = 5;
                    lf ++;
                    makeToken(1);
                }
        }
    }

    public void makeToken(int code){
        String lookahead = "";
        lf --;
        lookahead = program.substring(lb, lf);
        lb = lf;
        switch (code){
            case 0:
                if (keywords.contains(lookahead)){
                    currentToken = new Token("keyword", lookahead);
                } else {
                    currentToken = new Token("id", currentSymbolTable.tokenHandler(lookahead));
                }
                prevIsOp = false;
                break;
            case 1:
                currentToken = new Token("integer", lookahead);
                prevIsOp = false;
                break;
        }
    }

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
        scanner.getNextToken();
        System.out.println(scanner.currentToken);
//        scanner.getNextToken();
//        System.out.println(scanner.currentToken);
//        scanner.getNextToken();
//        System.out.println(scanner.currentToken);
    }
}




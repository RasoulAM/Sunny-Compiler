import java.util.Stack;

/**
 * Created by Rasoul on 1/26/2018.
 */
public class IntermediateCodeGenerator {


    String[] programBlock = new String[100];
    int index = 0;
    int variableAddress = 100;
    int baseTempAddress = 500;

    Stack<Object> semanticStack = new Stack<>();
    Stack<SymbolTable> scopeStack = new Stack<>();

    public int getTemp(){
        int temp = baseTempAddress;
        baseTempAddress += 4;
        return temp;
    }

    public int getVariableAddress(){
        int temp = variableAddress;
        variableAddress += 4;
        return temp;
    }

    enum Command {
        ADD, SUB, AND, ASSIGN, EQ, JPF, JP, LT, MULT, NOT, PRINT
    }

    public void write(String command, String par1, String par2, String par3){
        String ans = "(" + command + ", " + par1 + ", " + par2 + ", " + par3 + ")";
        programBlock[index] = ans;
        index++;

    }




}

import java.util.Stack;

/**
 * Created by Rasoul on 1/26/2018.
 */
public class IntermediateCodeGenerator {


    String[] programBlock = new String[100];
    private Integer index = 1;
    int variableAddress = 100;
    int baseTempAddress = 500;
    int returnValueAddress = 2000;

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

    public int getReturnValueAddress(){
        int temp = returnValueAddress;
        returnValueAddress += 4;
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

    public void writeWithDst(int dst, String command, String par1, String par2, String par3){
        String ans = "(" + command + ", " + par1 + ", " + par2 + ", " + par3 + ")";
        programBlock[dst] = ans;
    }

    public void incIndex(){
        index++;
    }


    public Integer getIndex() {
        return index;
    }
}

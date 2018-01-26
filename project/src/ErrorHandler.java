import java.util.Stack;

public class ErrorHandler {
    private Stack<Symbol> parseStack;


    public ErrorHandler(Stack<Symbol> parseStack) {
        this.parseStack = parseStack;
    }

    public void illegalLanguageCharacter(int lineNumber, String character){
        System.out.println("Scanning Error found at line "+lineNumber+
                ". Written character (" + character +") is undefined in language");
    }

    public void illegalNumberSyntax(int lineNumber){
        System.out.println("Scanning Error found at line: "+lineNumber+
                ". Written number format is incorrect.");
    }

    public void illegalAndSyntax(int lineNumber){
        System.out.println("Scanning Error fount at line: "+ lineNumber +
                ". Ooops There is just single ampersand(&).");
    }

    public void illegalCommentSyntax(int lineNumber, String character){
        System.out.println("Scanning Error fount at line: "+lineNumber+
        ". Written character ("+character +")"+" is not allowed in comment syntax.");
    }

    public void duplicateDefinition(int lineNumber, String variableName){
        System.out.println("Variable named "+variableName+" on line: " + lineNumber+
                " had been defined before.");
    }

    public void undefinedVariable(int lineNumber, String variableName){
        System.out.println("Variable named "+ variableName + " on line: " + lineNumber+
        " is undefined. ");
    }
}

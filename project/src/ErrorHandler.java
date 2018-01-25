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

    public void illigalCommentSyntax(int lineNumber, String character){
        System.out.println("Scanning Error fount at line: "+lineNumber+
        ". Written character ("+character +")"+" is not allowed in comment syntax.");
    }
}

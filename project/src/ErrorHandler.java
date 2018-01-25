import java.util.Stack;

public class ErrorHandler {
    private Stack<Symbol> parseStack;


    public ErrorHandler(Stack<Symbol> parseStack) {
        this.parseStack = parseStack;
    }

    public void illigalLanguageCharacter(int lineNumber, String character){
        System.out.println("Scanning Error found at line "+lineNumber+
                "Written character (" + character +") is undefined in language");
    }
}

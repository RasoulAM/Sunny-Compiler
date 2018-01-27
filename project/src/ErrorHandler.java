import java.util.HashSet;
import java.util.Stack;

public class ErrorHandler {
    private Parser parser;


    public ErrorHandler(Parser parser) {
        this.parser = parser;
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

    public void missingToken(int lineNumber, String missing){
        System.out.println("Missing \"" + missing + "\" " + " on line: " + lineNumber);
    }

    public boolean emptyParseTable(int lineNumber, String lookahead){
        boolean synch = false;
        int index = parser.parseStack.size() - 1;
        while (parser.parseStack.get(index).type != Type.NON_TERMINAL) {
            index--;
        }
        HashSet<Symbol> h = parser.grammar.follow.get(parser.grammar.getNonTerminal(parser.parseStack.get(index).name));
        if (h.contains(parser.grammar.getTerminal(lookahead)))
            synch = true;
        if (synch){
            parser.parseStack.pop();
            System.out.println("Parsing Error: Missing " + " in line: " + lineNumber);
            return false;
        }
        else{
            System.out.println("Parsing Error: Extra input \"" + lookahead + "\" in line " + lineNumber);
            return true;
        }

    }

}

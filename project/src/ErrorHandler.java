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
        System.out.println("Parsing Error: Missing \"" + missing + "\" " + " on line: " + lineNumber);
    }

    public boolean emptyParseTable(int lineNumber, String lookahead, Symbol topOfParseStack){
        boolean synch = false;
//        int index = parser.parseStack.size() - 1;
//        while (parser.parseStack.get(index).type != Type.NON_TERMINAL) {
//            index--;
//        }
//        HashSet<Symbol> h = parser.grammar.follow.get(parser.grammar.getNonTerminal(parser.parseStack.get(index).name));
        HashSet<Symbol> h = parser.grammar.follow.get(parser.grammar.getNonTerminal(topOfParseStack.name));
        if (h.contains(parser.grammar.getTerminal(lookahead))) {
            synch = true;
        }
        if (synch){
//            Symbol poppedSymbol = parser.parseStack.remove(index);
            String result = parser.grammar.getMin(topOfParseStack);
            System.out.println("Parsing Error: Missing " + result + "in line: " + lineNumber);
            parser.parseStack.pop();
            return false;
        }
        else{
            System.out.println("Parsing Error: Extra input \"" + lookahead + "\" in line " + lineNumber);
            return true;
        }

    }

    // for semantic error checking
    public void operandNotMatch(int lineNumber, int operation, int handSide){
        String alert = "OOPS! Semantic Error occur on line: " + lineNumber;
        switch (operation){
            case 1: //add
                if (handSide == 0){
                    System.out.println(alert + " at the ADD operation. RHS is boolean!");
                } else {
                    System.out.println(alert + " at the ADD operation. LHS is boolean!");
                }
                break;
            case 2: // sub
                if (handSide == 0){
                    System.out.println(alert + " at the SUB operation. RHS is boolean!");
                } else {
                    System.out.println(alert + " at the SUB operation. LHS is boolean!");
                }
                break;
            case 3: // mult
                if (handSide == 0){
                    System.out.println(alert + " at the MULT operation. RHS is boolean!");
                } else {
                    System.out.println(alert + " at the MULT operation. LHS is boolean!");
                }
                break;
            case 4: // &&

                break;
            case 5: //assignment

                break;
            case 6: // lessThan

                break;
            case 7: // equal

                break;



        }
    }

}

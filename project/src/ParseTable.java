import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rasoul on 1/25/2018.
 */
public class ParseTable {

    private Grammar grammar;
    String file = "Grammar.grm";

    HashMap<Pair<Symbol, Symbol>, ArrayList<Symbol>> parseTable;


    ParseTable(){
        grammar = new Grammar();
        initializeParseTable();
    }

    private void initializeParseTable(){
        grammar.initializeFirstsAndFollows();
        for (Rule r : grammar.rules) {
            for (Symbol s : grammar.first.get(r.LHS)) {
                parseTable.put(new Pair<>(r.LHS, s), r.RHS);
            }
        }
    }

    public ArrayList<Symbol> get(Symbol topOfStack, String token){
        if (grammar.getTerminal(token) == null){
            return null;
        }
        Symbol t = grammar.getTerminal(token);
        return parseTable.get(new Pair<>(topOfStack, t));

    }

}

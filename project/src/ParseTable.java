import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Rasoul on 1/25/2018.
 */
public class ParseTable {

    private Grammar grammar;
    String file = "Grammar.grm";

    HashMap<Pair<Symbol, Symbol>, ArrayList<Symbol>> parseTable;


    ParseTable(){
        grammar = new Grammar();
        parseTable = new HashMap<>();
        initializeParseTable();
    }

    private void initializeParseTable(){
        grammar.initializeFirstsAndFollows();
        for (Rule r : grammar.rules) {
            for (Symbol s : grammar.first(r.RHS)) {
//                if (Objects.equals(s.name, "ϵ"))
//                    continue;
                parseTable.put(new Pair<>(r.LHS, s), r.RHS);
                System.out.println("In: " + r.LHS + ", " + s + " " + r.RHS);
            }
        }
//        Rule rr = grammar.getRule(0);
//        System.out.println("WTF:" + parseTable.get(new Pair<>(rr.LHS, grammar.getTerminal("class"))));
    }

    public ArrayList<Symbol> get(Symbol topOfStack, String token){
        if (grammar.getTerminal(token) == null){
            System.out.println("The token is not a token!");
            return null;
        }
        Symbol t = grammar.getTerminal(token);
        Symbol top = grammar.getNonTerminal(topOfStack.name);
//        System.out.println("Here: " + topOfStack + " " + t);
//        System.out.println("Here2: " + parseTable.get(new Pair<>(topOfStack, t)));
//        System.out.println(topOfStack == grammar.getRule(0).LHS);
//        System.out.println(t == grammar.getTerminal("class"));
//        System.out.println("Here3: " + parseTable.get(new Pair<>(grammar.getRule(0).LHS, grammar.getTerminal("class"))));
        return parseTable.get(new Pair<>(top, t));

    }

}

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


    // TODO: 1/26/2018 consider when epsilon in first(alpha)
    private void initializeParseTable(){
        grammar.initializeFirstsAndFollows();
        for (Rule r : grammar.rules) {
            ArrayList<Symbol> aaa = r.RHS;
            if ((Objects.equals(r.RHS.get(0).name, "ϵ"))){
                aaa = new ArrayList<>(r.RHS);
                aaa.remove(0);
            }
            for (Symbol s : grammar.first(r.RHS)) {
//                if (r.RHS.size() == 1 && Objects.equals(r.RHS.get(0).name, "ϵ")) {
//                    parseTable.put(new Pair<>(r.LHS, s), new ArrayList<Symbol>());
//                    System.out.println("In1: " + r.LHS + ", " + s + " " + r.RHS);
//                }
//                else {
//                }
                parseTable.put(new Pair<>(r.LHS, s), aaa);
//                System.out.println("In2: " + r.LHS + ", " + s + " " + aaa);
            }
            if (grammar.first(r.RHS).contains(grammar.epsilon)){
                for (Symbol s : grammar.follow.get(r.LHS)){
                    parseTable.put(new Pair<>(r.LHS, s), aaa);
                }
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

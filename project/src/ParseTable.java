/**
 * Created by Rasoul on 1/25/2018.
 */
public class ParseTable {

    private Grammar grammar;
    Integer[][] parseTable;
    String file = "Grammar.grm";

    ParseTable(){
        grammar = new Grammar();
    }

    public Rule get(Symbol topOfStack, String token){
        return grammar.getRule(1);
    }

}

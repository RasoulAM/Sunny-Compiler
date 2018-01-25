import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Scanner;

/**
 * Created by Rasoul on 1/24/2018.
 */
public class Grammar {

    private java.util.Scanner scanner;
    private HashMap<Integer, Rule> rules;
    private FileInputStream file;
    private ArrayList<Symbol> terminals;
    private ArrayList<Symbol> nonTerminals;
    private Symbol startSymbol;

    Grammar(String string){
        try {
            file = new FileInputStream("project/src/" + string);
            initialize_grammar();

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }

    }

    private void initialize_grammar() {
        scanner = new Scanner(file);
        nonTerminals = new ArrayList<>();
        terminals = new ArrayList<>();
        rules = new HashMap<>();
        boolean setStart = false;
        while(scanner.hasNext()){
            scanner.next();
            Symbol next = new Symbol(scanner.next(),Type.NON_TERMINAL);
            if (!nonTerminals.contains(next))
                nonTerminals.add(next);
            if (!setStart) {
                startSymbol = next;
                setStart = true;
            }
            scanner.nextLine();
        }

        scanner.close();

        try {
            scanner = new Scanner(new FileInputStream("project/src/" + "Grammar-Copy.grm"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int index = 1;
        while (scanner.hasNext()){
            // TODO: 1/25/2018 handle action symbols
            scanner.next();
            String lhs = scanner.next();
            if (getNonTerminal(lhs) == null) {
                System.out.println("Undefined Non terminal in line " + (new Integer(index)).toString());
                return;
            }
            Symbol newLHS = getNonTerminal(lhs);
            if (!Objects.equals(scanner.next(), "->")) {
                System.out.println("Grammar is not context free!! Check line " + index);
                return;
            }
            String[] rhs = scanner.nextLine().split(" ");
            ArrayList<Symbol> newRHS = new ArrayList<>();
            for (String s: rhs) {
                if (s.trim().length() == 0)
                    continue;
                if (getNonTerminal(s) != null){
                    newRHS.add(getNonTerminal(s));
                }
                else if (getTerminal(s) != null){
                    newRHS.add(getTerminal(s));
                }
                else {
                    Symbol t = new Symbol(s, Type.TERMINAL);
                    terminals.add(t);
                    newRHS.add(t);
                }
            }

            rules.put(index, new Rule(newLHS, newRHS));
            index++;
        }

        System.out.println(rules.size());
        for (int i = 1;i <= rules.size(); i++) {
            if (rules.get(i) != null)
                System.out.println(rules.get(i));
        }
        System.out.println();
    }


    private Symbol getNonTerminal(String s){
        for (Symbol n: nonTerminals) {
            if (Objects.equals(n.name, s))
                return n;
        }
        return null;
    }

    private Symbol getTerminal(String s){
        for (Symbol t: terminals) {
            if (Objects.equals(t.name, s))
                return t;
        }
        return null;
    }

    public Rule getRule(Integer num){
        return rules.get(num);
    }

    public static void main(String[] args) {
        new Grammar("Grammar.grm");
    }


}

class Rule{
    Symbol LHS;
    ArrayList<Symbol> RHS;

    Rule(Symbol LHS, ArrayList<Symbol> RHS){
        if (LHS.type != Type.NON_TERMINAL){
            System.out.println("LHS of a rule is not Non terminal!");
            return;
        }
        this.LHS = LHS;
        this.RHS = RHS;
    }

    @Override
    public String toString() {
        String str;
        str = LHS + " -> ";
        for (int i = 0; i < RHS.size(); i++){
            str += RHS.get(i) + " ";
        }
        return str;
    }
}

enum Type {
    TERMINAL, NON_TERMINAL, ACTION_SYMBOL
}

class Symbol {
    String name;
    Type type;

    Symbol(String name, Type type){
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Symbol))
            return false;
        return Objects.equals(((Symbol) obj).name, this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
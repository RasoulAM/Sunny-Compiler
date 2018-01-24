import java.awt.image.AreaAveragingScaleFilter;
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
    ArrayList<Terminal> terminals;
    private ArrayList<NonTerminal> nonTerminals;

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
        while(scanner.hasNext()){
            scanner.next();
            NonTerminal next = new NonTerminal(scanner.next());
            if (!nonTerminals.contains(next))
                nonTerminals.add(next);
            scanner.nextLine();
        }

        scanner.close();

        scanner = new Scanner(file);
        int index = 1;
        while (scanner.hasNext()){
            scanner.next();
            String lhs = scanner.next();
            if (getNonterminal(lhs) == null) {
                System.out.println("Undefined Non terminal in line " + (new Integer(index)).toString());
                return;
            }
            if (!Objects.equals(scanner.next(), "->")) {
                System.out.println("Grammar is not context free!!");
                return;
            }

            String[] rhs = scanner.nextLine().split(" ");

            rules.put(index, new Rule());
        }

        for (NonTerminal n: nonTerminals) {
            System.out.println(n.name);
        }
        System.out.println();
    }


    private NonTerminal getNonterminal(String s){
        for (NonTerminal n: nonTerminals) {
            if (Objects.equals(n.name, s))
                return n;
        }
        return null;
    }

    private Terminal getTerminal(String s){
        for (Terminal t: terminals) {
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
    SententialForm LHS;
    SententialForm RHS;

}

class SententialForm{
    ArrayList<Element> elements;


}

class Element{
    String name;

    Element(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Element))
            return false;
        return Objects.equals(((Element) obj).name, this.name);
    }
}

class Terminal extends Element{

    Terminal(String name) {
        super(name);
    }
}

class NonTerminal extends Element{

    NonTerminal(String name) {
        super(name);
    }
}

class ActionSymbol extends Element{

    ActionSymbol(String name) {
        super(name);
    }
}
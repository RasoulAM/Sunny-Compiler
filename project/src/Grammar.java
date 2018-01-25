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
    private ArrayList<Element> terminals;
    private ArrayList<Element> nonTerminals;

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
            Element next = new Element(scanner.next(),Type.NON_TERMINAL);
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
            if (getNonTerminal(lhs) == null) {
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

        for (Element n: nonTerminals) {
            System.out.println(n.name);
        }
        System.out.println();
    }


    private Element getNonTerminal(String s){
        for (Element n: nonTerminals) {
            if (Objects.equals(n.name, s))
                return n;
        }
        return null;
    }

    private Element getTerminal(String s){
        for (Element t: terminals) {
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

    SententialForm(String[] s){
        elements = new ArrayList<>();
    }
}

enum Type {
    TERMINAL, NON_TERMINAL, ACTION_SYMBOL
}

class Element{
    String name;
    Type type;

    Element(String name, Type type){
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Element))
            return false;
        return Objects.equals(((Element) obj).name, this.name);
    }
}
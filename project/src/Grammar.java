import java.io.*;
import java.util.*;
import java.util.Scanner;

/**
 * Created by Rasoul on 1/24/2018.
 */
public class Grammar {

    private java.util.Scanner scanner;
    ArrayList<Rule> rules;
    private FileInputStream file;
    private ArrayList<Symbol> terminals;
    private ArrayList<Symbol> nonTerminals;
    Symbol startSymbol;
    private String grammarFileName = "Grammar.grm";

    static Symbol epsilon;
    static Symbol endOfFile;

    HashMap<Symbol, HashSet<Symbol> > first;
    HashMap<Symbol, HashSet<Symbol> > follow;

    Grammar(){
        try {
            boolean isWindows = System.getProperty("os.name").contains("Windows");
            String grammarSrc;
            if (isWindows)
                grammarSrc = "project/src/" + grammarFileName;
            else
                grammarSrc = "./src/" + grammarFileName;
            file = new FileInputStream(grammarSrc);
            initialize_grammar();
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }

        epsilon = getTerminal("ϵ");
        endOfFile = getTerminal("EOF");

        initializeFirstsAndFollows();
    }

    public void initializeFirstsAndFollows(){
        // TODO: 1/26/2018 Write the loadConfigFile function to be able to use the config file
//        File config = new File("project/src/Grammar.cfg");
//        if (config.exists()){
//            loadConfigFile();
//            return;
//        }
        setFirsts();
        setFollows();
//        try {
//            setConfigFile();
//        } catch (IOException e) {
//            System.out.println("Error setting config file");
//        }
    }

    private void loadConfigFile() {
        Scanner s;
        try {
            boolean isWindows = System.getProperty("os.name").contains("Windows");
            String workingDir;
            if (isWindows)
                workingDir = "project";
            else
                workingDir = ".";
            s = new Scanner(new FileInputStream(workingDir + "/src/Grammar.cfg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scanner.nextLine();
        scanner.nextLine();
        scanner.nextLine();
        scanner.nextLine();


    }

    private void setConfigFile() throws IOException {
        BufferedWriter bw = null;
        boolean isWindows = System.getProperty("os.name").contains("Windows");
        String workingDir;
        if (isWindows)
            workingDir = "project";
        else
            workingDir = ".";
        bw = new BufferedWriter(new FileWriter(workingDir + "/src/Grammar.cfg"));
        bw.write(nonTerminals.size());
        bw.write("\n");
        for (Symbol s: nonTerminals) {
            bw.write(s.name + " ");
        }
        bw.write("\n");
        bw.write(terminals.size());
        bw.write("\n");
        for (Symbol s: terminals) {
            bw.write(s.name + " ");
        }
        bw.write("\n");
        for (Symbol s: terminals) {
            bw.write(s.name + " : ");
            for (Symbol f:first.get(s)) {
                bw.write(f.name + " ");
            }
            bw.write("\n");
        }
        for (Symbol s: nonTerminals) {
            bw.write(s.name + " : ");
            for (Symbol f:first.get(s)) {
                bw.write(f.name + " ");
            }
            bw.write("\n");
        }
//        for (Symbol s: terminals) {
//            bw.write(s.name + " : ");
//            for (Symbol f:follow.get(s)) {
//                bw.write(f.name + " ");
//            }
//            bw.write("\n");
//        }
        for (Symbol s: nonTerminals) {
            bw.write(s.name + " : ");
            for (Symbol f:follow.get(s)) {
                bw.write(f.name + " ");
            }
            bw.write("\n");
        }
        bw.close();
    }

    public void setFirsts(){
        first = new HashMap<>();
        HashSet<Symbol> epSet = new HashSet<>();
        epSet.add(epsilon);
        first.put(getTerminal("ϵ"), epSet);
        for (Symbol t: terminals) {
            HashSet h = new HashSet<>();
            h.add(t);
            first.put(t, h);
        }

        for (Symbol n: nonTerminals) {
            first.put(n, new HashSet<>());
        }

        boolean change = true;
        while(change){
            change = false;
            for (Rule r :rules) {
                change = change || normalizeFirst(r);
            }
        }

//        for (int i = 0; i < nonTerminals.size(); i++) {
//            System.out.println(nonTerminals.get(i).toString() + " " + first.get(nonTerminals.get(i)).toString());
//        }
    }

    private boolean normalizeFirst(Rule rule){
        int initialSize = first.get(rule.LHS).size();
        for (int i = 0; i < rule.RHS.size(); i++) {
            if (rule.RHS.get(i).type == Type.ACTION_SYMBOL)
                continue;
            HashSet<Symbol> s = new HashSet<>(first.get(rule.RHS.get(i)));
            s.remove(epsilon);
            first.get(rule.LHS).addAll(s);
            if (!first.get(rule.RHS.get(i)).contains(epsilon))
                return first.get(rule.LHS).size() != initialSize;
        }
        first.get(rule.LHS).add(epsilon);
        return first.get(rule.LHS).size() != initialSize;
    }

    public void setFollows(){
        follow = new HashMap<>();
        HashSet<Symbol> h = new HashSet<>();
        h.add(endOfFile);
        follow.put(startSymbol, h);

        for (Symbol s : nonTerminals) {
            follow.put(s,new HashSet<>());
        }

        boolean change = true;
        while(change){
            change = false;
            for (Rule r : rules) {
                change = change || normalizeFollow(r);
            }
        }
//        for (int i = 0; i < nonTerminals.size(); i++) {
//            System.out.println(nonTerminals.get(i).toString() + " " + follow.get(nonTerminals.get(i)).toString());
//        }
    }

    private boolean normalizeFollow(Rule rule) {
        boolean change = false;
        for (int i = 0; i < rule.RHS.size(); i++) {
            if (rule.RHS.get(i).type != Type.NON_TERMINAL)
                continue;
            int prevSize = follow.get(rule.RHS.get(i)).size();
            ArrayList<Symbol> a = new ArrayList<>(rule.RHS.subList(i + 1, rule.RHS.size()));
            follow.get(rule.RHS.get(i)).addAll(first(a));
            follow.get(rule.RHS.get(i)).remove(epsilon);
            if (first(a).contains(epsilon)){
                follow.get(rule.RHS.get(i)).addAll(follow.get(rule.LHS));
            }
            change = change || (prevSize != follow.get(rule.RHS.get(i)).size());
        }
        return change;
    }

    public HashSet<Symbol> first(ArrayList<Symbol> sentence){
        HashSet<Symbol> ans = new HashSet<>();

        for (int i = 0; i < sentence.size(); i++) {
            if (sentence.get(i).type == Type.ACTION_SYMBOL)
                continue;
            HashSet<Symbol> h = new HashSet<>(first.get(sentence.get(i)));
            h.remove(epsilon);
            ans.addAll(h);
            if (!first.get(sentence.get(i)).contains(epsilon)){
                return ans;
            }
        }
        ans.add(epsilon);
        return ans;
    }

    private void initialize_grammar() {
        ArrayList<String> theGrammar = new ArrayList<>();
        scanner = new Scanner(file);
        nonTerminals = new ArrayList<>();
        terminals = new ArrayList<>();
        rules = new ArrayList<>();
        boolean setStart = false;
        while(scanner.hasNext()){
            theGrammar.add(scanner.next());
            String temp = scanner.next();
            theGrammar.add(temp);
            Symbol next = new Symbol(temp,Type.NON_TERMINAL);
            if (!nonTerminals.contains(next))
                nonTerminals.add(next);
            if (!setStart) {
                startSymbol = next;
                setStart = true;
            }
            theGrammar.add(scanner.next());
            theGrammar.add(scanner.nextLine());
        }

        scanner.close();

        int index = 1;
        int arrIndex = 0;
        while (arrIndex < theGrammar.size()){
            arrIndex++;
            String lhs = theGrammar.get(arrIndex);
            arrIndex++;
            if (getNonTerminal(lhs) == null) {
                System.out.println("Undefined Non terminal in line " + (new Integer(index)).toString());
                return;
            }
            Symbol newLHS = getNonTerminal(lhs);
            String temp = theGrammar.get(arrIndex);
            arrIndex++;
            if (!Objects.equals(temp, "->")) {
                System.out.println("Grammar is not context free!! Check line " + index);
                return;
            }
            String[] rhs = theGrammar.get(arrIndex).split(" ");
            arrIndex++;
            ArrayList<Symbol> newRHS = new ArrayList<>();
            for (String s: rhs) {
                if (s.trim().length() == 0)
                    continue;
                if (getSymbol(s) != null){
                    newRHS.add(getSymbol(s));
                }
                else {
                    Symbol t = new Symbol(s);
                    if (t.type == Type.TERMINAL)
                        terminals.add(t);
                    newRHS.add(t);
                }
            }

            rules.add(new Rule(newLHS, newRHS));
            index++;
        }

//        System.out.println(rules.size());
//        for (int i = 0;i < rules.size(); i++) {
//            if (rules.get(i) != null)
//                System.out.println(rules.get(i));
//        }
//        System.out.println();
    }


    Symbol getNonTerminal(String s){
        for (Symbol n: nonTerminals) {
            if (Objects.equals(n.name, s))
                return n;
        }
        return null;
    }

    Symbol getTerminal(String s){
        for (Symbol t: terminals) {
            if (Objects.equals(t.name, s))
                return t;
        }
        return null;
    }

    Symbol getSymbol(String s){
        for (Symbol n: nonTerminals) {
            if (Objects.equals(n.name, s))
                return n;
        }
        for (Symbol t: terminals) {
            if (Objects.equals(t.name, s))
                return t;
        }
        return null;
    }

    public Rule getRule(Integer num){
        return rules.get(num);
    }

    public String getMin(Symbol symbol){
        if (symbol.type == Type.TERMINAL){
            return symbol.name;
        }
        ArrayList<Symbol> ans = new ArrayList<>();
        ans.add(symbol);
        int index = 0;
        while (index < ans.size()){
            if (ans.get(index).type == Type.TERMINAL)
                index++;
            else if (ans.get(index).type == Type.ACTION_SYMBOL){
                ans.remove(index);
            }
            else if (ans.get(index).type == Type.NON_TERMINAL){
                ans.remove(index);
                Rule r = rules.get(minExpand(symbol));
                for (int i = 0; i < r.RHS.size(); i++) {
                    if (r.RHS.get(i) != epsilon)
                        ans.add(index, r.RHS.get(i));
                }
            }
        }
        String str = "";
        for (int i = 0; i < ans.size(); i++) {
            str = str + ans.get(i) + " ";
        }
        return str;
    }

    private int minExpand(Symbol symbol){
        int ruleIndex = 0;
        int minRHS = 100;
        for (int i = 0; i < rules.size(); i++) {
            if (ruleSize(i) <= minRHS ){
                ruleIndex = i;
                minRHS = ruleSize(i);
            }
        }
        return ruleIndex;
    }

    private int ruleSize(int ruleIndex){
        Rule rule = rules.get(ruleIndex);
        int count = 0;
        for (Symbol s:rule.RHS) {
            if (s.type != Type.ACTION_SYMBOL)
                count++;
        }
        return count;
    }



    public static void main(String[] args) {
//        new Grammar();
//        FileWriter file = null;
//        try {
//            file = new FileWriter("new.txt");
//        } catch (IOException e) {
//            System.out.println("Here");
//            return;
//        }
//        BufferedWriter bufferedWriter = new BufferedWriter(file);
//        String s = "Hello";
//        try {
//            bufferedWriter.write(s);
//            bufferedWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
        if (this.name.charAt(0) == '#'){
            this.type = Type.ACTION_SYMBOL;
        }
    }

    Symbol(String name){
        this.name = name;
        if (this.name.charAt(0) == '#'){
            this.type = Type.ACTION_SYMBOL;
        }
        else
            this.type = Type.TERMINAL;
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

    public boolean is(String name){
        return Objects.equals(this.name, name);
    }
}
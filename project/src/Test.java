import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.management.BufferPoolMXBean;
import java.util.Stack;

public class Test {
    public static void main(String[] args) {
//        String letter = "[a-zA-Z]";
//        String digit = "[0-9]";
//        String other1 = "[^a-zA-Z0-9]";
//        String validChars = "[a-zA-Z0-9|+|-|&|<|,|*|/|\\s|(|)|{|}|;|.]";
//        System.out.println("".matches("[^\n]"));

        Stack<Integer> s = new Stack<>();
        s.push(1);
        s.push(2);
        System.out.println(s.get(0));

    }
}

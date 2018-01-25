import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.management.BufferPoolMXBean;

public class Test {
    public static void main(String[] args) {
        String letter = "[a-zA-Z]";
        String digit = "[0-9]";
        String other1 = "[^a-zA-Z0-9]";
        System.out.println("2".matches("[+|-]"));

    }
}

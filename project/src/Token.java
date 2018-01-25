public class Token {
    private String first;
    private String second;

    public Token(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Token{" +
                "first='" + first + '\'' +
                ", second='" + second + '\'' +
                '}';
    }
}

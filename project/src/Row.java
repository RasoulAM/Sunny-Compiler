import java.util.ArrayList;

public class Row {
    private String name;
    private Integer address;
    private String type;
    private ArrayList<String> functionArgsType = new ArrayList<>();

    public Row(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFunctionArgsType(ArrayList<String> functionArgsType) {
        this.functionArgsType = functionArgsType;
    }

    public String getName() {
        return name;
    }

    public Integer getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getFunctionArgsType() {
        return functionArgsType;
    }

    @Override
    public String toString() {
        return "Row{" +
                "name='" + name + '\'' +
                ", address=" + address +
                ", type='" + type + '\'' +
                ", functionArgsType=" + functionArgsType +
                '}';
    }
}

import java.util.ArrayList;

public class Row {
    private String name;
    private Integer address;
    private String type;
    private Integer retValueAddress;
    private ArrayList<FuncArg> functionArgs = new ArrayList<>();

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

    public void setFunctionArgsType(ArrayList<FuncArg> functionArgs) {
        this.functionArgs = functionArgs;
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

    public ArrayList<FuncArg> getFunctionArgsType() {
        return functionArgs;
    }

    public void addArg(String type, String memory){
        this.functionArgs.add(new FuncArg(type, memory));
    }

    @Override
    public String toString() {
        return "Row{" +
                "name='" + name + '\'' +
                ", address=" + address +
                ", type='" + type + '\'' +
                ", functionArgsType=" + functionArgs +
                '}';
    }
}

class FuncArg{
    private String type;
    private String memory;

    public FuncArg(String type, String memory) {
        this.type = type;
        this.memory = memory;
    }

    public String getType() {
        return type;
    }

    public String getMemory() {
        return memory;
    }
}
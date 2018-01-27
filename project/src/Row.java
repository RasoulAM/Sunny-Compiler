import java.util.ArrayList;

public class Row {
    private String name;
    private Integer address;
    private String type;
    private Integer retValueAddress;
    private String returnValueType;
    private Integer returnAddressAddress;
    private ArrayList<FuncArg> functionArgs = new ArrayList<>();

    
    public void setReturnAddressAddress(Integer returnAddressAddress) {
        this.returnAddressAddress = returnAddressAddress;
    }

    public void setFunctionArgs(ArrayList<FuncArg> functionArgs) {
        this.functionArgs = functionArgs;
    }

    public Integer getReturnAddressAddress() {
        return returnAddressAddress;
    }

    public ArrayList<FuncArg> getFunctionArgs() {
        return functionArgs;
    }

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

    public Integer getRetValueAddress() {
        return retValueAddress;
    }

    public String getReturnValueType() {
        return returnValueType;
    }

    public void setRetValueAddress(Integer retValueAddress) {
        this.retValueAddress = retValueAddress;
    }

    public void setReturnValueType(String returnValueType) {
        this.returnValueType = returnValueType;
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
import java.util.ArrayList;

public class SymbolTable {
    private SymbolTable parent;
    private ArrayList<Row> rows = new ArrayList<>();
    private String name;

    public SymbolTable(String name) {
        this.name = name;
    }

    public Integer findRowByName(String name){
        for (int i = 0; i <rows.size() ; i++) {
            if (rows.get(i).getName().equals(name)){
                return i;
            }
        }
        return -1;
    }

    public String searchThis(String name){
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getName().equals(name)){
                return this.name + " " + rows.get(i).getName() + " " + i;
            }
        }
        return null;
    }

    public String searchForToken(String name){
        String result = "";
        SymbolTable current = this;
        do {
            result = current.searchThis(name);
            if (result != null){
                return result;
            } else {
                current = current.getParent();
            }
        } while (current != null);
        return null;
    }

    public String tokenHandler(String name, boolean isDefinition, ErrorHandler errorHandler, int lineNumber){
        if (isDefinition && searchThis(name) == null){
            this.rows.add(new Row(name));
            return searchThis(name);
        } else if (isDefinition && searchThis(name) != null){
            errorHandler.duplicateDefinition(lineNumber, name);
            return searchThis(name);
        } else if (!isDefinition && searchForToken(name) != null){
            return searchForToken(name);
        } else if (!isDefinition && searchForToken(name) == null){
            errorHandler.undefinedVariable(lineNumber, name);
            this.rows.add(new Row(name));
            return searchThis(name);
        }
        return null;
    }

    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    public void setRows(ArrayList<Row> rows) {
        this.rows = rows;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "parent=" + parent +
                ", rows=" + rows +
                ", name='" + name + '\'' +
                '}';
    }
}

package gen;

import java.util.HashMap;
import java.util.LinkedList;

public class BinaryType {

    public enum Value {POINTER, ARRAY, STRUCT, NUMBER, VOID}

    public Value stores;
    public int size;
    public int unitSize;
    private HashMap<Integer, BinaryType> fields;
    private HashMap<String, Integer> fieldNames;
    private String name;

    public BinaryType(int size, Value stores) {
        this.size = size;
        this.unitSize = this.size;
        this.stores = stores;
    }

    public BinaryType(BinaryType element, int elementCount) {
        int padding = 0;
        while ((element.size*elementCount+padding) % 4 != 0) {
            padding += 1;
        }
        this.unitSize = element.size;
        this.size = element.size*elementCount+padding;
        fields = new HashMap<>();
        for (int i=0;i<elementCount;i+=element.size) fields.put(i, element);
        this.stores = Value.ARRAY;
    }

    public BinaryType(LinkedList<BinaryType> fieldTypes) {
        fields = new HashMap<>();
        fieldNames = new HashMap<>();
        this.size = 0;
        for (BinaryType t : fieldTypes) {
            fields.put(size, t);
            if (t.getName() != null) {
                fieldNames.put(t.getName(), size);
            }
            int padding = 0;
            while ((t.size+padding) % 4 != 0) {
                padding += 1;
            }
            size += t.size+padding;
        }
        this.unitSize = this.size;
        this.stores = Value.STRUCT;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public int fieldNameOffset(String id) {
        return fieldNames.get(id);
    }
    public BinaryType field(int i) {
        return fields.get(i);
    }
}

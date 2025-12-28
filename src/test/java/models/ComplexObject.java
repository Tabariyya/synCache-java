package models;

public class ComplexObject {
    private String name;
    private int value;
    private String[] data;
    private User nested;

    public ComplexObject() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public User getNested() {
        return nested;
    }

    public void setNested(User nested) {
        this.nested = nested;
    }
}

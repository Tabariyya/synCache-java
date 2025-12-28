package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ComplexPojo {
    private List<String> items = new ArrayList<>();
    private HashMap<String, Integer> counts = new HashMap<>();
    private SimplePojo nested;

    public ComplexPojo() {
    }

    public ComplexPojo(List<String> items, HashMap<String, Integer> counts, SimplePojo nested) {
        this.items = items != null ? items : new ArrayList<>();
        this.counts = counts != null ? counts : new HashMap<>();
        this.nested = nested;
    }

    // Getters and setters
    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public HashMap<String, Integer> getCounts() {
        return counts;
    }

    public void setCounts(HashMap<String, Integer> counts) {
        this.counts = counts != null ? counts : new HashMap<>();
    }

    public SimplePojo getNested() {
        return nested;
    }

    public void setNested(SimplePojo nested) {
        this.nested = nested;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexPojo that = (ComplexPojo) o;
        return items.equals(that.items) &&
                counts.equals(that.counts) &&
                nested.equals(that.nested);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, counts, nested);
    }
}
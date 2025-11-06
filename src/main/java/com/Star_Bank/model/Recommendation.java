package com.Star_Bank.model;
import java.util.Objects;
import java.util.UUID;

public class Recommendation {
    private String name;
    private String id;
    private String text;

    public Recommendation() {}

    public Recommendation(String name, String id, String text) {
        this.name = name;
        this.id = id;
        this.text = text;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Recommendation that = (Recommendation) o;
        return Objects.equals(name, that.name) && Objects.equals(id, that.id) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, text);
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
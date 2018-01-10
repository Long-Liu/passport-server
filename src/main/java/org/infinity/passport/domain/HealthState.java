package org.infinity.passport.domain;


import java.io.Serializable;
import java.util.Objects;

public class HealthState implements Serializable {

    private static final long serialVersionUID = 4252202444338082990L;

    private String name;

    private String color;

    public HealthState() {
    }

    public HealthState(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthState that = (HealthState) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }

    @Override
    public String toString() {
        return "HealthState{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
